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
/**
 * Espresso UI tests for {@code MainActivity}, verifying navigation intents to different activities
 * within the application. This class demonstrates the usage of {@link ActivityScenarioRule} for
 * launching the activity under test and Espresso Intents for intent verification.
 * <p>
 * The tests ensure that the appropriate activities are started upon clicking specific buttons
 * in {@code MainActivity}, validating the application's navigational logic.
 * </p>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    /**
     * Sets up {@link ActivityScenarioRule} for {@code MainActivity}, providing a functional
     * testing environment by launching the activity before each test method.
     */
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
    /**
     * Initializes Espresso Intents before each test to prepare for intent verification.
     */
    @Before
    public void setUp() {
        // Initialize Espresso-Intents before each test
        Intents.init();
    }
    /**
     * Cleans up and releases Espresso Intents after each test to avoid memory leaks
     * and ensure a clean state for subsequent tests.
     */
    @After
    public void tearDown() {
        // Release Espresso-Intents after each test
        Intents.release();
    }
    /**
     * Verifies that clicking the attendee button in {@code MainActivity} correctly starts
     * the {@code AttendeeActivity}. This test simulates user interaction and checks that the
     * intended activity is launched.
     */
    @Test
    public void navigateToAttendeeActivity() {
        // Perform click action on attendee_button
        onView(withId(R.id.attendee_button)).perform(click());
        // Verify that the AttendeeActivity is started
        intended(hasComponent(AttendeeActivity.class.getName()));
    }
    /**
     * Tests the navigation to {@code EventListActivity} from {@code MainActivity} by simulating
     * a click on the organizer button and verifying that the correct intent is sent to launch
     * the target activity.
     */
    @Test
    public void navigateToEventListActivity() {
        // Perform click action on organizer_button
        onView(withId(R.id.organizer_button)).perform(click());
        // Verify that the EventListActivity is started
        intended(hasComponent(EventListActivity.class.getName()));
    }
    /**
     * Verifies that clicking the "Administrator" button correctly starts the {@code AdminActivity}.
     * This test simulates a user interaction with the UI and checks the intent to ensure the target
     * activity is invoked. The use of Espresso Intents allows for precise verification of the component
     * that should be started as a result of the interaction.
     */
    @Test
    public void navigateToAdminActivity() {
        onView(withId(R.id.administrator_button)).perform(click());
        intended(hasComponent(AdminActivity.class.getName()));
    }


}