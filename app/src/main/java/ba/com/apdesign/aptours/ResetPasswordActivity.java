package ba.com.apdesign.aptours;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import helpers.AppConfiguration;
import helpers.RetrofitBuilder;
import helpers.ValidationHelper;
import models.ResetPassword;
import okhttp3.ResponseBody;
import repositories.AccessRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    RelativeLayout resetPasswordContainer;
    EditText newPassword;
    EditText confirmNewPassword;
    EditText resetPasswordCode;
    ProgressBar resetPasswordProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        bindControls();
    }

    private void bindControls() {
        resetPasswordContainer = findViewById(R.id.ResetPasswordContainer);
        newPassword = resetPasswordContainer.findViewById(R.id.NewPassword);
        confirmNewPassword = resetPasswordContainer.findViewById(R.id.ConfirmNewPassword);
        resetPasswordCode = resetPasswordContainer.findViewById(R.id.ResetPasswordCode);
        resetPasswordProgress = resetPasswordContainer.findViewById(R.id.ResetPasswordProgress);
    }

    public void onResetPasswordButtonClick(View v) {
        if(!fieldsHaveErrors()) {
            resetPasswordProgress.setVisibility(View.VISIBLE);

            ResetPassword model = new ResetPassword();
            model.NewPassword = newPassword.getText().toString();
            model.ConfirmNewPassword = confirmNewPassword.getText().toString();
            model.ResetPasswordToken = resetPasswordCode.getText().toString();

            AccessRepository accessRepository = RetrofitBuilder.Build(this).create(AccessRepository.class);
            Call<ResponseBody> call = accessRepository.ResetPassword(model);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        finish();
                        Toast.makeText(ResetPasswordActivity.this, getString(R.string.PasswordSuccessfullyReseted), Toast.LENGTH_SHORT).show();
                    }
                    else if(response.code() == 456)
                        Toast.makeText(ResetPasswordActivity.this, getString(R.string.ErrMsgAccountNotActive), Toast.LENGTH_SHORT).show();
                    else if(response.code() == 554)
                        Toast.makeText(ResetPasswordActivity.this, getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();

                    resetPasswordProgress.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                    resetPasswordProgress.setVisibility(View.GONE);
                }
            });
        }
    }

    private boolean fieldsHaveErrors() {
        boolean haveErrors = false;
        if(!ValidationHelper.isPasswordValid(newPassword.getText().toString())) {
            newPassword.setError(getString(R.string.ErrMsgPasswordNotValid));
            haveErrors = true;
        }

        if(!confirmNewPassword.getText().toString().equals(newPassword.getText().toString())) {
            confirmNewPassword.setError(getString(R.string.ErrMsgPasswordNotMatch));
            haveErrors = true;
        }

        if(!ValidationHelper.isResetPasswordTokenValid(resetPasswordCode.getText().toString())) {
            resetPasswordCode.setError(getString(R.string.ErrMsgResetPasswordTokenNotValid));
            haveErrors = true;
        }

        return haveErrors;
    }

    public void onBackToLoginButtonClick(View v) { finish(); }
}
