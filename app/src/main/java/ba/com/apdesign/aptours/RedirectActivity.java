package ba.com.apdesign.aptours;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import helpers.GeneralHelper;
import models.Access;
import repositories.AccessRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RedirectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect);

        final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.AuthTokenPreferencesFile), Context.MODE_PRIVATE);
        String authToken = sharedPref.getString(getString(R.string.AuthTokenKey), null);

        if(authToken == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
        else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.ApiBaseUrl))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            AccessRepository accessRepository = retrofit.create(AccessRepository.class);
            Call<Access> call = accessRepository.Access(authToken);

            call.enqueue(new Callback<Access>() {
                @Override
                public void onResponse(Call<Access> call, Response<Access> response) {
                    if(response.isSuccessful()) {
                        String username = sharedPref.getString(getString(R.string.UsernameKey), null);

                        if(username == null)
                            GeneralHelper.fillAccessSharedPreferences(RedirectActivity.this, response.body());

                        Intent intent = new Intent(RedirectActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }
                    else {
                        GeneralHelper.clearAccessSharedPreferences(RedirectActivity.this);

                        Intent intent = new Intent(RedirectActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<Access> call, Throwable t) {
                    Toast.makeText(RedirectActivity.this, getString(R.string.ErrMsgApiFailure), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RedirectActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }
            });
        }
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}
