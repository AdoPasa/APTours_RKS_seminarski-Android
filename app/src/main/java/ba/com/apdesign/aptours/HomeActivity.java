package ba.com.apdesign.aptours;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import de.hdodenhof.circleimageview.CircleImageView;
import helpers.FragmentHelper;
import helpers.GeneralHelper;
import helpers.ImageHelper;
import helpers.RetrofitBuilder;
import models.Access;
import models.ProfilePhoto;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import repositories.UsersRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //Constants
    final public static int RESULT_CHANGE_PROFILE_PICTURE = 1;
    //End of constants


    Toolbar navigationToolbar;
    TextView navHeaderFullName;
    TextView navHeaderEmail;
    CircleImageView navHeaderProfilePhoto;

    Access _accessModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        navigationToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(navigationToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, navigationToolbar, R.string.NavigationDrawerOpen, R.string.NavigationDrawerClose) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                //implentirati poziv na API
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        Fragment fragment = new HomeFragment();
        FragmentHelper.ReplaceFragment(this, R.id.contentContainer, fragment);
        navigationToolbar.setTitle(getString(R.string.Home));

        View header = navigationView.getHeaderView(0);
        navHeaderProfilePhoto = (CircleImageView) header.findViewById(R.id.navHeaderProfilePhoto);
        navHeaderFullName = (TextView) header.findViewById(R.id.navHeaderFullName);
        navHeaderEmail = (TextView) header.findViewById(R.id.navHeaderEmail);

        _accessModel = GeneralHelper.readAccessSharedPreferences(this);

        updateNavHeader();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Class fragmentClass = null;

        switch (id) {
            case R.id.navHome: {
                fragmentClass = HomeFragment.class;
                navigationToolbar.setTitle(getString(R.string.Home));
                break;
            }
            case R.id.navUpcomingTours: {
                fragmentClass = UpcomingToursFragment.class;
                navigationToolbar.setTitle(getString(R.string.UpcomingTours));
                break;
            }
            case R.id.navSavedTours: {
                fragmentClass = SavedToursFragment.class;
                navigationToolbar.setTitle(getString(R.string.SavedTours));
                break;
            }
            case R.id.navFinishedTours: {
                fragmentClass = FinishedToursFragment.class;
                navigationToolbar.setTitle(getString(R.string.FinishedTours));
                break;
            }
            case R.id.navAccount: {
                fragmentClass = AccountFragment.class;
                navigationToolbar.setTitle(getString(R.string.Account));
                break;
            }
            case R.id.navLogout: {
                GeneralHelper.logoutUser(this);
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if(fragmentClass!=null)
        {
            try {
                FragmentHelper.ReplaceFragment(this, R.id.contentContainer, (Fragment) fragmentClass.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void updateNavHeader() {
        if(_accessModel.ProfilePhoto != null) {
            Ion.with(this).load(getString(R.string.ApiRoot) + _accessModel.ProfilePhoto).withBitmap().asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if(result != null)
                                navHeaderProfilePhoto.setImageBitmap(result);
                        }
                    });
        }

        navHeaderFullName.setText(_accessModel.FirstName + " " + _accessModel.LastName);
        navHeaderEmail.setText(_accessModel.Email);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CHANGE_PROFILE_PICTURE && resultCode == RESULT_OK && null != data) {
            UsersRepository _usersService = RetrofitBuilder.Build(this).create(UsersRepository.class);

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            AppCompatImageView imageView = findViewById(R.id.ProfilePhotoImageView);
            Bitmap image = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(image);

            ProfilePhoto model = new ProfilePhoto();
            model.ImageName = picturePath;
            model.ImageStream = Base64.encodeToString(ImageHelper.GetBytesFromBitmap(image), Base64.DEFAULT);
                    //RequestBody.create(MediaType.parse("application/octet-stream"), ImageHelper.GetBytesFromBitmap(image));

            Call<String> call = _usersService.UpdateProfilePhoto(_accessModel.AuthToken, model);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Toast.makeText(HomeActivity.this, getString(R.string.ProfilePhotoSuccessfullyChanged), Toast.LENGTH_SHORT).show();

                    _accessModel.ProfilePhoto = response.body();
                    GeneralHelper.fillAccessSharedPreferences( HomeActivity.this, _accessModel);
                    updateNavHeader();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(HomeActivity.this, getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
