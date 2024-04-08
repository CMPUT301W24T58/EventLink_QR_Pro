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
/**
 * Tests for {@code EventDetailActivity} using Espresso and Espresso Intents to verify UI components
 * and navigation intents. These tests ensure that event details are correctly displayed and that
 * buttons navigate to the correct activities.
 * <p>
 * Utilizes {@link androidx.test.espresso.intent.rule.IntentsTestRule} to initialize and release
 * Espresso Intents before and after each test, facilitating intent verification.
 * </p>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventDetailActivityTest {
    /**
     * Rule to launch {@code EventDetailActivity} with a specific intent, allowing tests
     * to verify the activity's behavior when populated with event details.
     */
    @Rule
    public IntentsTestRule<EventDetailActivity> mActivityRule = new IntentsTestRule<>(EventDetailActivity.class, true, false);
    /**
     * Sets up the environment for each test, preparing and launching {@code EventDetailActivity}
     * with an intent containing mock event details.
     */
    @Before
    public void setUp() {
        // Prepare intent with necessary extras
        Intent intent = new Intent();
        intent.putExtra("eventName", "Sample Event");
        mActivityRule.launchActivity(intent);
    }
    /**
     * Verifies that the event name is correctly displayed in the UI, based on the data provided
     * through the activity's intent.
     */
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
    /**
     * Tests the functionality of the back button in {@code EventDetailActivity}, ensuring it
     * performs the expected navigation or action.
     */
    @Test
    public void testBackButton() {
        onView(withId(R.id.btn_back)).perform(ViewActions.click());

    }
    /**
     * Verifies that the button for viewing and editing event details correctly navigates to
     * the {@code ViewEditEventDetailsActivity}.
     */
    @Test
    public void testNavigateToEditEventDetails() {
        onView(withId(R.id.btn_view_edit_details)).perform(ViewActions.click());

        Intents.intended(hasComponent(ViewEditEventDetailsActivity.class.getName()));
    }
    /**
     * Tests navigation to the attendee list screen when the view attendees button is clicked.
     */
    @Test
    public void testViewAttendeesButtonNavigatesCorrectly() {
        onView(withId(R.id.btn_view_attendees)).perform(ViewActions.click());

        Intents.intended(hasComponent(AttendeeList.class.getName()));
    }
    /**
     * Ensures that clicking the send notification button correctly navigates to the
     * {@code SendNotificationActivity}.
     */
    @Test
    public void testSendNotificationButtonNavigatesCorrectly() {
        onView(withId(R.id.btn_send_notification)).perform(ViewActions.click());

        Intents.intended(hasComponent(SendNotificationActivity.class.getName()));
    }
    /**
     * Verifies correct navigation to the map screen for event check-ins when the respective
     * button is clicked.
     */
    @Test
    public void testCheckInMapButtonNavigatesCorrectly() {
        onView(withId(R.id.btn_check_in_map)).perform(ViewActions.click());

        Intents.intended(hasComponent(MapActivity.class.getName()));
    }
    /**
     * Tests the share QR code button, ensuring it navigates to the {@code ShareQRCodeActivity}
     * as expected.
     */
    @Test
    public void testShareQRCodeButtonNavigatesCorrectly() {
        onView(withId(R.id.btn_share_qr_code)).perform(ViewActions.click());

        Intents.intended(hasComponent(ShareQRCodeActivity.class.getName()));
    }
    /**
     * Confirms that the alerts button correctly navigates to the {@code OrganizerAlerts} activity.
     */
    @Test
    public void testAlertsButtonNavigatesCorrectly() {
        onView(withId(R.id.btn_alerts)).perform(ViewActions.click());

        Intents.intended(hasComponent(OrganizerAlerts.class.getName()));
    }


}