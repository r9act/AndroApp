package com.example.myapplication.retrofit;

import com.example.myapplication.DTO.UserDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/users") // Replace with your backend endpoint
    Call<Void> sendUser(@Body UserDto userDto);
}
