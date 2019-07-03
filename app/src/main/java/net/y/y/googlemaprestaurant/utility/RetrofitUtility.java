package net.y.y.googlemaprestaurant.utility;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApi;

import net.y.y.googlemaprestaurant.R;
import net.y.y.googlemaprestaurant.network.APIInterface;
import net.y.y.googlemaprestaurant.network.NetworkConnection;
import net.y.y.googlemaprestaurant.network.RetrofitCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class RetrofitUtility {

    private Context context;

    public RetrofitUtility(Context context) {
        this.context = context;
    }

    public void apiGetMapPlaces(
            String location,
            String radius,
            String types,
            RetrofitCallback.RequestListener requestListener
    ) {
        APIInterface apiInterface = NetworkConnection.getClient().create(APIInterface.class);
        Call call = apiInterface.getPlace(mapPlaceRequestBody(
                location,
                radius,
                types));
        new RetrofitCallback(context, call, requestListener);
    }

    private Map<String, String> mapPlaceRequestBody(
            String location,
            String radius,
            String types
    ) {

        Map<String, String> map = new HashMap<>();
        map.put("location", location);
        map.put("radius", radius);
        map.put("types", types);
        map.put("key", context.getString(R.string.google_maps_key));

        return map;
    }

}
