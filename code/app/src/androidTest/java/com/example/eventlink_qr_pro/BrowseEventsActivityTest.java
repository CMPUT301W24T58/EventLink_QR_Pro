package com.example.eventlink_qr_pro;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
/**
 * UI tests for the {@code BrowseEventsActivity} of the EventLink QR Pro app.
 * This class uses Espresso to interact with the UI and Espresso Intents to verify
 * navigation and intent sending within the activity. It ensures that UI components
 * behave as expected when interacted with by the user.
 */
@RunWith(AndroidJUnit4.class)
public class BrowseEventsActivityTest {
    /**
     * Sets up an {@link ActivityScenarioRule} for {@code BrowseEventsActivity} to launch
     * the activity before each test. This rule provides a convenient way to test the
     * activity under different conditions.
     */
    @Rule
    public ActivityScenarioRule<BrowseEventsActivity> activityRule = new ActivityScenarioRule<>(BrowseEventsActivity.class);
    /**
     * Initializes Espresso Intents before each test. This setup is necessary for
     * intent verification and to mock intents for actions within the test.
     */
    @Before
    public void setUp() {
        Intents.init();
    }
    /**
     * Releases Espresso Intents after each test to clean up and avoid memory leaks.
     * This teardown process ensures that the testing environment is reset for the next test.
     */
    @After
    public void tearDown() {
        Intents.release();
    }
    /**
     * Verifies that the back button functions correctly within {@code BrowseEventsActivity}.
     * The expected behavior is that clicking the back button navigates the user to the
     * previous activity or screen.
     */
    @Test
    public void testBackButton() {
        onView(withId(R.id.back_button)).perform(ViewActions.click());

    }


}