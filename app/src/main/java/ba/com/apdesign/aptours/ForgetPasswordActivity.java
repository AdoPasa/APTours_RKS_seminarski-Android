package ba.com.apdesign.aptours;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.regex.Pattern;

import helpers.RetrofitBuilder;
import helpers.ValidationHelper;
import okhttp3.ResponseBody;
import repositories.AccessRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {
    EditText emailInput;
    ProgressBar forgetPasswordProgress;
    RelativeLayout forgetPasswordContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        bindControls();
    }

    private void bindControls() {
        forgetPasswordContainer = findViewById(R.id.ForgetPasswordContainer);

        emailInput = forgetPasswordContainer.findViewById(R.id.EmailInput);
        forgetPasswordProgress = forgetPasswordContainer.findViewById(R.id.ForgetPasswordProgress);
    }

    public void onRequestCodeButtonClick(View v) {
        if(!ValidationHelper.isEmailValid(emailInput.getText().toString())) {
            emailInput.setError(getString(R.string.ErrMsgEmailNotValid));
        }

        AccessRepository accessRepository = RetrofitBuilder.Build(this).create(AccessRepository.class);
        Call<ResponseBody> call = accessRepository.SendResetPasswordCode(emailInput.getText().toString());

        forgetPasswordProgress.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                forgetPasswordProgress.setVisibility(View.GONE);

                if(response.isSuccessful()) {
                    Intent intent = new Intent(ForgetPasswordActivity.this, ResetPasswordActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);

                    Toast.makeText(ForgetPasswordActivity.this, getString(R.string.ResetPasswordCodeSuccessfullySended), Toast.LENGTH_SHORT).show();
                }
                else if(response.code() == 404)
                    Toast.makeText(ForgetPasswordActivity.this, getString(R.string.ErrMsgUserAccountNotFound), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ForgetPasswordActivity.this, getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                forgetPasswordProgress.setVisibility(View.GONE);
                Toast.makeText(ForgetPasswordActivity.this, getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackToLoginButtonClick(View v) { finish(); }
}
