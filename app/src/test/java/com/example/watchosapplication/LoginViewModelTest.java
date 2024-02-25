package com.example.watchosapplication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Looper;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.example.watchosapplicaion.presentation.model.LoginRequest;
import com.example.watchosapplicaion.presentation.model.LoginResponse;
import com.example.watchosapplicaion.presentation.network.AppRepo;
import com.example.watchosapplicaion.presentation.view.LoginViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;

import io.reactivex.Single;
import io.reactivex.schedulers.TestScheduler;

@RunWith(RobolectricTestRunner.class)
public class LoginViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AppRepo appRepo;

    @Mock
    private Observer<Boolean> loginSuccessObserver;

    private LoginViewModel viewModel;
    private TestScheduler testScheduler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testScheduler = new TestScheduler();
        viewModel = new LoginViewModel(ApplicationProvider.getApplicationContext(), appRepo,testScheduler,testScheduler);

        // Observe the LiveData for login success
        viewModel.getLoginSuccessful().observeForever(loginSuccessObserver);
    }

    @Test
    public void loginUser_withValidCredentials_expectsSuccess() {
        // Given
        LoginResponse mockResponse = new LoginResponse(); // Assume this represents a successful login response
        when(appRepo.loginUser(any(LoginRequest.class))).thenReturn(Single.just(mockResponse));

        // When
        viewModel.loginUser("kminchelle", "0lelplR");
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        testScheduler.triggerActions(); // Trigger the RxJava chain

        // Then
        verify(loginSuccessObserver).onChanged(true); // Verify login success is observed
    }
}

