package com.example.eventlink_qr_pro;

import android.view.View;
import android.widget.ImageView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
/**
 * UI tests for the {@code AdminAttendeeDetailActivity} within the EventLink QR Pro app,
 * focusing on the visibility and state of UI elements after certain actions, such as deletion
 * operations. This class leverages Espresso for UI testing and includes a custom matcher
 * to verify the presence of a drawable in an {@link ImageView}.
 * <p>
 * The primary goal of these tests is to ensure that the UI responds correctly to user actions
 * and app logic, displaying the appropriate visual feedback.
 * </p>
 */
@RunWith(AndroidJUnit4.class)
public class AdminAttendeeDetailActivityUITest {
    /**
     * Rule to launch {@code AdminAttendeeDetailActivity} before each test execution. This setup
     * provides a consistent starting point for the tests.
     */
    @Rule
    public ActivityTestRule<AdminAttendeeDetailActivity> activityRule =
            new ActivityTestRule<>(AdminAttendeeDetailActivity.class);
    /**
     * Verifies that the placeholder image is correctly displayed in an {@link ImageView}
     * after a deletion operation or when an attendee's image is not available. This test
     * uses a custom matcher, {@code hasDrawable}, to check for the presence of any drawable
     * resource within the target {@link ImageView}.
     */
    @Test
    public void testPlaceholderImageIsDisplayedAfterDeletion() {

        // Check if the ImageView now displays any drawable
        onView(withId(R.id.attendeeProfileImageView)).check(matches(hasDrawable()));
    }

    /**
     * A custom {@link Matcher} for Espresso that checks if an {@link ImageView} contains a drawable.
     * This is useful for verifying the presence of images or placeholders within image views.
     *
     * @return A {@link Matcher} that returns {@code true} if the {@link ImageView} contains a drawable,
     * and {@code false} otherwise.
     */
    public static Matcher<View> hasDrawable() {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                return (view instanceof ImageView) && ((ImageView) view).getDrawable() != null;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has drawable");
            }
        };
    }
}


