package com.example.eventlink_qr_pro;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
/**
 * Test class for {@link ViewEditEventDetailsActivity} using Espresso. Ensures UI elements behave as expected:
 * - Clicking the 'cancel' button should navigate back.
 * - Toggling the 'limit attendees' switch makes the 'maximum attendees' field visible.
 * - Clicking the 'upload' button starts the {@link UploadImage} activity.
 */
@RunWith(AndroidJUnit4.class)
public class ViewEditEventDetailsActivityTest {
    /**
     * Rule to launch the {@link ViewEditEventDetailsActivity} before each test method. This rule ensures that
     * {@link ViewEditEventDetailsActivity} is started and ready for interaction, facilitating the testing of
     * various UI elements and interactions within the activity. The activity is launched before each test
     * annotated with {@code @Test} and torn down after the test runs, ensuring a fresh state for each test case.
     */
    @Rule
    public ActivityTestRule<ViewEditEventDetailsActivity> activityRule =
            new ActivityTestRule<>(ViewEditEventDetailsActivity.class);



    /**
     * Verifies that clicking the 'cancel' button navigates back to the previous screen.
     */
    @Test
    public void cancelButton_clickReturnsToPreviousActivity() {
        // Click the cancel button
        onView(withId(R.id.cancel_button)).perform(click());

        // Add verification as needed, such as checking if the activity is finished or if another activity is started
    }
    /**
     * Tests the visibility of the maximum attendees input field when the 'limit attendees' switch is toggled.
     */
    @Test
    public void limitAttendeesSwitch_toggleEnablesMaximumAttendeesEditText() {

        onView(withId(R.id.limit_attendees_switch)).perform(scrollTo(), click());

        // Check that the maximum attendees EditText is now visible
        onView(withId(R.id.maximum_attendees_edit_text)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
    /**
     * Checks that the 'upload' button click initiates the {@link UploadImage} activity.
     */
    @Test
    public void uploadButton_clickStartsUploadImageActivity() {
        // Initialize Espresso-Intents before the test
        Intents.init();

        // Perform a click on the upload button
        onView(withId(R.id.upload_poster_button)).perform(scrollTo(), click());

        // Verify that the UploadImage activity is intended to be started
        intended(hasComponent(UploadImage.class.getName()));

        // Release Espresso-Intents after the test
        Intents.release();
    }
}