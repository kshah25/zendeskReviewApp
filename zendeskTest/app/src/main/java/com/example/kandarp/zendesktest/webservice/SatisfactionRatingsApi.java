package com.example.kandarp.zendesktest.webservice;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import static com.example.kandarp.zendesktest.MainActivity.sHeaderAccept;

/**
 * Created by Kandarp on 12/5/2016.
 */

public interface SatisfactionRatingsApi {

        @Headers
         ({sHeaderAccept})


        @GET
        ("/api/v2/satisfaction_ratings.json?score=received_with_comment&sort_order=desc")
       Call<SatisfactionRatings> getSatisfactionRatings(@Header("Authorization") String token );
    }
