package ba.com.apdesign.aptours;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import models.TourDetails;
import models.ToursDTO;


public class TourDetailsOverviewFragment extends Fragment {
    public static final String ARG_TOUR = "ARG_TOUR";
    public static final String ARG_TOURDETAILS = "ARG_TOURDETAILS";

    private ToursDTO _tour;
    private TourDetails _tourDetails;

    public static TourDetailsOverviewFragment newInstance(ToursDTO tour, TourDetails tourDetails) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TOUR, tour);
        args.putSerializable(ARG_TOURDETAILS, tourDetails);

        TourDetailsOverviewFragment fragment = new TourDetailsOverviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _tour = (ToursDTO)getArguments().getSerializable(ARG_TOUR);
        _tourDetails = (TourDetails)getArguments().getSerializable(ARG_TOURDETAILS);
    }

    public TourDetailsOverviewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tour_details_overview, container, false);
        ((TextView)view.findViewById(R.id.TourGradeTextView)).setText((_tour.Grade > 0 ? String.valueOf(_tour.Grade) : "-") + "/5");
        ((TextView)view.findViewById(R.id.PriceTextView)).setText(String.valueOf(_tour.Price) + " " + getString(R.string.CurrencyCode));
        ((TextView)view.findViewById(R.id.TourDescriptionTextView)).setText(_tour.Description);

        LinearLayout additionnalInformationContainer = (LinearLayout) view.findViewById(R.id.additionalInformationContainer);

        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View additionalInfoView = null;

        if(_tourDetails.ToursAdditionalInformations.size() != 0) {
            for(int i = 0; i < _tourDetails.ToursAdditionalInformations.size(); i++) {
                additionalInfoView = vi.inflate(R.layout.additional_information_adapter, null);
                ((TextView)additionalInfoView.findViewById(R.id.additionalInformationDescription)).setText(_tourDetails.ToursAdditionalInformations.get(i).AdditionalInformationType);
                ((TextView)additionalInfoView.findViewById(R.id.additionalInformationValue)).setText(_tourDetails.ToursAdditionalInformations.get(i).Value);
                additionnalInformationContainer.addView(additionalInfoView);
            }
        }
        else {
            additionnalInformationContainer.setVisibility(View.GONE);
            view.findViewById(R.id.descriptionContainer).setBackground(null);
        }

        return view;
    }
}
