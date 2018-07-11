package repositories;

import java.util.ArrayList;
import java.util.List;

import models.TourDates;
import models.TourDetails;
import models.TourReservations;
import models.TourReview;
import models.ToursDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ToursRepository {
    @GET("Tours")
    Call<List<ToursDTO>> Tours(@Header("AuthToken")      String authToken,
                               @Query("filter")          String filter,
                               @Query("page")            int page,
                               @Query("numberOfResults") int numberOfResults);

    @GET("Tours/AddToFavorite")
    Call<ResponseBody> AddToFavorite(@Header("AuthToken") String authToken, @Query("tourId") int tourId);

    @GET("Tours/RemoveFromFavorite")
    Call<ResponseBody> RemoveFromFavorite(@Header("AuthToken") String authToken, @Query("tourId") int tourId);

    @GET("Tours/Details")
    Call<TourDetails> Details(@Header("AuthToken") String authToken, @Query("tourId") int tourId);

    @POST("Tours/{tourId}/addReview")
    Call<ResponseBody> AddReview(@Header("AuthToken") String authToken, @Path("tourId") int tourId, @Body TourReview model);

    @GET("tours/{tourId}/dates")
    Call<ArrayList<TourDates>> GetDates(@Header("AuthToken") String authToken, @Path("tourId") int tourId);

    @POST("Tours/{tourId}/addReservation")
    Call<ResponseBody> AddReservation(@Header("AuthToken") String authToken, @Path("tourId") int tourId, @Body TourReservations model);

    @POST("Tours/{tourId}/cancelReservation/{reservationId}")
    Call<ResponseBody> CancelReservation(@Header("AuthToken") String authToken, @Path("tourId") int tourId, @Path("reservationId") int reservationId);
}