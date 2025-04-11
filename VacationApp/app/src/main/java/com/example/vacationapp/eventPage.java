package com.example.vacationapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class eventPage extends AppCompatActivity {

    String vacationName, eventName;
    Event event;
    TextView header;
    Button back, upload;
    ScrollView photoHolder;
    LinearLayout photos, namingPhotoHolder;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    Database database;
    int page;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        header = this.findViewById(R.id.header);
        back = this.findViewById(R.id.btn_back);
        upload = this.findViewById(R.id.btn_upload);
        photoHolder = this.findViewById(R.id.photo_holder);
        photos = this.findViewById(R.id.all_photos);
        database = Database.getInstance(this);
        page = 0;

        vacationName = getIntent().getStringExtra("vacation");
        eventName = getIntent().getStringExtra("event");
        event = new Event(eventName, database.getPhotosByEvent(eventName, vacationName));
        List<List<Uri>> photos = splitList(event.getPhotos());
        if (!photos.isEmpty()) {
            handleSelectedImages(photos, photos.size() - 1);
        }
        configureImagePicker();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        header.setText(event.getName());

        back.setOnClickListener(v -> {
            Intent intent = new Intent(this, vacationPage.class);
            intent.putExtra("vacation", vacationName);
            startActivity(intent);
            overridePendingTransition(0,0);
        });

        upload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            intent.setDataAndType(uri, "image/*");
            imagePickerLauncher.launch(intent);
        });
    }

    private void handleSelectedImages(List<List<Uri>> selectedImages, int totalPages) {
        photos.removeAllViews();
        for (Uri image : selectedImages.get(page)) {
            LinearLayout photoHolder = new LinearLayout(this);
            photoHolder.setOrientation(LinearLayout.VERTICAL);
            ImageView view = new ImageView(this);
            view.setClickable(true);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 900);
            params.setMargins(0, 16, 0, 16);
            view.setLayoutParams(params);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(this).load(image).transform(new CenterInside()).into(view);
            Button deleteButton = new Button(this);
            deleteButton.setText(R.string.delete);
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150);
            deleteButton.setTextColor(getResources().getColor(R.color.white));
            deleteButton.setLayoutParams(deleteParams);
            GradientDrawable roundedBackground = new GradientDrawable();
            roundedBackground.setColor(Color.parseColor("#EF4444"));
            roundedBackground.setCornerRadius(20);
            deleteButton.setBackground(roundedBackground);
            deleteButton.setVisibility(View.GONE);

            view.setOnClickListener(v -> {
                if (deleteButton.getVisibility() == View.GONE) {
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    view.setLayoutParams(layoutParams);
                    view.setAdjustViewBounds(true);
                    view.setMaxHeight(1200);
                    view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    deleteButton.setVisibility(View.VISIBLE);
                } else {
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.height = 900;
                    view.setLayoutParams(layoutParams);
                    view.setAdjustViewBounds(false);
                    view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    view.setBackgroundColor(Color.TRANSPARENT);
                    deleteButton.setVisibility(View.GONE);
                }
            });

            deleteButton.setOnClickListener(v -> {
                database.deletePhoto(vacationName, eventName, image.toString());
                event.getPhotos().remove(image);
                handleSelectedImages(splitList(event.getPhotos()), totalPages);
            });

            photoHolder.addView(view);
            photoHolder.addView(deleteButton);
            photos.addView(photoHolder);
        }

        LinearLayout pageButtons = new LinearLayout(this);
        pageButtons.setOrientation(LinearLayout.HORIZONTAL);
        pageButtons.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        pageButtons.setPadding(0, 16, 0, 0);

        Button next = new Button(this);
        next.setText(R.string.next);
        next.setTextColor(Color.WHITE);
        GradientDrawable nextBg = new GradientDrawable();
        nextBg.setColor(Color.parseColor("#2563EB"));
        nextBg.setCornerRadii(new float[] {
                0f, 0f,
                20f, 20f,
                20f, 20f,
                0f, 0f
        });
        next.setBackground(nextBg);
        LinearLayout.LayoutParams confirmParams = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        confirmParams.setMarginEnd(8);
        next.setLayoutParams(confirmParams);

        next.setOnClickListener(v -> {
            this.page++;
            handleSelectedImages(selectedImages, totalPages);
            photoHolder.smoothScrollTo(0,0);
        });

        Button prev = new Button(this);
        prev.setText(R.string.prev);
        prev.setTextColor(Color.WHITE);
        GradientDrawable prevBg = new GradientDrawable();
        prevBg.setColor(Color.parseColor("#2563EB"));
        prevBg.setCornerRadii(new float[] {
                20f, 20f,
                0f, 0f,
                0f, 0f,
                20f, 20f
        });
        prev.setBackground(prevBg);
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        cancelParams.setMarginStart(8);
        prev.setLayoutParams(cancelParams);

        prev.setOnClickListener(v -> {
            this.page--;
            handleSelectedImages(selectedImages, totalPages);
            photoHolder.smoothScrollTo(0,0);
        });

        if (this.page == 0) {
            prev.setEnabled(false);
            prev.setAlpha(0.5f);
        }
        if (this.page == totalPages) {
            next.setEnabled(false);
            next.setAlpha(0.5f);
        }

        pageButtons.addView(prev);
        pageButtons.addView(next);
        photos.addView(pageButtons);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean containsUri(Uri image) {
        for(Uri photo : event.getPhotos()) {
            if (image.equals(photo)) {
                return true;
            }
        }
        return false;
    }

    private void configureImagePicker() {
        //Configure the image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            //if multiple images are selected
                            if (data.getClipData() != null) {
                                int count = data.getClipData().getItemCount();
                                for (int i = 0; i < count; i++) {
                                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                    if (!containsUri(imageUri)) {
                                        event.addPhoto(imageUri);
                                        database.addData(vacationName, eventName, imageUri.toString());
                                    }
                                }
                                //if the only one was selected
                            } else if (data.getData() != null) {
                                Uri imageUri = data.getData();
                                if (!containsUri(imageUri)) {
                                    event.addPhoto(imageUri);
                                    database.addData(vacationName, eventName, imageUri.toString());
                                }
                            }
                            List<List<Uri>> photos = splitList(event.getPhotos());
                            this.page = 0;
                            handleSelectedImages(photos, photos.size() - 1);
                        }
                    }
                }
        );
    }

    private List<List<Uri>> splitList(List<Uri> photos) {
        if (photos.isEmpty()) {
            return new ArrayList<>();
        }
        List<List<Uri>> res = new ArrayList<>();
        int size = photos.size();
        for (int i = 0; i < size; i += 10) {
            int end = Math.min(size, i + 10);
            res.add(new ArrayList<>(photos.subList(i, end)));
        }
        return res;
    }
}