package com.example.watchosapplicaion.presentation.network;

import com.example.watchosapplicaion.presentation.model.LoginRequest;
import com.example.watchosapplicaion.presentation.model.LoginResponse;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

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
 * LoginAPIService : This is network service for retrofit library.
 * The library will auto-generate code for API integration using annotations.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

public interface LoginAPIService {

    //Login API
    @POST("auth/login")
    Single<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}
