package ba.com.apdesign.aptours;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import helpers.GeneralHelper;
import helpers.RetrofitBuilder;
import helpers.SpinnerHelper;
import helpers.ValidationHelper;
import models.Access;
import models.GenderListView;
import models.UpdateProfile;
import models.Users;
import okhttp3.ResponseBody;
import repositories.UsersRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText usernameEditText;
    private Spinner genderSpinner;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText newPasswordEditText;
    private EditText confirmNewPasswordEditText;
    private EditText currentPasswordEditText;

    private ProgressBar profilePhotoLoadingSpinner;
    private AppCompatImageView profilePhotoImageView;
    private AppCompatImageView _changePhotoImageView;
    private TextView numberOfReviewsTextView;
    private TextView numberOfToursTextView;
    private ArrayList<GenderListView> spinnerItems;
    private boolean dataLoadedFlag = false;

    private Button saveButton;
    private UsersRepository _usersService;
    private Access _access;

    public AccountFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        _access = GeneralHelper.readAccessSharedPreferences(getActivity());
        _usersService = RetrofitBuilder.Build(getContext()).create(UsersRepository.class);

        BindControls(view);
        LoadData();

        saveButton.setOnClickListener(_saveButtonOnClickListner());
        _changePhotoImageView.setOnClickListener(_changePhotoOnClickListner());

        return view;
    }

    //Events
    private View.OnClickListener _saveButtonOnClickListner() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dataLoadedFlag)
                    return;

                firstNameEditText.setError(null);
                lastNameEditText.setError(null);
                emailEditText.setError(null);
                phoneEditText.setError(null);
                newPasswordEditText.setError(null);
                confirmNewPasswordEditText.setError(null);
                currentPasswordEditText.setError(null);

                boolean userModelValid = true;
                final UpdateProfile user = new UpdateProfile();

                user.FirstName = firstNameEditText.getText().toString();
                user.LastName = lastNameEditText.getText().toString();
                user.Gender = ((GenderListView) genderSpinner.getSelectedItem()).Value;
                user.Email = emailEditText.getText().toString();
                user.Phone = phoneEditText.getText().toString();
                user.NewPassword = newPasswordEditText.getText().toString();
                user.ConfirmNewPassword = confirmNewPasswordEditText.getText().toString();
                user.CurrentPassword = currentPasswordEditText.getText().toString();

                if (!ValidationHelper.isFirtsnameValid(user.FirstName)) {
                    userModelValid = false;
                    firstNameEditText.setError(getActivity().getString(R.string.ErrMsgFirstnameNotValid));
                }

                if (!ValidationHelper.isLastnameValid(user.FirstName)) {
                    userModelValid = false;
                    lastNameEditText.setError(getActivity().getString(R.string.ErrMsgLastnameNotValid));
                }

                if (!ValidationHelper.isGenderValid(getContext(), user.Gender)) {
                    userModelValid = false;

                    TextView errorText = (TextView) genderSpinner.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText(getString(R.string.ErrMsgGenerNotValid));//changes the selected item text to this
                }

                if (!ValidationHelper.isEmailValid(user.Email)) {
                    userModelValid = false;
                    emailEditText.setError(getActivity().getString(R.string.ErrMsgEmailNotValid));
                }

                if (!ValidationHelper.isPhoneValid(user.Phone)) {
                    userModelValid = false;
                    phoneEditText.setError(getActivity().getString(R.string.ErrMsgPhoneNotValid));
                }

                if (user.NewPassword != null && !user.NewPassword.equals("")) {
                    if (!ValidationHelper.isPasswordValid(user.NewPassword)) {
                        newPasswordEditText.setError(getString(R.string.ErrMsgPasswordNotValid));
                        userModelValid = false;
                    } else if (!user.NewPassword.equals(user.ConfirmNewPassword)) {
                        confirmNewPasswordEditText.setError(getString(R.string.ErrMsgPasswordNotMatch));
                        userModelValid = false;
                    }
                }

                if (!ValidationHelper.isPasswordValid(user.CurrentPassword)) {
                    currentPasswordEditText.setError(getString(R.string.ErrMsgPasswordRequired));
                    userModelValid = false;
                }


                if (userModelValid) {
                    Call<ResponseBody> call = _usersService.UpdateProfile(_access.AuthToken, user);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getActivity(), getContext().getText(R.string.ProfileSuccessfullyUpdated), Toast.LENGTH_SHORT).show();

                                _access.FirstName = user.FirstName;
                                _access.LastName = user.LastName;
                                _access.Email = user.Email;
                                GeneralHelper.fillAccessSharedPreferences(getActivity(), _access);

                                NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                                View header = navigationView.getHeaderView(0);

                                TextView navHeaderFullName = (TextView) header.findViewById(R.id.navHeaderFullName);
                                TextView navHeaderEmail = (TextView) header.findViewById(R.id.navHeaderEmail);
                                navHeaderFullName.setText(_access.FirstName + " " + _access.LastName);
                                navHeaderEmail.setText(_access.Email);
                            } else if (response.code() == 550)
                                emailEditText.setError(getActivity().getString(R.string.ErrMsgEmailInUse));
                            else if (response.code() == 551)
                                emailEditText.setError(getActivity().getString(R.string.ErrMsgPhoneInUse));
                            else if (response.code() == 540)
                                currentPasswordEditText.setError(getString(R.string.ErrMsgIncorrectPassword));
                            else
                                Toast.makeText(getActivity(), getContext().getText(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getActivity(), getContext().getText(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
    }

    private View.OnClickListener _changePhotoOnClickListner() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                //Handling is managed in the HomeActivity class
                getActivity().startActivityForResult(i, HomeActivity.RESULT_CHANGE_PROFILE_PICTURE);
            }
        };
    }
    //END OF EVENTS

    //HELPERS
    private void LoadData() {
        spinnerItems = SpinnerHelper.populateGender(getContext(), genderSpinner);

        if(_access.ProfilePhoto != null) {
            profilePhotoLoadingSpinner.setVisibility(View.VISIBLE);

            Ion.with(this).load(getString(R.string.ApiRoot) + _access.ProfilePhoto).withBitmap().asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if(result != null)
                                profilePhotoImageView.setImageBitmap(result);
                            else
                                profilePhotoImageView.setImageResource(R.drawable.default_profile);

                            profilePhotoLoadingSpinner.setVisibility(View.GONE);
                        }
                    });
        }
        else
            profilePhotoImageView.setImageResource(R.drawable.default_profile);

        firstNameEditText.setText(_access.FirstName);
        lastNameEditText.setText(_access.LastName);
        usernameEditText.setText(_access.Username);
        emailEditText.setText(_access.Email);

        Call<Users> call = _usersService.GetProfile(_access.AuthToken);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if(response.isSuccessful()) {
                    Users user = response.body();

                    for(int i = 0; i < spinnerItems.size(); i++) {
                        if(spinnerItems.get(i).Value.equals(user.Gender)) {
                            genderSpinner.setSelection(i);
                            break;
                        }
                    }

                    phoneEditText.setText(user.Phone);
                    numberOfReviewsTextView.setText(String.valueOf(user.NumberOfReviews));
                    numberOfToursTextView.setText(String.valueOf(user.NumberOfTours));

                    dataLoadedFlag = true;
                }
                else
                    Toast.makeText(getActivity(), getContext().getText(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(getActivity(), getContext().getText(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void BindControls(View view) {
        firstNameEditText = (EditText) view.findViewById(R.id.FirstNameEditText);
        lastNameEditText = (EditText) view.findViewById(R.id.LastNameEditText);
        usernameEditText = (EditText) view.findViewById(R.id.UsernameEditText);
        genderSpinner = (Spinner) view.findViewById(R.id.GenderSpinner);
        emailEditText = (EditText) view.findViewById(R.id.EmailEditText);
        phoneEditText = (EditText) view.findViewById(R.id.PhoneEditText);
        newPasswordEditText = (EditText) view.findViewById(R.id.NewPasswordEditText);
        confirmNewPasswordEditText = (EditText) view.findViewById(R.id.ConfirmNewPasswordEditText);
        currentPasswordEditText = (EditText) view.findViewById(R.id.CurrentPasswordEditText);

        profilePhotoLoadingSpinner = (ProgressBar) view.findViewById(R.id.ProfilePhotoLoadingSpinner);
        profilePhotoImageView = (AppCompatImageView) view.findViewById(R.id.ProfilePhotoImageView);
        _changePhotoImageView = (AppCompatImageView) view.findViewById(R.id.ChangePhotoIcon);
        numberOfReviewsTextView = (TextView) view.findViewById(R.id.NumberOfReviewsTextView);
        numberOfToursTextView = (TextView) view.findViewById(R.id.NumberOfToursTextView);

        saveButton = (Button) view.findViewById(R.id.SaveButton);
    }
    //END OF HELPERS
}