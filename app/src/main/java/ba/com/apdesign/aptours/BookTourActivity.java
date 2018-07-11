package ba.com.apdesign.aptours;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import helpers.AppConfiguration;
import helpers.RetrofitBuilder;
import helpers.SpinnerHelper;
import models.TourDates;
import models.TourDetails;
import models.TourReservations;
import okhttp3.ResponseBody;
import repositories.ToursRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookTourActivity extends AppCompatActivity {
    //Working data
    private int _numberOfPassengers = 0;
    private String _authToken;
    private float _tourPrice;
    private int _tourID;
    private ArrayList<TourDates> _tourDates;

    //Activity controls
    private EditText numberOfPassengers;
    private Spinner availableDate;
    private Button submitReservation;
    private Button cancelReservation;
    private TextView priceTextView;
    //private ProgressBar loadingContainer;
    private LinearLayout contentContainer;

    //Repositories
    ToursRepository _toursService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_tour);
        _toursService = RetrofitBuilder.Build(this).create(ToursRepository.class);

        bindControls();
        readFromExtras();

        //populateAvailableDates();
        addEvents();
    }

    private void readFromExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            _tourID = extras.getInt(AppConfiguration.TourIDKey);
            _tourPrice = extras.getFloat(AppConfiguration.TourPriceKey);
            _authToken = extras.getString(AppConfiguration.AuthTokenKey);
            _tourDates = (ArrayList<TourDates>)extras.getSerializable(AppConfiguration.TourDatesKey);

            ArrayAdapter<TourDates> arrayAdapter = new ArrayAdapter<TourDates>(BookTourActivity.this, android.R.layout.simple_spinner_dropdown_item, _tourDates);
            availableDate.setAdapter(arrayAdapter);
        }
        else {
            Toast.makeText(this, getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void addEvents() {
        numberOfPassengers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredPrice = numberOfPassengers.getText().toString();

                if(enteredPrice.length() == 0) {
                    priceTextView.setText("- " + getString(R.string.CurrencyCode));
                    _numberOfPassengers = 0;
                    return;
                }

                _numberOfPassengers = Integer.parseInt(enteredPrice);
                inputsAreValid();

                priceTextView.setText(String.valueOf(_tourPrice*_numberOfPassengers) + " " + getString(R.string.CurrencyCode));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submitReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inputsAreValid())
                    return;

                TourReservations model = new TourReservations();

                Call<ResponseBody> call = _toursService.AddReservation(_authToken, _tourID, model);

                model.TourID = _tourID;
                model.TourDateID = ((TourDates)availableDate.getSelectedItem()).TourDateID;
                model.NumberOfPassengers = _numberOfPassengers;

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()) {
                            Toast.makeText( BookTourActivity.this,getString(R.string.ReservationSuccessfullyAdded), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                            Toast.makeText( BookTourActivity.this,getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText( BookTourActivity.this,getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        cancelReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean inputsAreValid() {
        boolean valid = true;

        if(_numberOfPassengers <= 0) {
            numberOfPassengers.setError(getString(R.string.ErrMsgNumberOfPassengers));
            valid = false;
        }
        else if(_numberOfPassengers > ((TourDates)availableDate.getSelectedItem()).NumberOfFreePlaces) {
            numberOfPassengers.setError(String.format(getString(R.string.ErrMsgTourDatesFreePlaces), ((TourDates)availableDate.getSelectedItem()).NumberOfFreePlaces));
            valid = false;
        }
        else numberOfPassengers.setError(null);

        return valid;
    }

    /*private void populateAvailableDates() {
        Call<List<TourDates>> call = _toursService.GetDates(_authToken, _tourID);

        call.enqueue(new Callback<List<TourDates>>() {
            @Override
            public void onResponse(Call<List<TourDates>> call, Response<List<TourDates>> response) {
                _tourDates = response.body();

                if(_tourDates.size() == 0) {
                    Toast.makeText( BookTourActivity.this,getString(R.string.ErrMsgNoTourDates), Toast.LENGTH_SHORT).show();
                    finish();
                }

                ArrayAdapter<TourDates> arrayAdapter = new ArrayAdapter<TourDates>(BookTourActivity.this, android.R.layout.simple_spinner_dropdown_item, _tourDates);
                availableDate.setAdapter(arrayAdapter);

                contentContainer.setVisibility(View.VISIBLE);
                loadingContainer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<TourDates>> call, Throwable t) {
                Toast.makeText( BookTourActivity.this,getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        addEvents();

    }*/

    //Helpers
    private void bindControls() {
        numberOfPassengers = findViewById(R.id.NumberOfPassengersEditText);
        availableDate = findViewById(R.id.AvailableDateSpinner);
        submitReservation = findViewById(R.id.SubmitReservationButton);
        cancelReservation = findViewById(R.id.CancelReservationButton);
        priceTextView = findViewById(R.id.priceTextView);
        //loadingContainer = findViewById(R.id.loadingContainer);
        contentContainer = findViewById(R.id.contentContainer);
    }
}
