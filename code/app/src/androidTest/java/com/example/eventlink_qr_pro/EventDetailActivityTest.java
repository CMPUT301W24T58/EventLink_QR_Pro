package com.example.eventlink_qr_pro;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import android.content.Intent;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventDetailActivityTest {

    @Rule
    public IntentsTestRule<EventDetailActivity> mActivityRule = new IntentsTestRule<>(EventDetailActivity.class, true, false);

    @Before
    public void setUp() {
        // Prepare intent with necessary extras
        Intent intent = new Intent();
        intent.putExtra("eventName", "Sample Event");
        mActivityRule.launchActivity(intent);
    }

    public void testEventNameDisplayed() {

        final String expectedEventName = "Dynamic Event Name";

        // Create an Intent that contains the extra data
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), EventDetailActivity.class);
        intent.putExtra("eventName", expectedEventName); // Make sure the key matches what EventDetailActivity expects

        // Launch the EventDetailActivity with the Intent
        mActivityRule.launchActivity(intent);

        // Now check if the TextView shows the expected event name
        onView(withId(R.id.event_name_text_view))
                .check(matches(withText(expectedEventName)));
    }

    @Test
    public void testBackButton() {
        onView(withId(R.id.btn_back)).perform(ViewActions.click());

    }

    @Test
    public void testNavigateToEditEventDetails() {
        onView(withId(R.id.btn_view_edit_details)).perform(ViewActions.click());

        Intents.intended(hasComponent(ViewEditEventDetailsActivity.class.getName()));
    }
    @Test
    public void testViewAttendeesButtonNavigatesCorrectly() {
        onView(withId(R.id.btn_view_attendees)).perform(ViewActions.click());

        Intents.intended(hasComponent(AttendeeList.class.getName()));
    }

    @Test
    public void testSendNotificationButtonNavigatesCorrectly() {
        onView(withId(R.id.btn_send_notification)).perform(ViewActions.click());

        Intents.intended(hasComponent(SendNotificationActivity.class.getName()));
    }

    @Test
    public void testCheckInMapButtonNavigatesCorrectly() {
        onView(withId(R.id.btn_check_in_map)).perform(ViewActions.click());

        Intents.intended(hasComponent(MapActivity.class.getName()));
    }

    @Test
    public void testShareQRCodeButtonNavigatesCorrectly() {
        onView(withId(R.id.btn_share_qr_code)).perform(ViewActions.click());

        Intents.intended(hasComponent(ShareQRCodeActivity.class.getName()));
    }

    @Test
    public void testAlertsButtonNavigatesCorrectly() {
        onView(withId(R.id.btn_alerts)).perform(ViewActions.click());

        Intents.intended(hasComponent(OrganizerAlerts.class.getName()));
    }


}