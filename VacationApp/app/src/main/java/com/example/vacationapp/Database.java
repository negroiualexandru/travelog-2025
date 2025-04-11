package com.example.vacationapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database extends SQLiteOpenHelper {

    private static Database instance = null;
    SQLiteDatabase database = this.getWritableDatabase();

    public Database(Context context) {
        super(context, "vacations.db", null, 1);
    }

    public static Database getInstance(Context context) {
        if (instance == null) {
            instance = new Database(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void addData(String vacationName, String eventName, String photo) {
        database.execSQL("create Table IF NOT EXISTS data(vacationName TEXT, eventName TEXT, photo TEXT)");
        ContentValues values = new ContentValues();
        values.put("vacationName", vacationName);
        values.put("eventName", eventName);
        values.put("photo", photo);
        database.insertOrThrow("data", null, values);
    }

    public Map<String, Vacation> getData(Context context) {
        database.execSQL("create Table IF NOT EXISTS data(vacationName TEXT, eventName TEXT, photo TEXT)");
        Map<String, Vacation> res = new HashMap<>();
        Cursor allVacations = database.rawQuery("SELECT vacationName, eventName, photo FROM data", null);
        allVacations.moveToFirst();

        while(!allVacations.isAfterLast()) {
            String vacationName = allVacations.getString(0);
            String eventName = allVacations.getString(1);
            Uri photo = Uri.parse(allVacations.getString(2));
            Vacation vacation = res.get(vacationName);
            if (vacation == null) {
                vacation = new Vacation(new ArrayList<>(), vacationName, context);
                res.put(vacationName, vacation);
            }
            Event event = getEventByName(vacation.getEvents(), eventName);
            if (event == null) {
                event = new Event(eventName, new ArrayList<>());
                vacation.addEvent(event);
            }
            event.addPhoto(photo);
            allVacations.moveToNext();
        }
        allVacations.close();
        return res;
    }

    public List<Event> getEventsByVacation(String vacationName) {
        database.execSQL("create Table IF NOT EXISTS data(vacationName TEXT, eventName TEXT, photo TEXT)");
        List<Event> res = new ArrayList<>();
        String[] selection = {vacationName};
        Cursor allEvents = database.rawQuery("SELECT eventName, photo FROM data WHERE vacationName=?", selection);
        allEvents.moveToFirst();
        while(!allEvents.isAfterLast()) {
            String eventName = allEvents.getString(0);
            Uri photo = Uri.parse(allEvents.getString(1));
            Event event = getEventByName(res, eventName);
            if (event == null) {
                event = new Event(eventName, new ArrayList<>());
                res.add(event);
            }
            event.addPhoto(photo);
            allEvents.moveToNext();
        }
        allEvents.close();
        return res;
    }

    public List<Uri> getPhotosByEvent(String eventName, String vacationName) {
        database.execSQL("create Table IF NOT EXISTS data(vacationName TEXT, eventName TEXT, photo TEXT)");
        List<Uri> res = new ArrayList<>();
        String[] selection = {vacationName, eventName};
        Cursor allPhotos = database.rawQuery("SELECT photo FROM data WHERE vacationName=? and eventName=?", selection);
        allPhotos.moveToFirst();
        while(!allPhotos.isAfterLast()) {
            if (!allPhotos.getString(0).isEmpty()) {
                res.add(Uri.parse(allPhotos.getString(0)));
            }
            allPhotos.moveToNext();
        }
        allPhotos.close();
        return res;
    }

    public void deleteVacation(String vacationName) {
        database.execSQL("create Table IF NOT EXISTS data(vacationName TEXT, eventName TEXT, photo TEXT)");
        String[] selection = {vacationName};
        database.execSQL("DELETE FROM data WHERE vacationName = ?", selection);
    }

    public void deleteEvent(String eventName, String vacationName) {
        database.execSQL("create Table IF NOT EXISTS data(vacationName TEXT, eventName TEXT, photo TEXT)");
        String[] selection = {vacationName, eventName};
        database.execSQL("DELETE FROM data WHERE vacationName = ? and eventName=?", selection);
    }

    public void deletePhoto(String vacationName, String eventName, String photo) {
        database.execSQL("create Table IF NOT EXISTS data(vacationName TEXT, eventName TEXT, photo TEXT)");
        String[] selection = {vacationName, eventName, photo};
        database.execSQL("DELETE FROM data WHERE vacationName = ? and eventName=? and photo=?", selection);
    }

    //Helper Method that gets the event by name
    private Event getEventByName(List<Event> events, String name) {
        for (Event event : events) {
            if (name.equals(event.getName())) {
                return event;
            }
        }
        return null;
    }
}
