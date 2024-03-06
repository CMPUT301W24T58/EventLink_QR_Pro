package com.example.eventlink_qr_pro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class EventListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to your XML layout file
        setContentView(R.layout.event_list);
        // Here you would initialize your UI components and set up any necessary listeners or adapters

        Button createEventButton = findViewById(R.id.create_event_button);

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateEventDialogFragment dialogFragment = new CreateEventDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "createEvent");
            }
        });

    }
}

