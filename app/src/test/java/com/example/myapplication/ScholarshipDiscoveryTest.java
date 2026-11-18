package com.example.myapplication;

import static org.junit.Assert.*;

import android.os.Looper;
import android.content.Intent;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
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
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.lang.reflect.Field;
import java.util.function.BooleanSupplier;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, qualifiers = "w393dp-h852dp-mdpi")
@LooperMode(LooperMode.Mode.PAUSED)
public class ScholarshipDiscoveryTest {
    private MockWebServer server;
    private Retrofit original;
    private Field retrofitField;
    private ActivityController<ScholarshipDiscoveryActivity> controller;
    private ActivityController<HomeActivity> homeController;

    @Before
    public void setupApi() throws Exception {
        server = new MockWebServer();
        server.setDispatcher(new Dispatcher() {
            @Override public MockResponse dispatch(RecordedRequest request) {
                String body;
                if (request.getPath().startsWith("/api/scholarships/filters")) {
                    body = "{\"success\":true,\"data\":{\"countries\":[\"Japan\"],\"types\":[\"full\"],\"majors\":[],\"study_levels\":[\"bachelor\"]}}";
                } else {
                    StringBuilder rows = new StringBuilder();
                    for (int i = 1; i <= 6; i++) {
                        if (i > 1) rows.append(',');
                        rows.append("{\"id\":").append(i).append(",\"title\":\"Scholarship ").append(i)
                            .append("\",\"country\":\"Japan\",\"study_levels\":[\"bachelor\"]}");
                    }
                    body = "{\"success\":true,\"total\":6,\"totalPages\":1,\"data\":[" + rows + "]}";
                }
                return new MockResponse().setHeader("Content-Type", "application/json").setBody(body);
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
        if (controller != null) controller.pause().stop().destroy();
        if (homeController != null) homeController.pause().stop().destroy();
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
        assertTrue("API result did not reach the screen", ready.getAsBoolean());
    }

    private RecyclerView loadDiscovery(Intent intent) throws Exception {
        controller = Robolectric.buildActivity(ScholarshipDiscoveryActivity.class, intent).setup();
        ScholarshipDiscoveryActivity activity = controller.get();
        RecyclerView list = activity.findViewById(R.id.recycler_scholarship_results);
        await(() -> list.getAdapter().getItemCount() == 8);
        View root = activity.findViewById(R.id.main);
        root.measure(View.MeasureSpec.makeMeasureSpec(393, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(852, View.MeasureSpec.EXACTLY));
        root.layout(0, 0, 393, 852);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertFalse(activity.isFinishing());
        return list;
    }

    private void swipe(RecyclerView list, float fromY, float toY) {
        long start = SystemClock.uptimeMillis();
        for (int i = 0; i <= 12; i++) {
            int action = i == 0 ? MotionEvent.ACTION_DOWN : i == 12 ? MotionEvent.ACTION_UP : MotionEvent.ACTION_MOVE;
            MotionEvent event = MotionEvent.obtain(start, start + i * 40, action,
                195, fromY + (toY - fromY) * i / 12, 0);
            list.dispatchTouchEvent(event);
            event.recycle();
        }
        list.stopScroll();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
    }

    @Test
    public void openingDiscoveryAndSwipingToSixthResultDoesNotCrash() throws Exception {
        RecyclerView list = loadDiscovery(new Intent());
        for (int i = 0; i < 3; i++) swipe(list, 780, 100);
        assertNotNull("Sixth scholarship must be reachable", list.findViewHolderForAdapterPosition(6));
        for (int i = 0; i < 3; i++) swipe(list, 100, 780);
        assertNotNull("Filter header must be reachable again", list.findViewHolderForAdapterPosition(0));
    }

    @Test
    public void homeViewAllOpensDiscoveryWithLoadedResults() throws Exception {
        homeController = Robolectric.buildActivity(HomeActivity.class).setup();
        HomeActivity home = homeController.get();
        TextView viewAll = home.findViewById(R.id.tv_view_all_scholarships);
        await(() -> "Xem tất cả".contentEquals(viewAll.getText()));
        assertTrue(viewAll.performClick());
        Intent intent = Shadows.shadowOf(home).getNextStartedActivity();
        assertNotNull(intent);
        assertEquals(ScholarshipDiscoveryActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(8, loadDiscovery(intent).getAdapter().getItemCount());
    }
}
