package helpers.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.w3c.dom.Text;

import java.util.List;

import ba.com.apdesign.aptours.R;
import helpers.RetrofitBuilder;
import models.TourReview;
import models.ToursDTO;
import okhttp3.ResponseBody;
import repositories.AccessRepository;
import repositories.ToursRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourCardAdapter extends ArrayAdapter<ToursDTO> {
    private Context _context;
    private String _authToken;
    private boolean _isSavedToursFragment;
    private boolean _isFinishedTourFragment;
    private boolean _isUpcomingTourFragment;
    private ToursRepository _toursService;


    public TourCardAdapter(Context context, int textViewResourceId, String authToken) {
        super(context, textViewResourceId);
        _toursService = RetrofitBuilder.Build(_context).create(ToursRepository.class);

        _context = context;
        _authToken = authToken;
        _isSavedToursFragment = false;
        _isFinishedTourFragment = false;
    }

    public TourCardAdapter(Context context, int resource, List<ToursDTO> items, String authToken) {
        super(context, resource, items);
        _toursService = RetrofitBuilder.Build(_context).create(ToursRepository.class);

        _context = context;
        _authToken = authToken;
        _isSavedToursFragment = false;
        _isFinishedTourFragment = false;
    }

    public TourCardAdapter(Context context, int resource, List<ToursDTO> items, String authToken, boolean isSavedToursFragment) {
        super(context, resource, items);
        _toursService = RetrofitBuilder.Build(_context).create(ToursRepository.class);

        _context = context;
        _authToken = authToken;
        _isSavedToursFragment = isSavedToursFragment;
        _isFinishedTourFragment = false;
    }

    public TourCardAdapter(Context context, int resource, List<ToursDTO> items, String authToken, boolean isSavedToursFragment, boolean isFinishedTourFragment) {
        super(context, resource, items);
        _toursService = RetrofitBuilder.Build(_context).create(ToursRepository.class);

        _context = context;
        _authToken = authToken;
        _isSavedToursFragment = isSavedToursFragment;
        _isFinishedTourFragment = isFinishedTourFragment;
    }

    public TourCardAdapter(Context context, int resource, List<ToursDTO> items, String authToken, boolean isSavedToursFragment, boolean isFinishedTourFragment, boolean isUpcomingTourFragment) {
        super(context, resource, items);
        _toursService = RetrofitBuilder.Build(_context).create(ToursRepository.class);

        _context = context;
        _authToken = authToken;
        _isSavedToursFragment = isSavedToursFragment;
        _isFinishedTourFragment = isFinishedTourFragment;
        _isUpcomingTourFragment = isUpcomingTourFragment;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.adapter_tour_card, null);
        }

        final ToursDTO tour = getItem(position);

        if (tour != null) {
            final AppCompatImageView addToFavorite = v.findViewById(R.id.AddTourToFavorite);
            final ProgressBar tourProgress = v.findViewById(R.id.TourProgress);

            TextView tourPrice = v.findViewById(R.id.TourPrice);
            TextView tourTitle = v.findViewById(R.id.TourTitle);
            TextView tourGradeInformation = v.findViewById(R.id.TourGradeInformation);
            TextView tourDescription = v.findViewById(R.id.TourDescription);
            AppCompatImageView tourImage = v.findViewById(R.id.TourImage);

            AppCompatImageView ratingFirstStar = v.findViewById(R.id.RatingFirstStar);
            AppCompatImageView ratingSecondStar = v.findViewById(R.id.RatingSecondStar);
            AppCompatImageView ratingThirdStar = v.findViewById(R.id.RatingThirdStar);
            AppCompatImageView ratingFourthStar = v.findViewById(R.id.RatingFourthStar);
            AppCompatImageView ratingFifthStar = v.findViewById(R.id.RatingFifthStar);

            tourTitle.setText(tour.Title);
            tourDescription.setText(tour.Description);
            tourPrice.setText(String.valueOf(tour.Price) + " KM");
            addToFavorite.setImageResource(tour.Favorite ? R.drawable.ic_remove_to_favorite : R.drawable.ic_add_to_favorite);

            if(tour.NumberOfReviews == 0) {
                tourGradeInformation.setText("-/5 ("+ tour.NumberOfReviews +")");

                ratingFirstStar.setImageResource(R.drawable.ic_star_border);
                ratingSecondStar.setImageResource(R.drawable.ic_star_border);
                ratingThirdStar.setImageResource(R.drawable.ic_star_border);
                ratingFourthStar.setImageResource(R.drawable.ic_star_border);
                ratingFifthStar.setImageResource(R.drawable.ic_star_border);
            }
            else {
                tourGradeInformation.setText(tour.Grade + "/5 ("+ tour.NumberOfReviews +")");

                ratingFirstStar.setImageResource( (tour.Grade/1 >= 1 ? R.drawable.ic_full_star : tour.Grade%1 >= 0.5 ? R.drawable.ic_half_star : R.drawable.ic_star_border));
                ratingSecondStar.setImageResource((tour.Grade/2 >= 1 ? R.drawable.ic_full_star : tour.Grade%2 >= 1.5 ? R.drawable.ic_half_star : R.drawable.ic_star_border));
                ratingThirdStar.setImageResource( (tour.Grade/3 >= 1 ? R.drawable.ic_full_star : tour.Grade%3 >= 2.5 ? R.drawable.ic_half_star : R.drawable.ic_star_border));
                ratingFourthStar.setImageResource((tour.Grade/4 >= 1 ? R.drawable.ic_full_star : tour.Grade%4 >= 3.5 ? R.drawable.ic_half_star : R.drawable.ic_star_border));
                ratingFifthStar.setImageResource( (tour.Grade/5 >= 1 ? R.drawable.ic_full_star : tour.Grade%5 >= 4.5 ? R.drawable.ic_half_star : R.drawable.ic_star_border));
            }


            TextView tourReservationDate = v.findViewById(R.id.TourReservationDate);

            if(tour.ReservedAtString != null && tour.NumberOfPassengers > 0) {
                tourReservationDate.setText(String.format(getContext().getString(R.string.ReservationDateInformation), tour.NumberOfPassengers, tour.ReservedAtString));
                tourReservationDate.setVisibility(View.VISIBLE);
            }
            else if(tour.UpcomingDateString != null) {
                tourReservationDate.setText(String.format(getContext().getString(R.string.UpcomingTourDate), tour.UpcomingDateString));
                tourReservationDate.setVisibility(View.VISIBLE);
            }
            else
                tourReservationDate.setVisibility(View.GONE);

            if(tour.Image != null) {
                tourProgress.setVisibility(View.VISIBLE);

                Ion.with(tourImage).load(_context.getString(R.string.ApiRoot) + tour.Image).setCallback(new FutureCallback<ImageView>() {
                    @Override
                    public void onCompleted(Exception e, ImageView result) {
                        tourProgress.setVisibility(View.GONE);
                    }
                });
            }

            if(_isFinishedTourFragment) {
                final TextView tourReviewButton = (TextView) v.findViewById(R.id.TourReviewButton);
                if(tour.CanAddReview && !tour.AddingReviewFlag) {
                    tourReviewButton.setVisibility(View.VISIBLE);

                    tourReviewButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog dialog = new Dialog(getContext());
                            dialog.setContentView(R.layout.activity_add_review);

                            //Rating stars events
                            final AppCompatImageView reviewGradeFirstStar = dialog.findViewById(R.id.reviewGradeFirstStar);
                            final AppCompatImageView reviewGradeSecondStar = dialog.findViewById(R.id.reviewGradeSecondStar);
                            final AppCompatImageView reviewGradeThirdStar = dialog.findViewById(R.id.reviewGradeThirdStar);
                            final AppCompatImageView reviewGradeFourthStar = dialog.findViewById(R.id.reviewGradeFourthStar);
                            final AppCompatImageView reviewGradeFifthStar = dialog.findViewById(R.id.reviewGradeFifthStar);

                            reviewGradeFirstStar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    tour.ReviewGrade = 1;
                                    reviewGradeFirstStar.setImageResource(R.drawable.ic_full_star);
                                    reviewGradeSecondStar.setImageResource(R.drawable.ic_star_border);
                                    reviewGradeThirdStar.setImageResource(R.drawable.ic_star_border);
                                    reviewGradeFourthStar.setImageResource(R.drawable.ic_star_border);
                                    reviewGradeFifthStar.setImageResource(R.drawable.ic_star_border);
                                }
                            });

                            reviewGradeSecondStar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    tour.ReviewGrade = 2;

                                    reviewGradeFirstStar.setImageResource(R.drawable.ic_full_star);
                                    reviewGradeSecondStar.setImageResource(R.drawable.ic_full_star);
                                    reviewGradeThirdStar.setImageResource(R.drawable.ic_star_border);
                                    reviewGradeFourthStar.setImageResource(R.drawable.ic_star_border);
                                    reviewGradeFifthStar.setImageResource(R.drawable.ic_star_border);
                                }
                            });

                            reviewGradeThirdStar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    tour.ReviewGrade = 3;
                                    reviewGradeFirstStar.setImageResource(R.drawable.ic_full_star);
                                    reviewGradeSecondStar.setImageResource(R.drawable.ic_full_star);
                                    reviewGradeThirdStar.setImageResource(R.drawable.ic_full_star);
                                    reviewGradeFourthStar.setImageResource(R.drawable.ic_star_border);
                                    reviewGradeFifthStar.setImageResource(R.drawable.ic_star_border);
                                }
                            });

                            reviewGradeFourthStar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    tour.ReviewGrade = 4;
                                    reviewGradeFirstStar.setImageResource(R.drawable.ic_full_star);
                                    reviewGradeSecondStar.setImageResource(R.drawable.ic_full_star);
                                    reviewGradeThirdStar.setImageResource(R.drawable.ic_full_star);
                                    reviewGradeFourthStar.setImageResource(R.drawable.ic_full_star);
                                    reviewGradeFifthStar.setImageResource(R.drawable.ic_star_border);
                                }
                            });

                            reviewGradeFifthStar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    tour.ReviewGrade = 5;
                                    reviewGradeFirstStar.setImageResource(R.drawable.ic_full_star);
                                    reviewGradeSecondStar.setImageResource(R.drawable.ic_full_star);
                                    reviewGradeThirdStar.setImageResource(R.drawable.ic_full_star);
                                    reviewGradeFourthStar.setImageResource(R.drawable.ic_full_star);
                                    reviewGradeFifthStar.setImageResource(R.drawable.ic_full_star);
                                }
                            });
                            //End of rating stars events

                            Button submitButton = (Button) dialog.findViewById(R.id.SubmitReview);
                            submitButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TextView reviewText = (TextView) dialog.findViewById(R.id.reviewText);

                                    //Field validation
                                    if(tour.ReviewGrade == 0) {
                                        Toast.makeText(getContext(), getContext().getString(R.string.ErrMsgReviewGrade), Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    if(reviewText.getText().length() == 0) {
                                        reviewText.setError(getContext().getString(R.string.ErrMsgReviewText));
                                        return;
                                    }
                                    else
                                        reviewText.setError(null);
                                    //End of validation

                                    //Object preparation
                                    TourReview review = new TourReview();

                                    review.TourID = tour.TourID;
                                    review.Grade = tour.ReviewGrade;
                                    review.Review = reviewText.getText().toString();
                                    //End of preparation

                                    tour.AddingReviewFlag = true;

                                    Call<ResponseBody> call = _toursService.AddReview(_authToken, tour.TourID, review);

                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if(response.isSuccessful()) {
                                                tour.CanAddReview = false;
                                                Toast.makeText(getContext(), getContext().getString(R.string.ReviewSuccessfullyAdded), Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                                Toast.makeText(getContext(), getContext().getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();

                                            tour.AddingReviewFlag = false;
                                            tourReviewButton.setVisibility(View.GONE);
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            tour.AddingReviewFlag = false;
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });

                            Button cancelButton = (Button) dialog.findViewById(R.id.CancelReview);
                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        }
                    });
                }
                else
                    tourReviewButton.setVisibility(View.GONE);
            }

            if(_isUpcomingTourFragment) {
                final TextView tourReviewButton = v.findViewById(R.id.TourReviewButton);
                tourReviewButton.setText(getContext().getString(R.string.CancelReservation));
                tourReviewButton.setVisibility(View.VISIBLE);

                tourReviewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Call<ResponseBody> call = _toursService.CancelReservation(_authToken, tour.TourID, tour.TourReservationID);

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.isSuccessful()) {
                                    Toast.makeText(getContext(), getContext().getString(R.string.TourReservationSuccessfullyCanceled), Toast.LENGTH_SHORT).show();
                                    remove(tour);
                                }
                                else
                                    Toast.makeText(getContext(), getContext().getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(getContext(), getContext().getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            if(_isFinishedTourFragment)
                addToFavorite.setVisibility(View.GONE);
            else {
                addToFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(tour.ChangingFavoriteState)
                            return;

                        tour.ChangingFavoriteState = true;

                        Call<ResponseBody> call;

                        if(tour.Favorite)
                            call = _toursService.RemoveFromFavorite(_authToken, tour.TourID);
                        else
                            call = _toursService.AddToFavorite(_authToken, tour.TourID);

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.isSuccessful()) {
                                    if(tour.Favorite) {
                                        Toast.makeText(_context, _context.getText(R.string.SuccessfullyRemovedFromFavorite), Toast.LENGTH_SHORT).show();
                                        addToFavorite.setImageResource(R.drawable.ic_add_to_favorite);

                                        if(_isSavedToursFragment)
                                            remove(tour);
                                    }
                                    else {
                                        Toast.makeText(_context, _context.getText(R.string.SuccessfullyAddedToFavorite), Toast.LENGTH_SHORT).show();
                                        addToFavorite.setImageResource(R.drawable.ic_remove_to_favorite);
                                    }

                                    tour.Favorite = !tour.Favorite;
                                }
                                else if(tour.Favorite)
                                    Toast.makeText(_context, _context.getText(R.string.ErrMsgUnableToRemoveFromFavorite), Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(_context, _context.getText(R.string.ErrMsgUnableToAddToFavorite), Toast.LENGTH_SHORT).show();

                                tour.ChangingFavoriteState = false;
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(_context, _context.getText(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                                tour.ChangingFavoriteState = false;
                            }
                        });
                    }
                });
            }
        }
        return v;
    }
}
