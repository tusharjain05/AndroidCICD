package com.example.watchosapplicaion.presentation.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.watchosapplicaion.R;
import com.example.watchosapplicaion.databinding.ActivityArticleDetailBinding;
import com.example.watchosapplicaion.presentation.utils.Constants;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Project Name : StandAloneApplication
 *
 * @author VE00YM465
 * @company YMSLI
 * @date 14-02-2024
 * Copyright (c) 2021, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 * <p>
 * Description
 * -----------------------------------------------------------------------------------
 * ArticleDetailActivity : This is the ArticleDetail Activity in the Watch android application.
 * -----------------------------------------------------------------------------------
 * <p>
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

@AndroidEntryPoint
public class ArticleDetailActivity extends AppCompatActivity {
    private ActivityArticleDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticleDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeUI();

        binding.scrollView.setFocusable(true);
        binding.scrollView.setFocusableInTouchMode(true);
        binding.scrollView.requestFocus();
    }

    /**
     * Initializes the UI by extracting data from the Intent and setting text views.
     * Also initiates loading the article image if an URL is provided.
     */
    private void initializeUI() {
        String author = getIntent().getStringExtra(Constants.AUTHOR);
        String title = getIntent().getStringExtra(Constants.TITLE);
        String description = getIntent().getStringExtra(Constants.DESCRIPTION);
        String imageUrl = getIntent().getStringExtra(Constants.IMAGE_URL);

        displayArticleDetails(author, title, description);
        loadArticleImage(imageUrl);
    }

    /**
     * Displays the article details such as author, title, and description.
     */
    private void displayArticleDetails(String author, String title, String description) {
        binding.tvAuthor.setText(getString(R.string.prefix_author, author != null ? author : ""));
        binding.tvTitle.setText(getString(R.string.prefix_title, title != null ? title : ""));
        binding.tvDescription.setText(getString(R.string.prefix_description, description != null ? description : ""));
    }

    /**
     * Loads the article image using Glide with a given imageUrl.
     */
    private void loadArticleImage(String imageUrl) {
        if (imageUrl != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imageUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            binding.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(binding.ivArticleImage);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * This function is used for scroll view using rotatory input
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_SCROLL &&
                event.isFromSource(InputDevice.SOURCE_ROTARY_ENCODER)) {
            Log.d("RotaryInput", "Rotary event detected in activity.");
            float delta = -event.getAxisValue(MotionEvent.AXIS_SCROLL) * 10;
            binding.scrollView.scrollBy(0, Math.round(delta));
            return true;
        }
        return super.onGenericMotionEvent(event);
    }
}
