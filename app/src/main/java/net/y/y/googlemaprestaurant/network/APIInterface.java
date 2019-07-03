package net.y.y.googlemaprestaurant.network;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface APIInterface {

    @GET("place/nearbysearch/json?")
    Call<ResponseBody> getPlace(@QueryMap Map<String, String> map);

}
