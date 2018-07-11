package helpers;

import android.content.Context;

import java.util.regex.Pattern;

import ba.com.apdesign.aptours.R;
import models.GenderListView;

public class ValidationHelper {
    public static boolean isFirtsnameValid(String firstname) {
        Pattern regexPattern = Pattern.compile("^.{3,}$");
        return regexPattern.matcher(firstname).matches();
    }

    public static boolean isLastnameValid(String lastname) {
        Pattern regexPattern = Pattern.compile("^.{3,}$");
        return regexPattern.matcher(lastname).matches();
    }

    public static boolean isGenderValid(Context context, String genderValue) {
        Pattern regexPattern = Pattern.compile("^[" + context.getString(R.string.MaleShort) + context.getString(R.string.FemaleShort) + "]$");
        return regexPattern.matcher(genderValue).matches();
    }

    public static boolean isEmailValid(String email) {
        Pattern regexPattern = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
        return regexPattern.matcher(email).matches();
    }

    public static boolean isPhoneValid(String phone) {
        Pattern regexPattern = Pattern.compile("^[+\\-\\d\\\\]{6,}$");
        return regexPattern.matcher(phone).matches();
    }

    public static boolean isUsernameValid(String username) {
        Pattern regexPattern = Pattern.compile("^[a-zA-ZčćžpšđČŽĆPŠĐ\\-._]{5,}$");
        return regexPattern.matcher(username).matches();
    }

    public static boolean isPasswordValid(String password) {
        Pattern regexPattern = Pattern.compile("^((?=.*[a-zA-ZšđčćžŠĐČŽĆ])(?=.*\\d)).{6,}$");
        return regexPattern.matcher(password).matches();
    }

    public static boolean isResetPasswordTokenValid(String resetPasswordToken) {
        return resetPasswordToken.length() == AppConfiguration.ResetPasswordTokenLength;
    }

}
