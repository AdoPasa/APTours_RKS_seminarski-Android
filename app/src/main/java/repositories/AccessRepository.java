package repositories;

import models.Access;
import models.ActivateAccount;
import models.Login;
import models.Register;
import models.ResetPassword;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AccessRepository {
    @POST("Access/Login")
    Call<Access> Login(@Body Login login);

    @GET("Access")
    Call<Access> Access(@Header("AuthToken") String authToken);

    @POST("Access/ActivateAccount")
    Call<ResponseBody> ActivateAccount(@Body ActivateAccount model);

    @POST("Access/Register")
    Call<ResponseBody> Register(@Body Register model);

    @POST("Access/SendResetPasswordCode")
    Call<ResponseBody> SendResetPasswordCode(@Body String email);

    @POST("Access/ResetPassword")
    Call<ResponseBody> ResetPassword(@Body ResetPassword model);
}
