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
/**
 * Tests for the {@code AdminActivity} UI, verifying the correct navigation actions from the admin
 * dashboard to different sections of the app, including viewing events, profiles, and images.
 * These tests utilize Espresso for UI interactions and assertions, and Intents for verifying
 * intent-based navigations.
 * <p>
 * This class ensures that the appropriate activities are launched when the corresponding
 * buttons are clicked in the {@code AdminActivity} interface.
 * </p>
 */
@RunWith(AndroidJUnit4.class)
public class AdminActivityUITest {
    /**
     * Initializes {@code AdminActivity} before each test method. This rule ensures that
     * the activity is launched and terminated according to the lifecycle of the test,
     * providing a fresh instance for every test method.
     */
    @Rule
    public ActivityScenarioRule<AdminActivity> activityRule = new ActivityScenarioRule<>(AdminActivity.class);
    /**
     * Sets up Intents before each test. This is necessary for testing Android Intents
     * and ensuring that the correct activity transitions are happening.
     */
    @Before
    public void setUp() {
        // Initialize Intents before each test
        Intents.init();
    }
    /**
     * Cleans up and releases Intents after each test to avoid any potential
     * IllegalStateExceptions in subsequent tests.
     */
    @After
    public void tearDown() {
        // Release Intents after each test to avoid IllegalStateException
        Intents.release();
    }
    /**
     * Verifies navigation to the View Events screen from the Admin dashboard.
     * Asserts that the correct intent is launched when the View Events button is clicked.
     */
    @Test
    public void testNavigateToViewEvents() {
        onView(withId(R.id.view_events_button)).perform(ViewActions.click());
        Intents.intended(IntentMatchers.hasComponent(BrowseDeleteEventAdmin.class.getName()));
    }
    /**
     * Tests navigation to the View Profiles screen, ensuring the correct activity is started
     * upon clicking the View Profiles button in the Admin dashboard.
     */
    @Test
    public void testNavigateToViewProfiles() {
        onView(withId(R.id.view_profiles_button)).perform(ViewActions.click());
        Intents.intended(IntentMatchers.hasComponent(AdminAttendeeList.class.getName()));
    }
    /**
     * Confirms navigation to the View Images section, checking that the appropriate intent
     * is initiated when the View Images button is clicked in the Admin dashboard.
     */
    @Test
    public void testNavigateToViewImages() {
        onView(withId(R.id.view_images_button)).perform(ViewActions.click());
        Intents.intended(IntentMatchers.hasComponent(EventListForAdminImage.class.getName()));
    }
}




