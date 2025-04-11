package com.example.vacationapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vacation {
    private final Context context;
    private final List<Event> events;
    private String name;
    Database database;

    public Vacation (List<Event> events, String name, Context context) {
        this.events = new ArrayList<>();
        if (events != null) {
            this.events.addAll(events);
        }
        if (name == null) {
            throw new IllegalArgumentException("New Vacation Needs a Name");
        }
        this.name = name;
        this.context = context;
        database = Database.getInstance(context);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addEvent(Event e) {
        this.events.add(e);
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(this.events);
    }

    public LinearLayout getEventsList() {
        LinearLayout layout = new LinearLayout(this.context);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(createFavCard(this.events.get(0)));
        int size = this.events.size();
        for(int i = 1; i < size; i++) {
            layout.addView(createCardWithDelete(this.events.get(i)));
        }

        return layout;
    }

    private CardView createFavCard(Event event) {
        CardView cardView = new CardView(this.context);
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

        // Inner container with border
        LinearLayout contentLayout = new LinearLayout(this.context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(32, 32, 32, 32);
        contentLayout.setBackgroundResource(R.drawable.bordered_background);

        // Title text
        TextView textView = new TextView(context);
        textView.setText(event.getName());
        textView.setTextSize(20);
        textView.setTextColor(Color.parseColor("#1E293B"));
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        contentLayout.addView(textView);
        cardView.addView(contentLayout);

        // Click navigates to eventPage
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(this.context, eventPage.class);
            intent.putExtra("event", event.getName());
            intent.putExtra("vacation", this.getName());
            this.context.startActivity(intent);
            ((Activity) this.context).overridePendingTransition(0, 0);
        });

        return cardView;
    }


    private CardView createCardWithDelete(Event event) {
        CardView cardView = new CardView(this.context);
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

        // Inner container with border
        LinearLayout contentLayout = new LinearLayout(this.context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(32, 32, 32, 32);
        contentLayout.setBackgroundResource(R.drawable.bordered_background);

        LinearLayout rowLayout = new LinearLayout(context);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView textView = new TextView(context);
        textView.setText(event.getName());
        textView.setTextSize(20);
        textView.setTextColor(Color.parseColor("#1E293B"));
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        ));

        LinearLayout confirmDeleteLayout = new LinearLayout(context);
        confirmDeleteLayout.setOrientation(LinearLayout.HORIZONTAL);
        confirmDeleteLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        confirmDeleteLayout.setPadding(0, 16, 0, 0);
        confirmDeleteLayout.setVisibility(View.GONE);

        Button deleteButton = new Button(context);
        deleteButton.setText(R.string.delete);
        deleteButton.setTextColor(Color.WHITE);
        deleteButton.setPadding(0, 0, 0, 0);
        GradientDrawable roundedBackground = new GradientDrawable();
        roundedBackground.setColor(Color.parseColor("#EF4444"));
        roundedBackground.setCornerRadius(20);
        deleteButton.setBackground(roundedBackground);
        deleteButton.setOnClickListener(v -> {
           confirmDeleteLayout.setVisibility(View.VISIBLE);
        });

        Button confirmButton = new Button(context);
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
            database.deleteEvent(event.getName(), this.getName());
            this.events.remove(event);
            ((vacationPage) context).renderEvents(this);
        });

        Button cancelButton = new Button(context);
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
            Intent intent = new Intent(this.context, eventPage.class);
            intent.putExtra("event", event.getName());
            intent.putExtra("vacation", this.getName());
            this.context.startActivity(intent);
            ((Activity) this.context).overridePendingTransition(0, 0);
        });
        return cardView;
    }

}
