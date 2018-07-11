package ba.com.apdesign.aptours;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import models.TourDetails;
import models.TourReviews;
import models.ToursDTO;

public class TourDetailsReviewsFragment extends Fragment {
    public static final String ARG_TOURDETAILS = "ARG_TOURDETAILS";
    public static final String ARG_TOUR = "ARG_TOUR";

    private TourDetails _tourDetails;
    private ToursDTO _tour;

    public static TourDetailsReviewsFragment newInstance(ToursDTO tour, TourDetails tourDetails) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TOUR, tour);
        args.putSerializable(ARG_TOURDETAILS, tourDetails);

        TourDetailsReviewsFragment fragment = new TourDetailsReviewsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _tourDetails = (TourDetails)getArguments().getSerializable(ARG_TOURDETAILS);
        _tour = (ToursDTO) getArguments().getSerializable(ARG_TOUR);
    }

    public TourDetailsReviewsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_details_reviews, container, false);

        ((TextView) view.findViewById(R.id.NumberOfReviewsTextView)).setText(String.valueOf(_tour.NumberOfReviews) + " recenzija");

        if(_tour.NumberOfReviews == 0) {
            ((AppCompatImageView) view.findViewById(R.id.RatingFirstStar)).setImageResource(R.drawable.ic_star_border);
            ((AppCompatImageView) view.findViewById(R.id.RatingSecondStar)).setImageResource(R.drawable.ic_star_border);
            ((AppCompatImageView) view.findViewById(R.id.RatingThirdStar)).setImageResource(R.drawable.ic_star_border);
            ((AppCompatImageView) view.findViewById(R.id.RatingFourthStar)).setImageResource(R.drawable.ic_star_border);
            ((AppCompatImageView) view.findViewById(R.id.RatingFifthStar)).setImageResource(R.drawable.ic_star_border);
        }
        else {
            ((AppCompatImageView) view.findViewById(R.id.RatingFirstStar)).setImageResource((_tour.Grade/1 >= 1 ? R.drawable.ic_full_star : _tour.Grade%1 >= 0.5 ? R.drawable.ic_half_star : R.drawable.ic_star_border));
            ((AppCompatImageView) view.findViewById(R.id.RatingSecondStar)).setImageResource((_tour.Grade/2 >= 1 ? R.drawable.ic_full_star : _tour.Grade%2 >= 1.5 ? R.drawable.ic_half_star : R.drawable.ic_star_border));
            ((AppCompatImageView) view.findViewById(R.id.RatingThirdStar)).setImageResource( (_tour.Grade/3 >= 1 ? R.drawable.ic_full_star : _tour.Grade%3 >= 2.5 ? R.drawable.ic_half_star : R.drawable.ic_star_border));
            ((AppCompatImageView) view.findViewById(R.id.RatingFourthStar)).setImageResource((_tour.Grade/4 >= 1 ? R.drawable.ic_full_star : _tour.Grade%4 >= 3.5 ? R.drawable.ic_half_star : R.drawable.ic_star_border));
            ((AppCompatImageView) view.findViewById(R.id.RatingFifthStar)).setImageResource( (_tour.Grade/5 >= 1 ? R.drawable.ic_full_star : _tour.Grade%5 >= 4.5 ? R.drawable.ic_half_star : R.drawable.ic_star_border));
        }

        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        LinearLayout reviewsContainer = (LinearLayout)view.findViewById(R.id.reviewsContainer);
        View reviewView = null;

        if(_tourDetails.TourReviews.size() != 0) {
            TourReviews reviewItem;

            for(int i = 0; i < _tourDetails.TourReviews.size(); i++) {
                reviewItem = _tourDetails.TourReviews.get(i);

                reviewView = vi.inflate(R.layout.adapter_tour_review, null);

                ((TextView)reviewView.findViewById(R.id.reviewAuthor)).setText(reviewItem.User);
                ((TextView)reviewView.findViewById(R.id.reviewDate)).setText(reviewItem.CreatedAt);
                ((TextView)reviewView.findViewById(R.id.reviewText)).setText(reviewItem.Review);

                ((AppCompatImageView) reviewView.findViewById(R.id.RatingFirstStar)).setImageResource ((reviewItem.Grade >= 1 ? R.drawable.ic_full_star : R.drawable.ic_star_border));
                ((AppCompatImageView) reviewView.findViewById(R.id.RatingSecondStar)).setImageResource((reviewItem.Grade >= 2 ? R.drawable.ic_full_star : R.drawable.ic_star_border));
                ((AppCompatImageView) reviewView.findViewById(R.id.RatingThirdStar)).setImageResource ((reviewItem.Grade >= 3 ? R.drawable.ic_full_star : R.drawable.ic_star_border));
                ((AppCompatImageView) reviewView.findViewById(R.id.RatingFourthStar)).setImageResource((reviewItem.Grade >= 4 ? R.drawable.ic_full_star : R.drawable.ic_star_border));
                ((AppCompatImageView) reviewView.findViewById(R.id.RatingFifthStar)).setImageResource ((reviewItem.Grade >= 5 ? R.drawable.ic_full_star : R.drawable.ic_star_border));

                reviewsContainer.addView(reviewView);
                LoadUserImage((AppCompatImageView)reviewView.findViewById(R.id.reviewProfilePhoto), reviewItem.ProfilePhoto);
            }
        }
        else {
            view.findViewById(R.id.reviewsUnavaibleText).setVisibility(View.VISIBLE);
            view.findViewById(R.id.reviewGradeContainer).setVisibility(View.GONE);
        }

        return view;
    }

    private void LoadUserImage(ImageView imageView, String image){
        Ion.with(imageView)
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .load(getContext().getString(R.string.ApiRoot) + image);
    }
}
