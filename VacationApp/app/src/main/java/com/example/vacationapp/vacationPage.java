package com.example.vacationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class vacationPage extends AppCompatActivity {

    TextView header;
    Button addEventButton, finalizeAddButton, backButton;
    LinearLayout addEventInfoHolder, allEvents;
    ScrollView eventHolder;
    EditText eventNameField;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        header = this.findViewById(R.id.header);
        addEventButton = this.findViewById(R.id.btn_add_event);
        finalizeAddButton = this.findViewById(R.id.finalize_add_button);
        addEventInfoHolder = this.findViewById(R.id.add_event_info_holder);
        allEvents = this.findViewById(R.id.all_events);
        eventHolder = this.findViewById(R.id.event_holder);
        eventNameField = this.findViewById(R.id.event_name_field);
        backButton = this.findViewById(R.id.btn_back);
        database = Database.getInstance(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String vacationName = getIntent().getStringExtra("vacation");
        Vacation vacation = new Vacation(database.getEventsByVacation(vacationName), vacationName, this);
        header.setText(vacation.getName());
        renderEvents(vacation);
        //Listener when add Vacation color is clicked
        addEventButton.setOnClickListener(v -> {
            if (addEventButton.getText().toString().equals("Add a Category")) {
                addEventInfoHolder.setVisibility(View.VISIBLE);
                addEventButton.setText(R.string.cancel);
            } else {
                addEventInfoHolder.setVisibility(View.GONE);
                addEventButton.setText(R.string.addEvent);
                eventNameField.setText("");
            }
        });

        //Listener when the add Button to finalize is clicked
        finalizeAddButton.setOnClickListener(v -> {
            String enteredName = eventNameField.getText().toString();
            if (enteredName.isEmpty()) {
                Toast.makeText(this, "Please Enter a Category", Toast.LENGTH_SHORT).show();
            } else if (getAllEventNames(vacation).contains(enteredName.toLowerCase().trim())) {
                Toast.makeText(this, "Category with this name already exists", Toast.LENGTH_SHORT).show();
            } else {
                vacation.addEvent(new Event(enteredName, new ArrayList<>()));
                addEventButton.setText(R.string.addEvent);
                eventNameField.setText("");
                addEventInfoHolder.setVisibility(View.GONE);
                database.addData(vacation.getName(), enteredName, "");
                renderEvents(vacation);
            }
        });

        //Listener to go back
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, homepage.class);
            startActivity(intent);
            overridePendingTransition(0,0);
        });
    }

    //Helper method to get the names of all the cards
    private List<String> getAllEventNames(Vacation vacation) {
        List<String> res = new ArrayList<>();
        for (Event event : vacation.getEvents()) {
            res.add(event.getName().toLowerCase());
        }
        return res;
    }


    //Helper Method to create a update the events list
    public void renderEvents(Vacation vacation) {
        eventHolder.removeAllViews();
        allEvents = vacation.getEventsList();
        eventHolder.addView(allEvents);
    }
}