package com.example.myapplication;

import static org.junit.Assert.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import org.junit.Test;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** Regression coverage against the Node response shapes (not the old UX fixtures). */
public class ApiContractTest {
    private final Gson gson = new Gson();

    @Test
    public void scholarshipReadsCanonicalFieldsAndNumericId() {
        Scholarship item =
                gson.fromJson(
                        "{\"id\":73,\"title\":\"Test\",\"study_levels\":[\"bachelor\",\"master\"],\"majors\":[\"CNTT\"],\"type\":\"Toàn"
                            + " phần\",\"deadline\":\"2027-01-31T00:00:00.000Z\",\"application_link\":\"https://example.com/apply\",\"content_html\":\"<p>Real"
                            + " content</p>\"}",
                        Scholarship.class);
        assertEquals("73", item.getId());
        assertEquals("Cử nhân, Thạc sĩ", item.getDegreeLevel());
        assertEquals("CNTT", item.getFieldOfStudy());
        assertEquals("Toàn phần", item.getFundingType());
        assertEquals("31/01/2027", item.getDeadlineStr());
        assertEquals("https://example.com/apply", item.getApplicationLink());
        assertTrue(item.getContentHtml().contains("Real content"));
    }

    @Test
    public void nullScholarshipFieldsAreSafeAndNotInvented() {
        Scholarship item =
                gson.fromJson(
                        "{\"id\":1,\"deadline\":null,\"study_levels\":null,\"majors\":null}",
                        Scholarship.class);
        assertEquals("", item.getTitle());
        assertEquals("", item.getFieldOfStudy());
        assertNull(item.getDeadlineDate());
        assertEquals("Chưa công bố", item.getDeadlineStr());
    }

    @Test
    public void loginAcceptsSkillsArrayAndVerifiedRole() {
        ApiResponse<AuthData> envelope =
                gson.fromJson(
                        "{\"success\":true,\"data\":{\"token\":\"test-token\",\"user\":{\"id\":9,\"email\":\"test@example.com\",\"skills\":[\"Java\",\"SQL\"],\"role\":\"mentor\",\"is_mentor\":true}}}",
                        new TypeToken<ApiResponse<AuthData>>() {}.getType());
        assertEquals(2, envelope.getData().getUser().getSkills().size());
        assertTrue(envelope.getData().getUser().isVerifiedMentor());
    }

    @Test
    public void rankingUsesServerMetricsAndNullableRank() {
        MentorRankingItem mentor =
                gson.fromJson(
                        "{\"rank\":null,\"full_name\":\"Mentor"
                            + " A\",\"mentee_success_rate\":80.5,\"average_rating\":4.5,\"response_rate\":null,\"ranking_score\":null}",
                        MentorRankingItem.class);
        assertEquals("Mentor A", mentor.getMentorName());
        assertEquals("80.5%", mentor.getSuccessRate());
        assertEquals("4.5/5", mentor.getAvgRating());
        assertEquals(0, mentor.getRank());
        assertEquals("Chưa đủ dữ liệu", mentor.getTotal());
        assertEquals("Chưa đủ dữ liệu", mentor.getResponseRate());
    }

    private ApiService api(MockWebServer server) {
        return new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    @Test
    public void searchSendsAllFiltersAndKeepsEmptyResponseEmpty() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(
                    new MockResponse()
                            .setBody(
                                    "{\"success\":true,\"total\":0,\"totalPages\":0,\"data\":[]}"));
            ApiResponse<List<Scholarship>> response =
                    api(server)
                            .getScholarships(
                                    "test",
                                    "Hàn Quốc",
                                    "master",
                                    2,
                                    20,
                                    "title_asc",
                                    "Toàn phần",
                                    "CNTT",
                                    "2027-01-01",
                                    "2027-12-31")
                            .execute()
                            .body();
            RecordedRequest request = server.takeRequest(2, TimeUnit.SECONDS);
            assertNotNull(request);
            assertEquals("master", request.getRequestUrl().queryParameter("study_level"));
            assertEquals("2027-01-01", request.getRequestUrl().queryParameter("deadline_from"));
            assertEquals("2027-12-31", request.getRequestUrl().queryParameter("deadline_to"));
            assertEquals("Toàn phần", request.getRequestUrl().queryParameter("type"));
            assertEquals("CNTT", request.getRequestUrl().queryParameter("major"));
            assertEquals("title_asc", request.getRequestUrl().queryParameter("sort"));
            assertEquals("2", request.getRequestUrl().queryParameter("page"));
            assertTrue(response.getData().isEmpty());
            assertEquals(Integer.valueOf(0), response.getTotal());
        }
    }

    @Test
    public void bookmarkUsesRealIdAndBearerAndPreservesFailure() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(
                    new MockResponse()
                            .setResponseCode(404)
                            .setBody("{\"success\":false,\"message\":\"Not published\"}"));
            Response<ApiResponse<Object>> response =
                    api(server).saveScholarship("Bearer test-token", "73").execute();
            RecordedRequest request = server.takeRequest(2, TimeUnit.SECONDS);
            assertEquals("POST", request.getMethod());
            assertEquals("/api/users/saved/73", request.getPath());
            assertEquals("Bearer test-token", request.getHeader("Authorization"));
            assertFalse(response.isSuccessful());
        }
    }

    @Test
    public void profileWritesOnlyEditableFieldsAndArraySkills() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(
                    new MockResponse()
                            .setBody("{\"success\":true,\"data\":{\"skills\":[\"Java\"]}}"));
            Map<String, Object> body = new HashMap<>();
            body.put("full_name", "Student");
            body.put("skills", java.util.Arrays.asList("Java"));
            body.put("avatar_url", "");
            api(server).updateProfile("Bearer test-token", body).execute();
            com.google.gson.JsonObject json =
                    gson.fromJson(
                            server.takeRequest().getBody().readUtf8(),
                            com.google.gson.JsonObject.class);
            assertTrue(json.get("skills").isJsonArray());
            assertFalse(json.has("role"));
            assertFalse(json.has("id"));
            assertEquals(
                    "",
                    json.get("avatar_url").getAsString()); // Empty URL clears the server avatar.
        }
    }

    @Test
    public void resetUsesEmailTokenContract() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().setBody("{\"success\":true}"));
            Map<String, String> body = new HashMap<>();
            body.put("token", "a".repeat(64));
            body.put("new_password", "Password123");
            api(server).resetPassword(body).execute();
            RecordedRequest request = server.takeRequest();
            assertEquals("/api/auth/reset-password", request.getPath());
            assertTrue(request.getBody().readUtf8().contains("new_password"));
        }
    }

    @Test
    public void registrationOmitsUnsupportedOptionalUsername() {
        String json =
                gson.toJson(
                        new RegisterRequest(
                                "test@example.com", "Password123", "Student", null, "", ""));
        assertFalse(json.contains("username"));
    }
}
