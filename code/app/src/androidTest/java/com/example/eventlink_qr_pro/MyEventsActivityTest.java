package com.example.eventlink_qr_pro;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class MyEventsActivityTest {

    @Rule
    public ActivityTestRule<MyEventsActivity> activityRule = new ActivityTestRule<>(MyEventsActivity.class);

    @Test
    public void testUIComponentsVisibility() {
        onView(withId(R.id.currentEventsListView)).check(matches(isDisplayed()));
        onView(withId(R.id.futureEventsListView)).check(matches(isDisplayed()));
        onView(withId(R.id.backButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackButtonFunctionality() {

        onView(withId(R.id.backButton)).perform(click());

    }
}