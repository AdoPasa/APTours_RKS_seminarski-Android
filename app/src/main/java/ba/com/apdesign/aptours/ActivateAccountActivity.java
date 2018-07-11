package ba.com.apdesign.aptours;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.regex.Pattern;

import helpers.GeneralHelper;
import helpers.RetrofitBuilder;
import models.Access;
import models.ActivateAccount;
import models.Login;
import okhttp3.ResponseBody;
import repositories.AccessRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivateAccountActivity extends AppCompatActivity {
    //API
    Retrofit retrofit;
    AccessRepository accessRepository;

    //from extras
    String username;
    String password;

    //controls
    EditText activationCodeEditText;
    ProgressBar activateAccountProgress;
    Button activateAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_account);
        bindElements();

        Bundle extras = getIntent().getExtras();
        username = extras.getString(getString(R.string.UsernameKey));
        password = extras.getString(getString(R.string.PasswordKey));

        accessRepository = RetrofitBuilder.Build(this).create(AccessRepository.class);

        activateAccountButton.setOnClickListener(OnActivateAccountButtonClick());
    }

    public View.OnClickListener OnActivateAccountButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String activationCodeText = activationCodeEditText.getText().toString();

                activationCodeEditText.setError(null);

                if(!isActivationCodeValid(activationCodeText)) {
                    activationCodeEditText.setError(getString(R.string.ErrMsgActivationCodeNotValid));
                    activationCodeEditText.requestFocus();
                    return;
                }

                activateAccountProgress.setVisibility(View.VISIBLE);

                ActivateAccount model = new ActivateAccount();
                model.Username = username;
                model.ActivationCode = activationCodeText;

                Call<ResponseBody> call = accessRepository.ActivateAccount(model);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()) {
                            Login model = new Login();

                            String android_id = Settings.Secure.getString(ActivateAccountActivity.this.getContentResolver(),Settings.Secure.ANDROID_ID);
                            model.Username = username;
                            model.Password = password;
                            model.DeviceToken = android_id;

                            model.InfoVersionRelease = Build.VERSION.RELEASE;
                            model.InfoDevice = Build.DEVICE;
                            model.InfoModel = Build.MODEL;
                            model.InfoBrand = Build.BRAND;
                            model.InfoManufacturer = Build.MANUFACTURER;
                            model.InfoProduct = Build.PRODUCT;
                            model.AndroidSerialOrID = Build.SERIAL;

                            Call<Access> loginCall = accessRepository.Login(model);

                            loginCall.enqueue(new Callback<Access>() {
                                @Override
                                public void onResponse(Call<Access> call, Response<Access> response) {
                                    if(response.isSuccessful()) {
                                        Access responseData = response.body();

                                        if(responseData == null)
                                            startLoginActivity();
                                        else
                                            startHomeActivity(responseData);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Access> call, Throwable t) {
                                    startLoginActivity();
                                }
                            });
                        }
                        else if(response.code() == 404) {
                            Toast.makeText(ActivateAccountActivity.this, getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                            startLoginActivity();
                        }
                        else {
                            activationCodeEditText.setError(getString(R.string.ErrMsgActivationCodeNotValid));
                            activationCodeEditText.requestFocus();
                            activateAccountProgress.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ActivateAccountActivity.this, getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                        activateAccountProgress.setVisibility(View.GONE);
                    }
                });
            }
        };
    }

    private void bindElements() {
        activationCodeEditText = findViewById(R.id.ActivationCode);
        activateAccountProgress = findViewById(R.id.ActivateAccountProgress);
        activateAccountButton = findViewById(R.id.ActivateAccountButton);
    }

    private boolean isActivationCodeValid(String password) {
        Pattern regexPattern = Pattern.compile("^[a-zA-Z0-9]{10}$");
        return regexPattern.matcher(password).matches();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(ActivateAccountActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void startHomeActivity(Access model) {
        GeneralHelper.fillAccessSharedPreferences(this, model);

        Intent intent = new Intent(ActivateAccountActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}
