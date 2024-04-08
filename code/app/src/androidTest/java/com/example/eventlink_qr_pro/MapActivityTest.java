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

@RunWith(AndroidJUnit4.class)
public class MapActivityTest {

    @Rule
    public ActivityTestRule<MapActivity> activityRule = new ActivityTestRule<>(MapActivity.class);

    @Test
    public void testButtonsVisibility() {
        onView(withId(R.id.btn_disable_tracking)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_enable_tracking)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_back)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackButtonFunctionality() {

        onView(withId(R.id.btn_back)).perform(click());

    }
}
