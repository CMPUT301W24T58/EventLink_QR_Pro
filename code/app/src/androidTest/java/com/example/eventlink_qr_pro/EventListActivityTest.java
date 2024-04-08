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
/**
 * Espresso tests for {@code EventListActivity}, focusing on the user interaction flow for creating
 * a new event. This class ensures the UI elements are interactive as expected and validates the
 * successful creation and display of an event in the event list.
 */
@RunWith(AndroidJUnit4.class)
public class EventListActivityTest {
    /**
     * Launches {@code EventListActivity} before each test method. This setup provides a consistent
     * starting point for the tests, ensuring that the activity is in its initial state.
     */
    @Rule
    public ActivityScenarioRule<EventListActivity> activityRule =
            new ActivityScenarioRule<>(EventListActivity.class);
    /**
     * Tests the "Create Event" functionality by simulating user input to create a new event and
     * then verifying that the event is correctly added to the event list. The test follows these steps:
     * <ol>
     *     <li>Checks that the "Create Event" button is displayed with the correct text.</li>
     *     <li>Performs a click action on the "Create Event" button to navigate to the event creation form.</li>
     *     <li>Fills in the event name, location, and description fields with sample text, and closes the soft keyboard to ensure visibility of subsequent elements.</li>
     *     <li>Clicks the "OK" button to submit the form.</li>
     *     <li>Verifies that the newly created event, identified by its name starting with "Sample Event," is displayed in the event list.</li>
     * </ol>
     */
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

