package ba.com.apdesign.aptours;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import models.TourAgenda;
import models.TourDetails;

public class TourDetailsAgendaFragment extends Fragment {
    public static final String ARG_TOUR = "ARG_TOUR";
    public static final String ARG_TOURDETAILS = "ARG_TOURDETAILS";
    private TourDetails _tourDetails;

    public static TourDetailsAgendaFragment newInstance(TourDetails tourDetails) {
        Bundle args = new Bundle();

        args.putSerializable(ARG_TOURDETAILS, tourDetails);

        TourDetailsAgendaFragment fragment = new TourDetailsAgendaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _tourDetails = (TourDetails)getArguments().getSerializable(ARG_TOURDETAILS);
    }

    public TourDetailsAgendaFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_details_agenda, container, false);
        LinearLayout agendaItemsContainer = (LinearLayout) view.findViewById(R.id.agendaItemsContainer);

        LayoutInflater vi = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View agendaView = null;

        if(_tourDetails.TourAgenda.size() != 0) {
            TourAgenda agendaItem;
            int currentDay = 0;

            for(int i = 0; i < _tourDetails.TourAgenda.size(); i++) {
                agendaItem = _tourDetails.TourAgenda.get(i);

                if(currentDay < agendaItem.Day) {
                    currentDay = agendaItem.Day;

                    agendaView = vi.inflate(R.layout.agenda_title_adapter, null);
                    ((TextView)agendaView.findViewById(R.id.agendaTitleText)).setText(currentDay  + ". " + getString(R.string.Day));
                    agendaItemsContainer.addView(agendaView);
                }

                agendaView = vi.inflate(R.layout.agenda_item_adapter, null);

                ((TextView)agendaView.findViewById(R.id.agendaItemDescription)).setText(_tourDetails.TourAgenda.get(i).Time);
                ((TextView)agendaView.findViewById(R.id.agendaItemValue)).setText(_tourDetails.TourAgenda.get(i).Value);

                agendaItemsContainer.addView(agendaView);
            }
        }
        else {
            view.findViewById(R.id.agendaUnavaibleText).setVisibility(View.VISIBLE);
        }

        return view;
    }
}
