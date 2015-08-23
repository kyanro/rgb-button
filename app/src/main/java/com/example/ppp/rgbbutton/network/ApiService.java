package com.example.ppp.rgbbutton.network;

import com.example.ppp.rgbbutton.model.ApiGetLedChika;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * api 叩くよ
 * memo: アクセストークン直書きだから気をつけてね！
 */
public class ApiService {
    volatile private static IgApiService singleton;

    private ApiService() {
    }

    public static IgApiService getIgApiService() {

        if (singleton == null) {
            synchronized (ApiService.class) {
                if (singleton == null) {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(30, TimeUnit.SECONDS);
                    client.setConnectTimeout(30, TimeUnit.SECONDS);

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setEndpoint("http://kyanroflashair")
                            .setClient(new OkClient(client))
                            .setLogLevel(RestAdapter.LogLevel.FULL)
                            .build();

                    singleton = restAdapter.create(IgApiService.class);
                }
            }
        }
        return singleton;
    }

    public interface IgApiService {
        @GET("/lchika2.lua")
        Observable<ApiGetLedChika> getLedChika(@Query("rgb") String rgb);
    }
}
