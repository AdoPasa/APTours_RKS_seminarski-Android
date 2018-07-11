package repositories;

import models.ProfilePhoto;
import models.UpdateProfile;
import models.Users;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UsersRepository {
    @GET("Users/Profile")
    Call<Users> GetProfile(@Header("AuthToken") String authToken);

    @POST("Users/Profile")
    Call<ResponseBody> UpdateProfile(@Header("AuthToken") String authToken, @Body UpdateProfile model);

    @POST("Users/ProfilePhoto")
    Call<String> UpdateProfilePhoto(@Header("AuthToken") String authToken, @Body ProfilePhoto model);
}
