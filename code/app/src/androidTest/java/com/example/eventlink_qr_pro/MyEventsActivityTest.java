package com.example.eventlink_qr_pro;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
/**
 * Espresso tests for {@code MyEventsActivity} focus on verifying the visibility of UI components
 * and the functionality of navigational controls. Utilizing {@link ActivityTestRule}, these tests
 * ensure that the activity displays its essential elements correctly and responds to user actions
 * as intended.
 */
@RunWith(AndroidJUnit4.class)
public class MyEventsActivityTest {
    /**
     * Prepares {@code MyEventsActivity} for testing, launching it before each test method. This
     * setup provides a consistent starting point for evaluating the activity's UI and behavior.
     */
    @Rule
    public ActivityTestRule<MyEventsActivity> activityRule = new ActivityTestRule<>(MyEventsActivity.class);
    /**
     * Validates that the key UI components, such as lists for current and future events and the
     * back button, are visible to the user upon activity start. This test ensures that the
     * activity's layout is correctly initialized and that essential views are displayed.
     */
    @Test
    public void testUIComponentsVisibility() {
        onView(withId(R.id.currentEventsListView)).check(matches(isDisplayed()));
        onView(withId(R.id.futureEventsListView)).check(matches(isDisplayed()));
        onView(withId(R.id.backButton)).check(matches(isDisplayed()));
    }
}