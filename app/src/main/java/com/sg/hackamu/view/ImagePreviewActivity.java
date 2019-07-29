package com.sg.hackamu.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sg.hackamu.R;
import com.sg.hackamu.view.faculties.FacultyMainActivity;

public class ImagePreviewActivity extends AppCompatActivity {
    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        imageView=findViewById(R.id.imageViewImagePreview);
        progressBar=findViewById(R.id.progressBarImagePreview);
        Intent i=getIntent();
        String imageURI=i.getStringExtra("imageURI");
        Glide.with(ImagePreviewActivity.this).load(imageURI).listener(requestListener()).into(imageView);
    }

    public RequestListener<Drawable> requestListener(){
        return new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ImagePreviewActivity.this,e.getMessage().trim(),Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        };
    }
}
