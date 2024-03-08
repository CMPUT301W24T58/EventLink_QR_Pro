package com.example.eventlink_qr_pro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private boolean attendeeCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button attendeeButton = findViewById(R.id.attendee_button); // Changed from organizerButton to attendeeButton

        // Set an OnClickListener for the Attendee button
        attendeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create an Intent to start the appropriate activity for attendees
                Intent intent = new Intent(MainActivity.this, AttendeeActivity.class); // Assuming AttendeeActivity is the appropriate activity for attendees
                if (!attendeeCreated){
                    String attendeeId = UUID.randomUUID().toString();
                    Attendee attendee = new Attendee(attendeeId, "","","");
                    intent.putExtra("attendee", attendee);


                }
                attendeeCreated = true;
                startActivity(intent);
            }
        });



        Button organizerButton = findViewById(R.id.organizer_button);

        // Set an OnClickListener for the Organizer button
        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start RegisterQRActivity

                Intent intent = new Intent(MainActivity.this, EventListActivity.class);

                startActivity(intent);
            }
        });

        getFMCToken();

    }
    void getFMCToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task. getResult();
                Log.i( "My token", token);
            }
        });

    }

}