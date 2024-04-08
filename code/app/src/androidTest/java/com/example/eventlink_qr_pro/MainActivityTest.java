package com.example.eventlink_qr_pro;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Rule;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    // Use ActivityScenarioRule for general purposes or IntentsTestRule for testing Intents specifically.
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // Initialize Espresso-Intents before each test
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Espresso-Intents after each test
        Intents.release();
    }

    @Test
    public void navigateToAttendeeActivity() {
        // Perform click action on attendee_button
        onView(withId(R.id.attendee_button)).perform(click());
        // Verify that the AttendeeActivity is started
        intended(hasComponent(AttendeeActivity.class.getName()));
    }

    @Test
    public void navigateToEventListActivity() {
        // Perform click action on organizer_button
        onView(withId(R.id.organizer_button)).perform(click());
        // Verify that the EventListActivity is started
        intended(hasComponent(EventListActivity.class.getName()));
    }


}