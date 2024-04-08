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

@RunWith(AndroidJUnit4.class)
public class ViewEventAttendeeActivityTest {

    @Rule
    public ActivityTestRule<ViewEventAttendeeActivity> activityRule = new ActivityTestRule<>(ViewEventAttendeeActivity.class);

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

    @Test
    public void testCancelButtonFunctionality() {
        // Simulate button click
        onView(withId(R.id.cancel_button_attendee)).perform(click());

    }


}