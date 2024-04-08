package com.example.eventlink_qr_pro;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ViewProfileActivityTest {

    @Test
    public void deleteButton_clickPerformsAction() {
        // Prepare a dummy Attendee object
        Attendee dummyAttendee = new Attendee("id123", "John Doe", "johndoe@example.com", "555-1234");

        // Prepare a dummy Bitmap for the profile picture
        Bitmap dummyBitmap = BitmapFactory.decodeResource(ApplicationProvider.getApplicationContext().getResources(), R.drawable.ic_alert); // Replace 'R.drawable.ic_launcher' with any drawable resource for testing

        // Create an Intent that includes the Attendee object and Bitmap
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ViewProfileActivity.class);
        intent.putExtra("attendee", dummyAttendee); // Assuming 'Attendee' implements Serializable
        intent.putExtra("imageUrl", "path/to/image"); // This can be adjusted based on how your Activity expects to receive the image URL


        try (ActivityScenario<ViewProfileActivity> scenario = ActivityScenario.launch(intent)) {
            // Perform a click on the delete button
            onView(withId(R.id.deleteButton)).perform(click());


        }
    }

}
