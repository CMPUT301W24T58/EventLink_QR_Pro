package com.example.eventlink_qr_pro;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;
/**
 * Espresso tests for {@code MapActivity} to verify the visibility of control buttons and
 * the functionality of the back button. This class uses {@link ActivityTestRule} to launch
 * {@code MapActivity} for testing.
 * <p>
 * The tests ensure that essential UI components are displayed as expected when the activity
 * starts and that navigation actions, such as pressing the back button, perform correctly.
 * </p>
 */
@RunWith(AndroidJUnit4.class)
public class MapActivityTest {
    /**
     * Launches {@code MapActivity} before each test method. This setup provides a consistent
     * testing environment, ensuring the activity is in its initial state for every test.
     */
    @Rule
    public ActivityTestRule<MapActivity> activityRule = new ActivityTestRule<>(MapActivity.class);
    /**
     * Verifies that the buttons for disabling tracking, enabling tracking, and going back
     * are all visible to the user upon activity launch. This test checks the initial UI
     * state of {@code MapActivity} for the presence of these essential controls.
     */
    @Test
    public void testButtonsVisibility() {
        onView(withId(R.id.btn_disable_tracking)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_enable_tracking)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_back)).check(matches(isDisplayed()));
    }
    /**
     * Tests the functionality of the back button within {@code MapActivity}. This test simulates
     * a user clicking the back button and verifies that the correct navigational action is taken.
     * Note: The actual navigation verification might require additional implementation to check
     * whether the activity finishes or navigates to the expected destination.
     */
    @Test
    public void testBackButtonFunctionality() {
        onView(withId(R.id.btn_back)).perform(click());

    }
}
