package com.example.watchosapplicaion.presentation.network;

import com.example.watchosapplicaion.presentation.model.LoginRequest;
import com.example.watchosapplicaion.presentation.model.LoginResponse;
import com.example.watchosapplicaion.presentation.model.NewsAPIResponse;

import javax.inject.Inject;

import io.reactivex.Single;

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
 * AppRepo : AppRepo class to manage data retrieval and storage.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

public class AppRepo {

    private final APIServices mApiServices;
    private final LoginAPIService mLoginAPIService;

    @Inject
    public AppRepo(APIServices apiServices, LoginAPIService loginAPIService) {
        this.mApiServices = apiServices;
        this.mLoginAPIService = loginAPIService;
    }

    /*
     *This function is responsible to return the News Response.
     * */
    public Single<NewsAPIResponse> getNewsArticles() {
        return mApiServices.getNewsArticles();
    }


    /*
     *This function is responsible to Authenticate the user.
     * */
    public Single<LoginResponse> loginUser(LoginRequest loginRequest) {
        return mLoginAPIService.loginUser(loginRequest);
    }
}
