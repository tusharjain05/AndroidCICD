package com.example.watchosapplication.presentation.ActivityTest;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.watchosapplicaion.R;
import com.example.watchosapplicaion.presentation.view.LoginActivity;
import com.example.watchosapplicaion.presentation.view.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public IntentsTestRule<LoginActivity> intentsTestRule = new IntentsTestRule<>(LoginActivity.class);


    @Before
    public void setUp() {
    }

    @Test
    public void login_withValidCredentials_expectsSuccess() {
        onView(withId(R.id.usernameEditText)).perform(typeText("kminchelle"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText("0lelplR"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        // Replace "NextActivity" with the actual activity that is expected to launch after successful login.
        //intended(hasComponent(NewActivity.class.getName()));
        // For demonstration, let's say we check for a success message displayed as a Toast.
        // This is a placeholder; you'll need to implement a custom matcher to check for Toast messages.

        // This example assumes a TextView with a success message is shown in the LoginActivity upon successful login.
        //intended(hasComponent(MainActivity.class.getName()));    }
}
