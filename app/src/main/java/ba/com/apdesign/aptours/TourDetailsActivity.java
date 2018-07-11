package ba.com.apdesign.aptours;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import helpers.AppConfiguration;
import helpers.GeneralHelper;
import helpers.RetrofitBuilder;
import helpers.adapters.TourDetailsTabsAdapter;
import helpers.myWidgets.WrapContentHeightViewPager;
import models.Access;
import models.TourDates;
import models.ToursDTO;
import okhttp3.ResponseBody;
import repositories.ToursRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourDetailsActivity extends AppCompatActivity {
    private ToursDTO _tour;
    private String _tourFilter;

    private Access _accessModel;
    private boolean _changingFavoriteStateFlag = false;
    private ToursRepository _toursService;
    private AppCompatImageView addTourToFavorite;
    private FloatingActionButton bookTour;

    private TextView tourTitle;
    private AppCompatImageView tourImage;
    private ProgressBar tourImageProgressBar;
    private ProgressBar dataLoadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BindControls();
        ReadFromExtras();

        if(_tourFilter.equals(AppConfiguration.TourFlterFinished)) {
            addTourToFavorite.setVisibility(View.GONE);
            bookTour.setVisibility(View.GONE);
        }
        else {
            addTourToFavorite.setImageResource(_tour.Favorite ? R.drawable.ic_remove_to_favorite : R.drawable.ic_add_to_favorite);

            Call<ArrayList<TourDates>> call = _toursService.GetDates(_accessModel.AuthToken, _tour.TourID);

            call.enqueue(new Callback<ArrayList<TourDates>>() {
                @Override
                public void onResponse(Call<ArrayList<TourDates>> call, Response<ArrayList<TourDates>> response) {
                    final ArrayList<TourDates> tourDates = response.body();

                    if(tourDates.size() > 0) {
                        bookTour.setVisibility(View.VISIBLE);

                        bookTour.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(TourDetailsActivity.this, BookTourActivity.class);

                                intent.putExtra(AppConfiguration.TourIDKey, _tour.TourID);
                                intent.putExtra(AppConfiguration.AuthTokenKey, _accessModel.AuthToken);
                                intent.putExtra(AppConfiguration.TourPriceKey, _tour.Price);

                                intent.putExtra(AppConfiguration.TourDatesKey, tourDates);

                                startActivity(intent);
                            }
                        });
                    }

                    /*ArrayAdapter<TourDates> arrayAdapter = new ArrayAdapter<TourDates>(BookTourActivity.this, android.R.layout.simple_spinner_dropdown_item, _tourDates);
                    availableDate.setAdapter(arrayAdapter);

                    contentContainer.setVisibility(View.VISIBLE);
                    loadingContainer.setVisibility(View.GONE);*/
                }

                @Override
                public void onFailure(Call<ArrayList<TourDates>> call, Throwable t) {
                    bookTour.setVisibility(View.GONE);
                    finish();
                }
            });


            addTourToFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(_changingFavoriteStateFlag)
                        return;

                    _changingFavoriteStateFlag = true;

                    Call<ResponseBody> call;

                    if(_tour.Favorite)
                        call = _toursService.RemoveFromFavorite(_accessModel.AuthToken, _tour.TourID);
                    else
                        call = _toursService.AddToFavorite(_accessModel.AuthToken, _tour.TourID);

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.isSuccessful()) {
                                if(_tour.Favorite) {
                                    Toast.makeText(TourDetailsActivity.this, getText(R.string.SuccessfullyRemovedFromFavorite), Toast.LENGTH_SHORT).show();
                                    addTourToFavorite.setImageResource(R.drawable.ic_add_to_favorite);
                                }
                                else {
                                    Toast.makeText(TourDetailsActivity.this, getText(R.string.SuccessfullyAddedToFavorite), Toast.LENGTH_SHORT).show();
                                    addTourToFavorite.setImageResource(R.drawable.ic_remove_to_favorite);
                                }

                                _tour.Favorite = !_tour.Favorite;
                            }
                            else if(_tour.Favorite)
                                Toast.makeText(TourDetailsActivity.this, getText(R.string.ErrMsgUnableToRemoveFromFavorite), Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(TourDetailsActivity.this, getText(R.string.ErrMsgUnableToAddToFavorite), Toast.LENGTH_SHORT).show();

                            _changingFavoriteStateFlag = false;
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(TourDetailsActivity.this, getText(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                            _changingFavoriteStateFlag = false;
                        }
                    });
                }
            });
        }
        
        LoadData();

        WrapContentHeightViewPager viewPager = (WrapContentHeightViewPager) findViewById(R.id.ViewPager);
        viewPager.setAdapter(new TourDetailsTabsAdapter(getSupportFragmentManager(),this, _tour, _accessModel.AuthToken));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.TourDetailTabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void LoadData() {
        tourTitle.setText(_tour.Title);

        if(_tour.Image != null) {
            tourImageProgressBar.setVisibility(View.VISIBLE);

            Ion.with(tourImage).load(getString(R.string.ApiRoot) + _tour.Image).setCallback(new FutureCallback<ImageView>() {
                @Override
                public void onCompleted(Exception e, ImageView result) {
                    tourImageProgressBar.setVisibility(View.GONE);
                }
            });
        }

    }

    private void ReadFromExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            _tourFilter = extras.getString(AppConfiguration.FilterKey);
            _tour = (ToursDTO) extras.getSerializable(AppConfiguration.TourKey);
        }
    }

    private void BindControls() {
        _toursService = RetrofitBuilder.Build(this).create(ToursRepository.class);
        _accessModel = GeneralHelper.readAccessSharedPreferences(this);

        addTourToFavorite = findViewById(R.id.AddTourToFavorite);
        bookTour = (FloatingActionButton) findViewById(R.id.BookTour);
        bookTour.bringToFront();

        tourImage = findViewById(R.id.TourImage);
        tourTitle = findViewById(R.id.TourTitle);
        tourImageProgressBar = findViewById(R.id.TourImageProgressBar);
        dataLoadingProgress = findViewById(R.id.DataLoadingProgress);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return false;
        }

        return super.onOptionsItemSelected(item);
    }
}
