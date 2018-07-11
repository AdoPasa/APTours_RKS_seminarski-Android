package helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import ba.com.apdesign.aptours.LoginActivity;
import ba.com.apdesign.aptours.R;
import models.Access;

public class GeneralHelper {
    public static void fillAccessSharedPreferences(Context context, Access model) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.AuthTokenPreferencesFile), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(context.getString(R.string.AuthTokenKey), model.AuthToken);
        editor.putString(context.getString(R.string.FirstnameKey), model.FirstName);
        editor.putString(context.getString(R.string.LastnameKey), model.LastName);
        editor.putString(context.getString(R.string.UsernameKey), model.Username);
        editor.putString(context.getString(R.string.EmailKey), model.Email);
        editor.putString(context.getString(R.string.ProfilePhotoKey), model.ProfilePhoto);

        editor.apply();
    }

    public static Access readAccessSharedPreferences(Context context) {
        Access model = new Access();

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.AuthTokenPreferencesFile), Context.MODE_PRIVATE);
        model.AuthToken = sharedPreferences.getString(context.getString(R.string.AuthTokenKey), null);
        model.FirstName = sharedPreferences.getString(context.getString(R.string.FirstnameKey), "");
        model.LastName = sharedPreferences.getString(context.getString(R.string.LastnameKey), "");
        model.Username = sharedPreferences.getString(context.getString(R.string.UsernameKey), "");
        model.Email = sharedPreferences.getString(context.getString(R.string.EmailKey), "");
        model.ProfilePhoto = sharedPreferences.getString(context.getString(R.string.ProfilePhotoKey), null);

        return model;
    }

    public static void clearAccessSharedPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.AuthTokenPreferencesFile), Context.MODE_PRIVATE);
        sharedPref.edit().clear().commit();
    }

    public static void logoutUser(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.AuthTokenPreferencesFile), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.remove(context.getString(R.string.AuthTokenKey));
        editor.apply();

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);
    }
}
