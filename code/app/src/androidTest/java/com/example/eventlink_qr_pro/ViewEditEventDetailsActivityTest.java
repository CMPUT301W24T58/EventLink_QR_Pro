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

@RunWith(AndroidJUnit4.class)
public class ViewEditEventDetailsActivityTest {

    @Rule
    public ActivityTestRule<ViewEditEventDetailsActivity> activityRule =
            new ActivityTestRule<>(ViewEditEventDetailsActivity.class);




    @Test
    public void cancelButton_clickReturnsToPreviousActivity() {
        // Click the cancel button
        onView(withId(R.id.cancel_button)).perform(click());

        // Add verification as needed, such as checking if the activity is finished or if another activity is started
    }
    @Test
    public void limitAttendeesSwitch_toggleEnablesMaximumAttendeesEditText() {
        // Assuming the switch starts in the OFF position
        onView(withId(R.id.limit_attendees_switch)).perform(scrollTo(), click());

        // Check that the maximum attendees EditText is now visible
        onView(withId(R.id.maximum_attendees_edit_text)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

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