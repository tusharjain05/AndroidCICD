package com.example.watchosapplicaion.presentation.network;

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
 * APIServices : This is network service for retrofit library.
 * The library will auto-generate code for API integration using annotations.
 * -----------------------------------------------------------------------------------
 *
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

import com.example.watchosapplicaion.presentation.model.NewsAPIResponse;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface APIServices {
    
    // Get news Article
    @GET("v2/everything?q=tesla&from=2024-01-29&sortBy=publishedAt&apiKey=4a1a1317a3064046b297b54cedd7516d")
    Single<NewsAPIResponse> getNewsArticles();
}
