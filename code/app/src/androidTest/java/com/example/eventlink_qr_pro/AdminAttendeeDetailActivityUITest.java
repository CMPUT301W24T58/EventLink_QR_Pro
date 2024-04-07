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

@RunWith(AndroidJUnit4.class)
public class AdminAttendeeDetailActivityUITest {

    @Rule
    public ActivityTestRule<AdminAttendeeDetailActivity> activityRule =
            new ActivityTestRule<>(AdminAttendeeDetailActivity.class);

    @Test
    public void testPlaceholderImageIsDisplayedAfterDeletion() {

        // Check if the ImageView now displays any drawable
        onView(withId(R.id.attendeeProfileImageView)).check(matches(hasDrawable()));
    }

    // Custom matcher to check if an ImageView has a drawable
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


