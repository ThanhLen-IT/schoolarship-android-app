package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ApiService {

    @GET("health/midterm")
    Call<ApiResponse<Object>> checkHealth();

    @POST("api/auth/register")
    Call<ApiResponse<AuthData>> register(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<ApiResponse<AuthData>> login(@Body LoginRequest request);

    @POST("api/auth/forgot-password")
    Call<ApiResponse<Object>> forgotPassword(@Body java.util.Map<String, String> request);

    @POST("api/auth/reset-password")
    Call<ApiResponse<Object>> resetPassword(@Body java.util.Map<String, String> request);

    @GET("api/auth/me")
    Call<ApiResponse<UserProfile>> getCurrentUser(@Header("Authorization") String bearerToken);

    @PUT("api/users/profile")
    Call<ApiResponse<UserProfile>> updateProfile(
            @Header("Authorization") String bearerToken,
            @Body java.util.Map<String, Object> profile);

    @GET("api/scholarships")
    Call<ApiResponse<List<Scholarship>>> getScholarships(
            @Query("search") String search,
            @Query("country") String country,
            @Query("study_level") String studyLevel,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("sort") String sort,
            @Query("type") String type,
            @Query("major") String major,
            @Query("deadline_from") String deadlineFrom,
            @Query("deadline_to") String deadlineTo);

    @GET("api/scholarships/{id}")
    Call<ApiResponse<Scholarship>> getScholarshipDetail(@Path("id") String scholarshipId);

    @POST("api/users/saved/{scholarshipId}")
    Call<ApiResponse<Object>> saveScholarship(
            @Header("Authorization") String bearerToken,
            @Path("scholarshipId") String scholarshipId);

    @DELETE("api/users/saved/{scholarshipId}")
    Call<ApiResponse<Object>> unsaveScholarship(
            @Header("Authorization") String bearerToken,
            @Path("scholarshipId") String scholarshipId);

    @GET("api/users/saved")
    Call<ApiResponse<List<Scholarship>>> getSavedScholarships(
            @Header("Authorization") String bearerToken);

    @GET("api/mentors/ranking")
    Call<ApiResponse<List<MentorRankingItem>>> getMentorRanking(
            @Query("scholarship_id") int scholarshipId,
            @Query("page") Integer page,
            @Query("limit") Integer limit);

    @retrofit2.http.Multipart
    @POST("api/mentors/verify")
    Call<ApiResponse<Object>> submitVerification(
            @Header("Authorization") String token,
            @retrofit2.http.Part("scholarship_id") okhttp3.RequestBody scholarshipId,
            @retrofit2.http.Part okhttp3.MultipartBody.Part evidence);

    @GET("api/mentors/verify/status")
    Call<ApiResponse<VerificationData>> verificationStatus(
            @Header("Authorization") String token, @Query("scholarship_id") String scholarshipId);

    @GET("api/posts")
    Call<ApiResponse<List<CommunityPost>>> getPosts(
            @Query("search") String search, @Query("page") int page, @Query("limit") int limit);

    @POST("api/posts")
    Call<ApiResponse<CommunityPost>> createPost(
            @Header("Authorization") String token, @Body java.util.Map<String, Object> body);

    @POST("api/posts/{id}/like")
    Call<ApiResponse<Object>> likePost(
            @Header("Authorization") String token, @Path("id") String id);

    @GET("api/posts/{id}/comments")
    Call<ApiResponse<List<PostComment>>> getComments(
            @Path("id") String id, @Query("page") int page, @Query("limit") int limit);

    @POST("api/posts/{id}/comments")
    Call<ApiResponse<PostComment>> addComment(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body java.util.Map<String, String> body);

    @GET("api/mentoring/applications/mine")
    Call<ApiResponse<List<ApplicationData>>> myApplications(@Header("Authorization") String token);

    @GET("api/scholarships/filters")
    Call<ApiResponse<MobileData.Filters>> filters();

    @retrofit2.http.Multipart
    @POST("api/users/avatar")
    Call<ApiResponse<MobileData.Avatar>> uploadAvatar(
            @Header("Authorization") String token,
            @retrofit2.http.Part okhttp3.MultipartBody.Part avatar);

    @retrofit2.http.HTTP(method = "DELETE", path = "api/users/me", hasBody = true)
    Call<ApiResponse<Object>> closeAccount(
            @Header("Authorization") String token,
            @retrofit2.http.Body java.util.Map<String, String> body);

    @GET("api/chat/conversations")
    Call<ApiResponse<List<MobileData.Conversation>>> conversations(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("limit") int limit);

    @POST("api/chat/conversations")
    Call<ApiResponse<MobileData.Conversation>> conversation(
            @Header("Authorization") String token, @Body java.util.Map<String, String> body);

    @GET("api/chat/conversations/{id}/messages")
    Call<ApiResponse<List<MobileData.Message>>> messages(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Query("before_id") String before,
            @Query("after_id") String after,
            @Query("limit") int limit);

    @POST("api/chat/conversations/{id}/messages")
    Call<ApiResponse<MobileData.Message>> sendMessage(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body java.util.Map<String, String> body);

    @retrofit2.http.PATCH("api/chat/conversations/{id}/read")
    Call<ApiResponse<Object>> readConversation(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body java.util.Map<String, String> body);

    @GET("api/notifications")
    Call<ApiResponse<List<MobileData.Notice>>> notifications(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("limit") int limit);

    @retrofit2.http.PATCH("api/notifications/{id}/read")
    Call<ApiResponse<Object>> readNotice(
            @Header("Authorization") String token, @Path("id") String id);

    @retrofit2.http.PATCH("api/notifications/read-all")
    Call<ApiResponse<Object>> readNotices(@Header("Authorization") String token);

    @POST("api/support/tickets")
    Call<ApiResponse<MobileData.Ticket>> support(
            @Header("Authorization") String token, @Body java.util.Map<String, String> body);

    @GET("api/support/tickets")
    Call<ApiResponse<List<MobileData.Ticket>>> supportHistory(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("limit") int limit);

    @GET("api/mentors/{id}")
    Call<ApiResponse<MobileData.Mentor>> mentor(@Path("id") String id);

    @retrofit2.http.Multipart
    @POST("api/mentoring/applications/with-cv")
    Call<ApiResponse<ApplicationData>> apply(
            @Header("Authorization") String token,
            @retrofit2.http.Part("mentor_id") okhttp3.RequestBody mentor,
            @retrofit2.http.Part("scholarship_id") okhttp3.RequestBody scholarship,
            @retrofit2.http.Part("introduction") okhttp3.RequestBody intro,
            @retrofit2.http.Part okhttp3.MultipartBody.Part cv);

    @retrofit2.http.PATCH("api/mentoring/applications/{id}/progress")
    Call<ApiResponse<ApplicationData>> progress(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body java.util.Map<String, String> body);

    @GET("api/mentoring/applications/{id}/response-requests")
    Call<ApiResponse<List<MobileData.Request>>> responseRequests(
            @Header("Authorization") String token, @Path("id") String id);

    @POST("api/mentoring/applications/{id}/response-requests")
    Call<ApiResponse<MobileData.Request>> responseRequest(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body java.util.Map<String, String> body);

    @retrofit2.http.PATCH("api/mentoring/response-requests/{id}/respond")
    Call<ApiResponse<MobileData.Request>> respond(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body java.util.Map<String, String> body);

    @POST("api/mentors/{id}/reviews")
    Call<ApiResponse<Object>> review(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body java.util.Map<String, Object> body);

    @retrofit2.http.Streaming
    @GET("api/mentoring/applications/{id}/cv")
    Call<okhttp3.ResponseBody> cv(@Header("Authorization") String token, @Path("id") String id);
}
