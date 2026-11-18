package com.example.myapplication;

import static org.junit.Assert.*;

import android.content.Intent;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.BooleanSupplier;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, qualifiers = "w393dp-h852dp-mdpi")
@LooperMode(LooperMode.Mode.PAUSED)
public class MentorVerificationTest {
    private MockWebServer server;
    private Field retrofitField;
    private Retrofit original;
    private ActivityController<BecomeMentorActivity> listController;
    private ActivityController<SubmitVerificationActivity> formController;

    @Before
    public void setupApi() throws Exception {
        new TokenManager(RuntimeEnvironment.getApplication()).saveToken("fixture-verification-token");
        server = new MockWebServer();
        server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                if (request.getPath().startsWith("/api/scholarships")) {
                    return new MockResponse().setHeader("Content-Type", "application/json")
                            .setBody("{\"success\":true,\"total\":1,\"totalPages\":1,\"data\":["
                                    + "{\"id\":152,\"title\":\"Scholarship for verification\","
                                    + "\"organization\":\"Test University\"}]}");
                }
                return new MockResponse().setResponseCode(404);
            }
        });
        server.start();
        retrofitField = ApiClient.class.getDeclaredField("retrofit");
        retrofitField.setAccessible(true);
        original = (Retrofit) retrofitField.get(null);
        retrofitField.set(null, new Retrofit.Builder().baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create()).build());
    }

    @After
    public void cleanup() throws Exception {
        if (formController != null) formController.pause().stop().destroy();
        if (listController != null) listController.pause().stop().destroy();
        new TokenManager(RuntimeEnvironment.getApplication()).clear();
        retrofitField.set(null, original);
        server.shutdown();
    }

    private void await(BooleanSupplier ready) throws Exception {
        long deadline = System.nanoTime() + 10_000_000_000L;
        while (!ready.getAsBoolean() && System.nanoTime() < deadline) {
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            Thread.sleep(10);
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertTrue("Scholarship response did not reach the verification list", ready.getAsBoolean());
    }

    private BecomeMentorActivity openList() throws Exception {
        listController = Robolectric.buildActivity(BecomeMentorActivity.class).setup();
        BecomeMentorActivity activity = listController.get();
        RecyclerView list = activity.findViewById(R.id.recycler_scholarship_verify);
        await(() -> list.getAdapter().getItemCount() == 1);
        View root = activity.findViewById(R.id.main);
        root.measure(View.MeasureSpec.makeMeasureSpec(393, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(852, View.MeasureSpec.EXACTLY));
        root.layout(0, 0, 393, 852);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        return activity;
    }

    @Test
    public void verifyButtonOnApiLoadedScholarshipOpensTheCorrectPdfForm() throws Exception {
        BecomeMentorActivity activity = openList();
        RecyclerView list = activity.findViewById(R.id.recycler_scholarship_verify);
        RecyclerView.ViewHolder holder = list.findViewHolderForAdapterPosition(0);
        assertNotNull(holder);
        TextView verify = holder.itemView.findViewById(R.id.btn_verify_this);
        assertTrue(verify.isEnabled());
        assertTrue(verify.performClick());
        Intent intent = Shadows.shadowOf(activity).getNextStartedActivity();
        assertNotNull("Enabled Verify must open the submission form", intent);
        assertEquals(SubmitVerificationActivity.class.getName(), intent.getComponent().getClassName());
        ScholarshipVerifyItem selected = (ScholarshipVerifyItem)
                intent.getSerializableExtra(SubmitVerificationActivity.EXTRA_SCHOLARSHIP_ITEM);
        assertEquals("152", selected.getId());

        formController = Robolectric.buildActivity(SubmitVerificationActivity.class, intent).setup();
        SubmitVerificationActivity form = formController.get();
        assertEquals("Scholarship for verification", ((TextView)
                form.findViewById(R.id.tv_selected_scholarship_title)).getText().toString());
        assertEquals("Test University", ((TextView)
                form.findViewById(R.id.tv_selected_institution_name)).getText().toString());
        assertFalse("Submission requires a selected PDF", ((Button)
                form.findViewById(R.id.btn_submit_document)).isEnabled());
        assertTrue(form.findViewById(R.id.layout_upload_area).performClick());
        Intent picker = Shadows.shadowOf(form).getNextStartedActivityForResult().intent;
        assertEquals(Intent.ACTION_CHOOSER, picker.getAction());
        Intent document = picker.getParcelableExtra(Intent.EXTRA_INTENT);
        assertEquals(Intent.ACTION_GET_CONTENT, document.getAction());
        assertEquals("application/pdf", document.getType());
    }

    @Test
    public void pendingAndApprovedStayBlockedWhileRejectedAndUnverifiedCanOpenTheForm() throws Exception {
        BecomeMentorActivity activity = openList();
        ScholarshipVerifyItem item = new ScholarshipVerifyItem("152", "Test", "University", "Pending verification");
        ScholarshipVerifyAdapter adapter = new ScholarshipVerifyAdapter(Arrays.asList(item));
        ScholarshipVerifyAdapter.ViewHolder holder = adapter.onCreateViewHolder(new FrameLayout(activity), 0);
        for (String status : Arrays.asList("Pending verification", "Verified", "pending", "approved")) {
            item.setVerificationStatus(status);
            adapter.onBindViewHolder(holder, 0);
            assertFalse(status + " must stay disabled", holder.btnVerify.isEnabled());
            holder.btnVerify.performClick();
            assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
        }
        for (String status : Arrays.asList("Not verified", "rejected", "Chọn để nộp hồ sơ")) {
            item.setVerificationStatus(status);
            adapter.onBindViewHolder(holder, 0);
            assertTrue(holder.btnVerify.isEnabled());
            holder.btnVerify.performClick();
            assertNotNull(status + " must permit opening the form",
                    Shadows.shadowOf(activity).getNextStartedActivity());
        }
    }
}
