package com.example.watchosapplicaion.presentation.utils;


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
 * Constants : Contains common constants for the application.
 * -----------------------------------------------------------------------------------
 * <p>
 * Revision History
 * -----------------------------------------------------------------------------------
 * Modified By          Modified On         Description
 * -----------------------------------------------------------------------------------
 */

public class Constants {

    private Constants() {
        //No Implementation to hide the implicit public one.
    }

    public static final int SPLASH_DISPLAY_LENGTH = 3000;

    public static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 2000;

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    public static final long LOGIN_SESSION = 3600000;

    public static final long LOCATION_INTERVAL = 20000;

    public static final long DELETE_INTERVAL = 120000;
    public static final String USER_SESSION = "UserSession";

    public static final String LOGIN_TIMESTAMP = "LoginTimestamp";

    public static final String AUTHOR = "author";

    public static final String TITLE = "title";

    public static final String DESCRIPTION = "description";

    public static final String IMAGE_URL = "imageUrl";

    public static final String ACTION_BROADCAST_EVENT = "com.yamaha.watchbroadcastreceiver.BROADCAST_EVENT";

    public static final String EXTRA_ACTION = "extra_action";

}
