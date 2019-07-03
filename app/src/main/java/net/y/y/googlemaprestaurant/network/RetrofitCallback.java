package net.y.y.googlemaprestaurant.network;

import android.content.Context;
import android.util.Log;

import net.y.y.googlemaprestaurant.utility.NetworkConnectUtility;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitCallback {

    public String TAG = getClass().getSimpleName();
    private Context context = null;
    private RequestListener apiRequestListener = null;
    JSONObject jsonObject;
    private Call call;


    public RetrofitCallback(Context context, Call call, RequestListener apiRequestListener) {
        this.apiRequestListener = apiRequestListener;
        this.context = context;
        this.call = call;

        CallRequest(call, context);
    }

    public void CallRequest(Call call, final Context context) {

        if (!NetworkConnectUtility.checkConnected(context)) {
            apiRequestListener.onFailure("Network connection fail");
        }

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    try {
                        String getResponse = response.body().string();
                        jsonObject = new JSONObject(getResponse);

                        Log.d(TAG, "getResponse: " + getResponse);

                        apiRequestListener.onResult(jsonObject.getString("status"), jsonObject);
                    } catch (Exception e) {
                        Log.e(TAG, "unexpected exception occurs ", e);
                        apiRequestListener.onFailure(e.getMessage());
                    }
                    return;

                } else {
                    Log.e(TAG, "Error in response");
                    apiRequestListener.onFailure(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e(TAG, "onFailure", t);

                apiRequestListener.onFailure(t.getMessage());
            }
        });
    }


    public interface RequestListener {

        void onFailure(String errorMessage);

        void onResult(String status, JSONObject jsonObject);
    }


}
