package helpers.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ba.com.apdesign.aptours.R;
import ba.com.apdesign.aptours.TourDetailsAgendaFragment;
import ba.com.apdesign.aptours.TourDetailsOverviewFragment;
import ba.com.apdesign.aptours.TourDetailsReviewsFragment;
import helpers.RetrofitBuilder;
import helpers.myWidgets.WrapContentHeightViewPager;
import models.TourDetails;
import models.ToursDTO;
import repositories.ToursRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourDetailsTabsAdapter extends FragmentPagerAdapter {
    final int TabCount = 3;
    View _rootView;
    private ToursRepository _toursService;
    private TourDetails _tourDetails;
    private static int _previusTabHeight = 0;

    private String tabTitles[] = new String[] { //<--- naslov taba (dugme)
            "Detalji",
            "Agenda",
            "Recenzije"
    };

    private Context context;
    private int mCurrentPosition = -1;
    private ToursDTO _tour;

    public TourDetailsTabsAdapter(FragmentManager fm, final Context context, ToursDTO tour, String authToken) {
        super(fm);
        this.context = context;
        _tour = tour;

        _rootView = ((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);

        _toursService = RetrofitBuilder.Build(context).create(ToursRepository.class);
        Call<TourDetails> call = _toursService.Details(authToken, tour.TourID);

        call.enqueue(new Callback<TourDetails>() {
            @Override
            public void onResponse(Call<TourDetails> call, Response<TourDetails> response) {
                if(response.isSuccessful()) {
                    _rootView.findViewById(R.id.TourDetailTabs).setVisibility(View.VISIBLE);
                    _rootView.findViewById(R.id.ViewPager).setVisibility(View.VISIBLE);
                    _tourDetails = response.body();

                    getItem(0);
                }
                else Toast.makeText(context, context.getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();

                _rootView.findViewById(R.id.DataLoadingProgress).setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<TourDetails> call, Throwable t) {
                Toast.makeText(context, context.getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                _rootView.findViewById(R.id.DataLoadingProgress).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getCount() {
        return TabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) { // <-- Postavljanje fragmenta u tab
            case 0:
                return TourDetailsOverviewFragment.newInstance(_tour, _tourDetails);

            case 1:
                return TourDetailsAgendaFragment.newInstance(_tourDetails);

            case 2:
                return TourDetailsReviewsFragment.newInstance(_tour, _tourDetails);
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (position != mCurrentPosition) {
            Fragment fragment = (Fragment) object;
            WrapContentHeightViewPager pager = (WrapContentHeightViewPager) container;
            if (fragment != null && fragment.getView() != null) {
                mCurrentPosition = position;
                pager.measureCurrentView(fragment.getView());
            }
        }
    }
}