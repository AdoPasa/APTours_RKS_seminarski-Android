package ba.com.apdesign.aptours;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.net.ResponseCache;
import java.util.regex.Pattern;

import helpers.RetrofitBuilder;
import helpers.SpinnerHelper;
import helpers.ValidationHelper;
import models.GenderListView;
import models.Register;
import okhttp3.ResponseBody;
import repositories.AccessRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    Spinner genderSpinner;
    EditText firstNameInput;
    EditText lastNameInput;
    EditText emailInput;
    EditText phoneInput;
    EditText usernameInput;
    EditText passwordInput;
    EditText confirmPasswordInput;
    ProgressBar registerProgress;
    boolean registerInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bindControls();

        SpinnerHelper.populateGender(this, genderSpinner);
    }

    //Event listeners
    public void onRegisterButtonClick(View v) {
        if(registerInProgress)
            return;

        if(!fieldsHaveErrors()) {
            registerInProgress = true;
            registerProgress.setVisibility(View.VISIBLE);

            Register model = new Register();
            model.FirstName = firstNameInput.getText().toString();
            model.LastName = lastNameInput.getText().toString();
            model.Gender = ((GenderListView)genderSpinner.getSelectedItem()).Value;
            model.Username = usernameInput.getText().toString();
            model.Email = emailInput.getText().toString();
            model.Phone = phoneInput.getText().toString();
            model.Password = passwordInput.getText().toString();

            AccessRepository accessRepository = RetrofitBuilder.Build(this).create(AccessRepository.class);
            Call<ResponseBody> call = accessRepository.Register(model);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.SuccessfullyRegistered), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        if(response.code() == 550)
                            emailInput.setError(getString(R.string.ErrMsgEmailInUse));
                        else if(response.code() == 551)
                            phoneInput.setError(getString(R.string.ErrMsgPhoneInUse));
                        else if(response.code() == 552)
                            usernameInput.setError(getString(R.string.ErrMsgUsernameInUse));
                        else if(response.code() == 400)
                            Toast.makeText(RegisterActivity.this, getString(R.string.ErrMsgEnteredDataIsNotValid), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(RegisterActivity.this, getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                    }

                    registerInProgress = false;
                    registerProgress.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                    registerInProgress = false;
                    registerProgress.setVisibility(View.GONE);
                }
            });
        }
    }

    public boolean fieldsHaveErrors() {
        boolean haveErrors = false;

        if(!ValidationHelper.isFirtsnameValid(firstNameInput.getText().toString())) {
            firstNameInput.setError(getString(R.string.ErrMsgFirstnameNotValid));
            haveErrors = true;
        }

        if(!ValidationHelper.isLastnameValid(lastNameInput.getText().toString())) {
            lastNameInput.setError(getString(R.string.ErrMsgLastnameNotValid));
            haveErrors = true;
        }

        if(!ValidationHelper.isGenderValid(this, ((GenderListView)genderSpinner.getSelectedItem()).Value)) {
            TextView errorText = (TextView)genderSpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText(getString(R.string.ErrMsgGenerNotValid));//changes the selected item text to this
            haveErrors = true;
        }

        if(!ValidationHelper.isEmailValid(emailInput.getText().toString())) {
            emailInput.setError(getString(R.string.ErrMsgEmailNotValid));
            haveErrors = true;
        }

        if(!ValidationHelper.isPhoneValid(phoneInput.getText().toString())) {
            phoneInput.setError(getString(R.string.ErrMsgPhoneNotValid));
            haveErrors = true;
        }

        if(!ValidationHelper.isUsernameValid(usernameInput.getText().toString())) {
            usernameInput.setError(getString(R.string.ErrMsgUsernameNotValid));
            haveErrors = true;
        }

        if(!ValidationHelper.isPasswordValid(passwordInput.getText().toString())) {
            passwordInput.setError(getString(R.string.ErrMsgPasswordNotValid));
            haveErrors = true;
        }
        else if(!confirmPasswordInput.getText().toString().equals(passwordInput.getText().toString())) {
            confirmPasswordInput.setError(getString(R.string.ErrMsgPasswordNotMatch));
            haveErrors = true;
        }

        return haveErrors;
    }

    public void onBackToLoginButtonClick(View v) { finish(); }

    //Helpers
    private void bindControls() {
        RelativeLayout registerActivity = findViewById(R.id.RegisterActivity);

        genderSpinner  = registerActivity.findViewById(R.id.GenderSpinner);
        firstNameInput = registerActivity.findViewById(R.id.FirstNameInput);
        lastNameInput  = registerActivity.findViewById(R.id.LastNameInput);
        emailInput     = registerActivity.findViewById(R.id.EmailInput);
        phoneInput     = registerActivity.findViewById(R.id.PhoneInput);
        usernameInput  = registerActivity.findViewById(R.id.UsernameInput);
        passwordInput  = registerActivity.findViewById(R.id.PasswordInput);
        confirmPasswordInput  = registerActivity.findViewById(R.id.ConfirmPasswordInput);
        registerProgress  = registerActivity.findViewById(R.id.RegisterProgress);
    }
}
