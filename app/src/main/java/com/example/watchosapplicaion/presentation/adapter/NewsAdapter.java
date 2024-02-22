package com.example.watchosapplicaion.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.watchosapplicaion.databinding.NewsItemBinding;
import com.example.watchosapplicaion.presentation.model.NewsArticleResponseBody;
import com.example.watchosapplicaion.presentation.utils.Constants;
import com.example.watchosapplicaion.presentation.view.ArticleDetailActivity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Project Name : StandAloneApplication
 *
 * @author VE00YM465
 * @company YMSLI
 * @date 14-02-2024
 * Copyright (c) 2021, Yamaha Motor Solutions (INDIA) Pvt Ltd.
 *
 * Description
 * -----------------------------------------------------------------------------------
 * NewsAdapter : This is the NewsAdapter for viewing news data list Activity.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<NewsArticleResponseBody> articles;

    private LayoutInflater inflater;

    public NewsAdapter(Context context, List<NewsArticleResponseBody> articles) {
        this.inflater = LayoutInflater.from(context);
        //this.context = context;
        this.articles = articles;
    }

    /**
     *  This method is responsible for creating a new ViewHolder and inflating the layout for each item.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NewsItemBinding newsItemBinding = NewsItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(newsItemBinding);
    }

    /**
     * This method is responsible for binding the data to the ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsArticleResponseBody article = articles.get(position);
        holder.newsItemBinding.tvAuthor.setText(article.getAuthor());
        holder.newsItemBinding.tvTitle.setText(article.getTitle());

        holder.newsItemBinding.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ArticleDetailActivity.class);
            intent.putExtra(Constants.AUTHOR, article.getAuthor());
            intent.putExtra(Constants.TITLE, article.getTitle());
            intent.putExtra(Constants.DESCRIPTION, article.getDescription());
            intent.putExtra(Constants.IMAGE_URL, article.getUrlToImage());
            view.getContext().startActivity(intent);
        });

    }

    /**
     * This method returns the number of items in the data set.
     */
    @Override
    public int getItemCount() {
        return articles != null ? articles.size() : 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final NewsItemBinding newsItemBinding;
        public ViewHolder(NewsItemBinding newsItemBinding) {
            super(newsItemBinding.getRoot());
            this.newsItemBinding = newsItemBinding;
        }
    }

    public void setArticles(List<NewsArticleResponseBody> newArticles) {
        List<NewsArticleResponseBody> filteredArticles = newArticles.stream()
                .filter(article -> article.getUrlToImage() != null && !article.getUrlToImage().isEmpty())
                .collect(Collectors.toList());
        this.articles = filteredArticles;
        notifyDataSetChanged();
    }
}
