package com.example.kandarp.zendesktest.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.example.kandarp.zendesktest.MainActivity.sHeaderAccept;

/**
 * Created by Kandarp on 12/5/2016.
 */

public interface UserListApi {
    @Headers
            ({sHeaderAccept})


    @GET
    ("/api/v2/users/show_many.json?")
    Call<Users> getAllUsers(@Header("Authorization") String token, @Query("ids")String list);
}
