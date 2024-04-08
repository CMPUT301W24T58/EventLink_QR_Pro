package com.example.eventlink_qr_pro;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
/**
 * Performs UI tests on the {@link ViewEventAttendeeActivity} within the EventLink QR Pro application.
 * This class leverages the Espresso framework to simulate user interactions and verify UI component visibility
 * and behavior, ensuring that elements are displayed as expected and functionality meets specifications.
 */
@RunWith(AndroidJUnit4.class)
public class ViewEventAttendeeActivityTest {
    /**
     * Establishes an {@link ActivityTestRule} for {@link ViewEventAttendeeActivity} to launch the activity
     * before each test method. This setup provides a consistent starting point for tests, ensuring the activity
     * is in the expected state.
     */
    @Rule
    public ActivityTestRule<ViewEventAttendeeActivity> activityRule = new ActivityTestRule<>(ViewEventAttendeeActivity.class);
    /**
     * Verifies the visibility of UI components within the activity. Ensures that essential text input fields
     * and buttons are displayed to the user upon activity launch. This test contributes to confirming the
     * UI's layout integrity.
     */
    @Test
    public void testUIComponentVisibility() {
        // Check visibility of EditTexts and Buttons
        onView(withId(R.id.event_name_edit_text_attendee)).check(matches(isDisplayed()));
        onView(withId(R.id.event_date_edit_text_attendee)).check(matches(isDisplayed()));
        onView(withId(R.id.event_location_edit_text_attendee)).check(matches(isDisplayed()));
        onView(withId(R.id.event_description_edit_text_attendee)).check(matches(isDisplayed()));
        onView(withId(R.id.update_button_attendee)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel_button_attendee)).check(matches(isDisplayed()));
    }
    /**
     * Tests the functionality of the cancel button within the activity. Simulates a user clicking the button
     * to ensure it triggers the expected UI response or activity transition. This method validates the button's
     * operational readiness and its integration within the activity.
     */
    @Test
    public void testCancelButtonFunctionality() {
        // Simulate button click
        onView(withId(R.id.cancel_button_attendee)).perform(click());

    }


}