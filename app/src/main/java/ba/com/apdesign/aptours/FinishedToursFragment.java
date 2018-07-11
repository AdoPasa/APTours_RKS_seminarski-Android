package ba.com.apdesign.aptours;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import helpers.AppConfiguration;
import helpers.GeneralHelper;
import helpers.RetrofitBuilder;
import helpers.adapters.TourCardAdapter;
import models.ToursDTO;
import repositories.ToursRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinishedToursFragment extends Fragment {
    //Controls
    private LinearLayout loadingContainer;
    private LinearLayout noResultsContainer;
    private Button loadAgainButton;
    private ListView toursListView;

    //Repository
    private ToursRepository toursService;

    //Properties
    private String authToken;

    //ToursLoadingStaff
    List<ToursDTO> loadedTours = new ArrayList<>();
    boolean loadedAllFlag = false;
    boolean loadingFlag = false;

    public FinishedToursFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finished_tours, container, false);
        bindControls(view);

        toursService = RetrofitBuilder.Build(getContext()).create(ToursRepository.class);

        SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.AuthTokenPreferencesFile), Context.MODE_PRIVATE);
        authToken = sharedPref.getString(getString(R.string.AuthTokenKey), null);

        if(authToken == null || authToken.trim() == "") {
            GeneralHelper.logoutUser(getContext());
            return null;
        }

        TourCardAdapter adapter = new TourCardAdapter(getContext(), R.id.ToursListView, loadedTours, authToken, false, true);

        toursListView.setAdapter(adapter);
        toursListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), TourDetailsActivity.class);

                intent.putExtra(AppConfiguration.TourKey, loadedTours.get(position));
                intent.putExtra(AppConfiguration.FilterKey, AppConfiguration.TourFlterFinished);
                intent.putExtra(AppConfiguration.TourFavoriteKey, loadedTours.get(position).Favorite);

                startActivity(intent);
            }
        });

        toursListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) { }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(loadingFlag || loadedAllFlag)
                    return;

                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0 && totalItemCount % AppConfiguration.ResultsPerPage == 0)
                    loadTours(((loadedTours.size()/AppConfiguration.ResultsPerPage) + 1), AppConfiguration.ResultsPerPage);
            }
        });

        loadTours(((loadedTours.size()/AppConfiguration.ResultsPerPage) + 1), AppConfiguration.ResultsPerPage);

        loadAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadedAllFlag = false;
                loadTours(((loadedTours.size()/AppConfiguration.ResultsPerPage) + 1), AppConfiguration.ResultsPerPage);
            }
        });

        return view;
    }

    private void bindControls(View v) {
        loadingContainer = v.findViewById(R.id.LoadingContainer);
        noResultsContainer = v.findViewById(R.id.NoResultsContainer);
        loadAgainButton = v.findViewById(R.id.LoadAgainButton);
        toursListView = v.findViewById(R.id.ToursListView);
    }

    private void loadTours(final int page, int numberOfResults) {
        if(loadingFlag || loadedAllFlag)
            return;

        loadingFlag = true;
        loadingContainer.setVisibility(View.VISIBLE);
        noResultsContainer.setVisibility(View.GONE);

        Call<List<ToursDTO>> toursCall = toursService.Tours(authToken, AppConfiguration.TourFlterFinished, page, numberOfResults);

        toursCall.enqueue(new Callback<List<ToursDTO>>() {
            @Override
            public void onResponse(Call<List<ToursDTO>> call, Response<List<ToursDTO>> response) {
                if(response.isSuccessful()) {
                    List<ToursDTO> currentTours = response.body();

                    if(currentTours.size() == 0)
                        loadedAllFlag = true;
                    else {
                        loadedTours.addAll(currentTours);
                        toursListView.requestLayout();
                    }
                }
                else if(response.code() == 401) {
                    GeneralHelper.logoutUser(getContext());
                    return;
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.ErrMsgFriendly), Toast.LENGTH_SHORT).show();
                }

                if(loadedTours == null || loadedTours.size() == 0)
                    noResultsContainer.setVisibility(View.VISIBLE);

                loadingContainer.setVisibility(View.GONE);
                loadingFlag = false;
            }

            @Override
            public void onFailure(Call<List<ToursDTO>> call, Throwable t) {
                if(page == 1)
                    noResultsContainer.setVisibility(View.VISIBLE);

                Toast.makeText(getContext(), getString(R.string.ErrMsgApiFailure), Toast.LENGTH_SHORT).show();
                loadingContainer.setVisibility(View.GONE);
                loadingFlag = false;
            }
        });
    }
}