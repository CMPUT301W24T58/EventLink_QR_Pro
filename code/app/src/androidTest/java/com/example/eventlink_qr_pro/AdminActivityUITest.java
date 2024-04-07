package com.example.eventlink_qr_pro;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AdminActivityUITest {

    @Rule
    public ActivityScenarioRule<AdminActivity> activityRule = new ActivityScenarioRule<>(AdminActivity.class);

    @Before
    public void setUp() {
        // Initialize Intents before each test
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Intents after each test to avoid IllegalStateException
        Intents.release();
    }

    @Test
    public void testNavigateToViewEvents() {
        onView(withId(R.id.view_events_button)).perform(ViewActions.click());
        Intents.intended(IntentMatchers.hasComponent(BrowseDeleteEventAdmin.class.getName()));
    }

    @Test
    public void testNavigateToViewProfiles() {
        onView(withId(R.id.view_profiles_button)).perform(ViewActions.click());
        Intents.intended(IntentMatchers.hasComponent(AdminAttendeeList.class.getName()));
    }

    @Test
    public void testNavigateToViewImages() {
        onView(withId(R.id.view_images_button)).perform(ViewActions.click());
        Intents.intended(IntentMatchers.hasComponent(EventListForAdminImage.class.getName()));
    }
}




