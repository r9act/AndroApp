package com.example.myapplication.retrofit;

import com.example.myapplication.DTO.PersonBirthdayDto;
import com.example.myapplication.DTO.UserDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/users")
    Call<Long> saveUserToRemote(@Body UserDto userDto);

    @POST("api/birthdays/{foreignId}")
    Call<Void> saveUserBirthdays(@Path("foreignId") Long foreignId, @Body List<PersonBirthdayDto> userDto);
}
