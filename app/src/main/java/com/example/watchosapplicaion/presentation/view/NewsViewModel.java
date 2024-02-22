package com.example.watchosapplicaion.presentation.view;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.watchosapplicaion.presentation.model.NewsAPIResponse;
import com.example.watchosapplicaion.presentation.model.NewsArticleResponseBody;
import com.example.watchosapplicaion.presentation.network.AppRepo;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

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
 * NewsViewModel : This is the News ViewModel in the Watch android application.
 * -----------------------------------------------------------------------------------
 * <p>
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

@HiltViewModel
public class NewsViewModel extends AndroidViewModel {
    private final Context context;
    private final MutableLiveData<List<NewsArticleResponseBody>> articles = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private final MutableLiveData<String> responseError = new MutableLiveData<>();
    private final AppRepo mAppRepo;

    @Inject
    public NewsViewModel(@NonNull Application application, AppRepo appRepo) {
        super(application);
        this.mAppRepo = appRepo;
        this.context = application.getApplicationContext();
    }

    /**
     * Clears the disposables to prevent memory leaks, called when the ViewModel is no longer used and will be destroyed.
     */
    @Override
    protected void onCleared() {
        mCompositeDisposable.clear(); // Clear all ongoing operations
        super.onCleared();
    }

    /**
     * Provides the LiveData object containing the list of news articles.
     */
    public LiveData<List<NewsArticleResponseBody>> getArticles() {
        return articles;
    }

    /**
     * Provides the LiveData object indicating the loading state of the news data fetching operation.
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Provides the LiveData object containing any error messages resulting from the news data fetching operation.
     */
    public LiveData<String> getResponseError() {
        return responseError;
    }

    /**
     * Initiates the fetching of news articles from the repository.
     */
    public void fetchArticles() {
        isLoading.setValue(true);
        Disposable disposable = mAppRepo.getNewsArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<NewsAPIResponse>() {
                    @Override
                    public void onSuccess(NewsAPIResponse newsAPIResponse) {
                        isLoading.setValue(false);
                        articles.setValue(newsAPIResponse.getArticles());
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading.setValue(false);
                        responseError.setValue(e.getMessage());
                    }
                });

        mCompositeDisposable.add(disposable);
    }
}
