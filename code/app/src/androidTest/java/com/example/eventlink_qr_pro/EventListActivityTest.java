package com.example.eventlink_qr_pro;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.startsWith;

@RunWith(AndroidJUnit4.class)
public class EventListActivityTest {

    @Rule
    public ActivityScenarioRule<EventListActivity> activityRule =
            new ActivityScenarioRule<>(EventListActivity.class);

    @Test
    public void testCreateEvent() {

        onView(withId(R.id.create_event_button)).check(matches(withText("Create Event")));


        onView(withId(R.id.create_event_button)).perform(click());

        onView(withId(R.id.eventName)).perform(typeText("Sample Event"));
        onView(withId(R.id.eventLocation)).perform(typeText("Sample Location"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.eventDescription)).perform(typeText("Sample Description"), ViewActions.closeSoftKeyboard());


        onView(withText("OK")).perform(click());

        onData(startsWith("Sample Event"))
                .inAdapterView(withId(R.id.events_list_view))
                .check(matches(isDisplayed()));
    }
}

