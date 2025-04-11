package com.example.vacationapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class homepage extends AppCompatActivity {

    List<Vacation> vacations;
    Button addVacationButton, finalizeAddButton;
    LinearLayout addVacationInfoHolder, allVacations;
    ScrollView vacationHolder;
    EditText vacationNameField;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.homepage_style);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addVacationButton = this.findViewById(R.id.btn_add_vacation);
        addVacationInfoHolder = this.findViewById(R.id.add_vacation_info_holder);
        vacationHolder = this.findViewById(R.id.vacation_holder);
        vacationNameField = this.findViewById(R.id.vacation_name_field);
        finalizeAddButton = this.findViewById(R.id.finalize_add_button);
        allVacations = this.findViewById(R.id.all_vacations);
        database = Database.getInstance(this);
    }

    @SuppressLint("UnsafeIntentLaunch")
    @Override
    protected void onResume() {
        super.onResume();
        Map<String, Vacation> data = database.getData(this);
        vacations = new ArrayList<>(data.values());
        renderVacations();

        //Listener when add Vacation color is clicked
        addVacationButton.setOnClickListener(v -> {
            if (addVacationButton.getText().toString().equals("Add a Vacation")) {
                addVacationInfoHolder.setVisibility(View.VISIBLE);
                addVacationButton.setText(R.string.cancel);
            } else {
                addVacationInfoHolder.setVisibility(View.GONE);
                addVacationButton.setText(R.string.addVacation);
                vacationNameField.setText("");
            }
        });

        //Listener when the add Button to finalize is clicked
        finalizeAddButton.setOnClickListener(v -> {
            String enteredName = vacationNameField.getText().toString();
            if (enteredName.isEmpty()) {
                Toast.makeText(this, "Please Enter a Name", Toast.LENGTH_SHORT).show();
            } else if (getAllVacationNames().contains(enteredName.toLowerCase().trim())) {
                Toast.makeText(this, "Vacation with this name already exists", Toast.LENGTH_SHORT).show();
            } else {
                vacations.add(new Vacation(new ArrayList<>(), enteredName, this));
                addVacationButton.setText(R.string.addVacation);
                vacationNameField.setText("");
                addVacationInfoHolder.setVisibility(View.GONE);
                database.addData(enteredName, "Favorites", "");
                renderVacations();
            }
        });
    }


    private CardView createCard(Vacation vacation) {
        CardView cardView = new CardView(this);
        cardView.setClickable(true);
        cardView.setCardElevation(4);
        cardView.setRadius(16);
        cardView.setUseCompatPadding(true);
        cardView.setPreventCornerOverlap(true);
        cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(16, 16, 16, 16);
        cardView.setLayoutParams(cardParams);

        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(32, 32, 32, 32);
        contentLayout.setBackgroundResource(R.drawable.bordered_background);

        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView textView = new TextView(this);
        textView.setText(vacation.getName());
        textView.setTextSize(20);
        textView.setTextColor(Color.parseColor("#1E293B"));
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        ));

        LinearLayout confirmDeleteLayout = new LinearLayout(this);
        confirmDeleteLayout.setOrientation(LinearLayout.HORIZONTAL);
        confirmDeleteLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        confirmDeleteLayout.setPadding(0, 16, 0, 0);
        confirmDeleteLayout.setVisibility(View.GONE);

        Button deleteButton = new Button(this);
        deleteButton.setText(R.string.delete);
        deleteButton.setTextColor(Color.WHITE);
        GradientDrawable roundedBackground = new GradientDrawable();
        roundedBackground.setColor(Color.parseColor("#EF4444"));
        roundedBackground.setCornerRadius(20);
        deleteButton.setBackground(roundedBackground);
        deleteButton.setOnClickListener(v -> {
            confirmDeleteLayout.setVisibility(View.VISIBLE);
        });

        Button confirmButton = new Button(this);
        confirmButton.setText(R.string.confirm);
        confirmButton.setTextColor(Color.WHITE);
        GradientDrawable confirmBg = new GradientDrawable();
        confirmBg.setColor(Color.parseColor("#EF4444"));
        confirmBg.setCornerRadius(20);
        confirmButton.setBackground(confirmBg);
        LinearLayout.LayoutParams confirmParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        confirmParams.setMarginEnd(8);
        confirmButton.setLayoutParams(confirmParams);

        confirmButton.setOnClickListener(v -> {
            database.deleteVacation(vacation.getName());
            vacations.remove(vacation);
            renderVacations();
        });

        Button cancelButton = new Button(this);
        cancelButton.setText(R.string.cancel);
        cancelButton.setTextColor(Color.WHITE);
        GradientDrawable cancelBg = new GradientDrawable();
        cancelBg.setColor(Color.parseColor("#64748B"));
        cancelBg.setCornerRadius(20);
        cancelButton.setBackground(cancelBg);
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        cancelParams.setMarginStart(8);
        cancelButton.setLayoutParams(cancelParams);

        cancelButton.setOnClickListener(v -> {
            confirmDeleteLayout.setVisibility(View.GONE);
        });

        confirmDeleteLayout.addView(confirmButton);
        confirmDeleteLayout.addView(cancelButton);
        rowLayout.addView(textView);
        rowLayout.addView(deleteButton);
        contentLayout.addView(rowLayout);
        contentLayout.addView(confirmDeleteLayout);
        cardView.addView(contentLayout);

        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(this, vacationPage.class);
            intent.putExtra("vacation", vacation.getName());
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        return cardView;
    }



    //Helper method to get the names of all the cards
    private List<String> getAllVacationNames() {
        List<String> res = new ArrayList<>();
        for (Vacation vacation : vacations) {
            res.add(vacation.getName().toLowerCase());
        }
        return res;
    }

    //Helper Method to create a update the vacations list
    private void renderVacations() {
        vacationHolder.removeAllViews();
        allVacations.removeAllViews();
        for (Vacation vacation : vacations) {
            allVacations.addView(createCard(vacation));
        }
        vacationHolder.addView(allVacations);
    }
}