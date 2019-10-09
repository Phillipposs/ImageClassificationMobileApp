package com.example.imageclassificationapp;
import com.example.imageclassificationapp.model.User;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitCalls {
    @Multipart
    @POST(Urls.sendImageForClassification)
    Call<String> sendImageForClassification(@Part MultipartBody.Part file, @Part("userName") RequestBody requestBody);

    @Multipart
    @POST(Urls.sendImagesForClassification)
    Call<String> sendImagesForClassification(@Part List<MultipartBody.Part> files, @Part("userName") RequestBody requestBody);

    @GET(Urls.getResult)
    Call<String> getImageClassificationPrediction(String token);

    @GET(Urls.getResult)
    Call<String> getResult(@Query("userName") String userName);

    @POST(Urls.login)
    Call<String> userLogin(@Body User user);
}
