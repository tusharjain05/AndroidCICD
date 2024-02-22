package com.example.watchosapplicaion.presentation.view;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.watchosapplicaion.R;
import com.example.watchosapplicaion.presentation.model.LoginRequest;
import com.example.watchosapplicaion.presentation.model.LoginResponse;
import com.example.watchosapplicaion.presentation.network.AppRepo;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

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
 * LoginViewModel : This is the Login ViewModel in the Watch android application.
 * -----------------------------------------------------------------------------------
 * <p>
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

@HiltViewModel
public class LoginViewModel extends AndroidViewModel {
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccessful = new MutableLiveData<>();
    private final MutableLiveData<String> errorResponse = new MutableLiveData<>();
    private final AppRepo mAppRepo;

    @Inject
    public LoginViewModel(@NonNull Application application, AppRepo appRepo) {
        super(application);
        this.mAppRepo = appRepo;
    }

    /**
     * Clears all disposables to prevent memory leaks when the ViewModel is no longer used.
     */
    @Override
    protected void onCleared() {
        mCompositeDisposable.clear();
        super.onCleared();
    }

    /**
     * Returns a LiveData object representing the loading state of login operations.
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Returns a LiveData object indicating the success status of the login operation.
     */
    public LiveData<Boolean> getLoginSuccessful() {
        return loginSuccessful;
    }

    /**
     * Returns a LiveData object containing the error response of the login operation.
     */
    public LiveData<String> getErrorResponse() {
        return errorResponse;
    }

    /**
     * Initiates the login process for a user with the provided username and password.
     */
    public void loginUser(String username, String password) {
        isLoading.setValue(true);
        Disposable disposable = mAppRepo.loginUser(new LoginRequest(username, password))
                .subscribeOn(Schedulers.io()) // Perform API call on IO (background) thread.
                .observeOn(AndroidSchedulers.mainThread()) // Observe API call result on the main thread.
                .subscribeWith(new DisposableSingleObserver<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse loginResponse) {
                        isLoading.setValue(false);
                        loginSuccessful.setValue(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading.setValue(false);
                        if (e instanceof HttpException) {
                            HttpException httpException = (HttpException) e;
                            if (httpException.code() == 400) {
                                // Here, you can parse the error body if needed to extract a specific message
                                errorResponse.setValue("Invalid Credentials Please Try Again");
                            } else {
                                errorResponse.setValue("An Error Occurred");
                            }
                        } else {
                            errorResponse.setValue(e.getMessage());
                        }
                    }
                });

        mCompositeDisposable.add(disposable);
    }
}
