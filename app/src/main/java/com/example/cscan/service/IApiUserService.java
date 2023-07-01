package com.example.cscan.service;

import com.example.cscan.models.ChangePasswordRequest;
import com.example.cscan.models.GroupImage;
import com.example.cscan.models.Images;
import com.example.cscan.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IApiUserService {

    IApiUserService apiService = new Retrofit.Builder()
            .baseUrl("http://192.168.142.241:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IApiUserService.class);

    @POST("api/Users/Register")
    Call<User> registerUser(@Body User user);

    @POST("api/Users/Login")
    Call<User> login(@Body User user);

    @POST("api/Users/ChangePassword")
    Call<Void> changePassword(@Body ChangePasswordRequest changePasswordRequest);
    @POST("api/GroupImages/InsertGroup")
    Call<GroupImage> insertGroup(@Body GroupImage groupImage);

    @PUT("api/GroupImages/updateGroup")
    Call<GroupImage> updateGroup(@Body GroupImage groupImage);

    @DELETE("api/GroupImages/deleteGroupImage/{groupId}")
    Call<Void> deleteGroup(@Path("groupId") int groupId);
    @POST("api/Images/InsertImage")
    Call<Images> insertImage(@Body Images imgaes);

    @GET("api/Images/getAllImage/{groupId}")
    Call<List<Images>> getAllImages(@Path("groupId") int groupId);

    @GET("api/GroupImages/getAllGroup/{userId}")
    Call<List<GroupImage>> getAllGroup(@Path("userId") int userId);
    @DELETE("api/Images/deleteImage/{imageId}")
    Call<Void> deleteImage(@Path("imageId") int imageId);
}

