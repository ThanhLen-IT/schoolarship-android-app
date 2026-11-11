package com.example.myapplication;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("health/midterm")
    Call<ApiResponse<Object>> checkHealth();

    @POST("api/auth/register")
    Call<ApiResponse<AuthData>> register(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<ApiResponse<AuthData>> login(@Body LoginRequest request);

    @GET("api/auth/me")
    Call<ApiResponse<UserProfile>> getCurrentUser(@Header("Authorization") String bearerToken);

    @PUT("api/users/profile")
    Call<ApiResponse<UserProfile>> updateProfile(
            @Header("Authorization") String bearerToken,
            @Body UserProfile profile
    );

    @GET("api/scholarships")
    Call<ApiResponse<List<Scholarship>>> getScholarships(
            @Query("search") String search,
            @Query("country") String country,
            @Query("study_level") String studyLevel,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("sort") String sort
    );

    @GET("api/scholarships/{id}")
    Call<ApiResponse<Scholarship>> getScholarshipDetail(@Path("id") String scholarshipId);

    @POST("api/users/saved/{scholarshipId}")
    Call<ApiResponse<Object>> saveScholarship(
            @Header("Authorization") String bearerToken,
            @Path("scholarshipId") String scholarshipId
    );

    @DELETE("api/users/saved/{scholarshipId}")
    Call<ApiResponse<Object>> unsaveScholarship(
            @Header("Authorization") String bearerToken,
            @Path("scholarshipId") String scholarshipId
    );

    @GET("api/users/saved")
    Call<ApiResponse<List<Scholarship>>> getSavedScholarships(@Header("Authorization") String bearerToken);

    @GET("api/mentors/ranking")
    Call<ApiResponse<List<MentorRankingItem>>> getMentorRanking(
            @Query("scholarship_id") int scholarshipId,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );
}
