package com.example.vacationapp;

import android.net.Uri;
import java.util.List;

public class Event {
    private String name;
    private final List<Uri> photos;

    public Event(String name, List<Uri> photos) {
        this.name = name;
        this.photos = photos;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPhoto(Uri photo) {
        this.photos.add(photo);
    }

    public List<Uri> getPhotos() {
        return this.photos;
    }
}

