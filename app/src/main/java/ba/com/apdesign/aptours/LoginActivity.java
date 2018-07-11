package ba.com.apdesign.aptours;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import helpers.ConnectionHelper;
import helpers.GeneralHelper;
import helpers.RetrofitBuilder;
import models.Access;
import models.Login;
import okhttp3.ResponseBody;
import repositories.AccessRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private EditText passwordInput;
    private EditText usernameInput;
    private ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindContols();

        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    tryToLogin();
                    return true;
                }
                return false;
            }
        });
    }

    //EventListeners
    public void onLoginButtonClick(View v) {
        tryToLogin();
    }

    public void onRegisterButtonClick(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void onForgetPasswordButtonClick(View v) {
        startActivity(new Intent(this, ForgetPasswordActivity.class));
    }

    //Helpers
    private void bindContols() {
        usernameInput = (EditText)findViewById(R.id.usernameInput);
        passwordInput = (EditText)findViewById(R.id.passwordInput);
        loginProgress = (ProgressBar)findViewById(R.id.loginProgress);
    }

    private boolean isPasswordValid(String password) {
        Pattern regexPattern = Pattern.compile("^((?=.*[a-zA-ZšđčćžŠĐČŽĆ])(?=.*\\d)).{6,}$");
        return regexPattern.matcher(password).matches();
    }

    private void startHomeActivity(Access model) {
        GeneralHelper.fillAccessSharedPreferences(this, model);

        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    private void tryToLogin() {
        String email = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        boolean forceFocus = false;
        View focusView = null;

        usernameInput.setError(null);
        passwordInput.setError(null);

        if (!isPasswordValid(password)) {
            passwordInput.setError(getString(R.string.ErrMsgInvalidPassword));
            focusView = passwordInput;
            forceFocus = true;
        }

        if (TextUtils.isEmpty(email)) {
            usernameInput.setError(getString(R.string.ErrMsgUsernameOrEmailReq));
            focusView = usernameInput;
            forceFocus = true;
        }

        if (forceFocus)
            focusView.requestFocus();
        else {
            if(!ConnectionHelper.isInternetAvailable(this)) {
                Toast.makeText(this, getString(R.string.ErrMsgNoInternetConnection), Toast.LENGTH_SHORT).show();
                return;
            }

            final Login model = new Login();

            loginProgress.setVisibility(View.VISIBLE);


            String android_id = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
            model.Username = usernameInput.getText().toString();
            model.Password = passwordInput.getText().toString();
            model.DeviceToken = android_id;

            model.InfoVersionRelease = Build.VERSION.RELEASE;
            model.InfoDevice = Build.DEVICE;
            model.InfoModel = Build.MODEL;
            model.InfoBrand = Build.BRAND;
            model.InfoManufacturer = Build.MANUFACTURER;
            model.InfoProduct = Build.PRODUCT;
            model.AndroidSerialOrID = Build.SERIAL;

            AccessRepository accessRepository = RetrofitBuilder.Build(this).create(AccessRepository.class);
            Call<Access> call = accessRepository.Login(model);

                call.enqueue(new Callback<Access>() {
                    @Override
                    public void onResponse(Call<Access> call, Response<Access> response) {
                        if(response.isSuccessful()) {
                            Access responseData = response.body();

                            if(responseData == null)
                                Toast.makeText(LoginActivity.this, getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                            else
                                startHomeActivity(responseData);
                        }
                        else {
                            ResponseBody responseData = response.errorBody();
                            int i = response.code();

                            if(response.code() == 404)
                                Toast.makeText(LoginActivity.this, getString(R.string.ErrMsgWrongUsernameOrPassword), Toast.LENGTH_SHORT).show();
                            else if(response.code() == 300) {
                                Intent intent = new Intent(LoginActivity.this, ActivateAccountActivity.class);
                                intent.putExtra(getString(R.string.UsernameKey), model.Username);
                                intent.putExtra(getString(R.string.PasswordKey), model.Password);

                                startActivity(intent);
                            }
                            else if(response.code() == 401)
                                Toast.makeText(LoginActivity.this, getString(R.string.ErrMsgAccountNotActive), Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(LoginActivity.this, getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                        }

                        loginProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<Access> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                        loginProgress.setVisibility(View.GONE);
                    }
                });
        }
    }
}
