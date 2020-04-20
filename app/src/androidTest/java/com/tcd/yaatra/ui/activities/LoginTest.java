package com.tcd.yaatra.ui.activities;

import android.view.View;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.test.rule.ActivityTestRule;

import com.tcd.yaatra.R;
import com.tcd.yaatra.repository.datasource.UserInfoRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.LoginResponse;
import com.tcd.yaatra.ui.viewmodels.LoginActivityViewModel;
import com.tcd.yaatra.utils.Constants;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Checks.checkNotNull;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class LoginTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    //@Rule
    //InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    LoginActivityViewModel loginActivityViewModel;

    @Mock
    UserInfoRepository userInfoRepository;

    LoginActivity testLoginActivity;

    @Captor
    ArgumentCaptor<AsyncData<LoginResponse>> captor;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        testLoginActivity = mActivityTestRule.getActivity();

        testLoginActivity.setLoginActivityViewModel(loginActivityViewModel);
        testLoginActivity.setUserInfoRepository(userInfoRepository);
    }


    @Test
    public void test_ErrorShown_IfLoginIsClickedWithoutEnteringUserName() {

        onView(withId(R.id.username)).perform(clearText());
        onView(withId(R.id.password)).perform(clearText());
        onView(withId(R.id.login)).perform(click());

        onView(withId(R.id.username)).check(matches(hasErrorText(Constants.USER_NAME_IS_REQUIRED, false)));
        onView(withId(R.id.username)).check(matches(hasErrorText(Constants.PLEASE_ENTER_USER_NAME, true)));
//
//        Mockito.when(loginActivityViewModel.authenticateUser(testUser, testPass)).thenReturn(new LiveData<AsyncData<LoginResponse>>() {
//
//        });
    }

    @Test
    public void test_ErrorShown_IfLoginIsClickedWithoutEnteringPassword() {

        onView(withId(R.id.username)).perform(clearText());
        onView(withId(R.id.username)).perform(typeText("DummyUser"));
        onView(withId(R.id.password)).perform(clearText());
        onView(withId(R.id.login)).perform(click());

        onView(withId(R.id.password)).check(matches(hasErrorText(Constants.PASSWORD_IS_REQUIRED, false)));
        onView(withId(R.id.password)).check(matches(hasErrorText(Constants.PLEASE_ENTER_PASSWORD, true)));
    }


    //TODO: Test is in progress
    //TODO: Capture observer of the api call
    //TODO: set Success or Failure status values in the login response and validate behavior on mock objects
    @Test
    public void test_AuthenticateUser() {

        String testUser = "DummyUser";
        String testPassword = "DummyPwd";

        LiveData<AsyncData<LoginResponse>> loginResponse = new LiveData<AsyncData<LoginResponse>>() {};

        /*AsyncData<LoginResponse> response = AsyncData.getSuccessState(new LoginResponse("", ""));*/

        //loginActivityViewModel.authenticateUser(testUser, testPassword).observeForever(mockObserver);


        /*Observer<AsyncData<LoginResponse>> mockObserver = new Observer<AsyncData<LoginResponse>>() {
            @Override
            public void onChanged(AsyncData<LoginResponse> loginResponseAsyncData) {

            }
        };*/

        Mockito.when(loginActivityViewModel.authenticateUser(testUser, testPassword)).thenReturn(loginResponse);

        //testLoginActivity.setApiObserver(mockObserver);

        onView(withId(R.id.username)).perform(clearText());
        onView(withId(R.id.password)).perform(clearText());
        onView(withId(R.id.username)).perform(typeText(testUser));
        onView(withId(R.id.password)).perform(typeText(testPassword));
        onView(withId(R.id.login)).perform(click());


        verify(loginActivityViewModel, Mockito.times(1)).authenticateUser(testUser, testPassword);

        //verify(mockObserver).onChanged(captor.capture());

        //loginResponse.setValue(AsyncData.getSuccessState(new LoginResponse("Success", "")));
    }

    private void captureLiveData(AsyncData<LoginResponse> response){

    }

    private static Matcher<? super View> hasErrorText(String expectedError, boolean isHint) {
        return new ErrorTextMatcher(expectedError, isHint);
    }

    private static class ErrorTextMatcher extends TypeSafeMatcher<View> {
        private final String expectedError;
        private final boolean isHint;

        private ErrorTextMatcher(String expectedError, boolean isHint) {
            this.expectedError = checkNotNull(expectedError);
            this.isHint = isHint;
        }

        @Override
        public boolean matchesSafely(View view) {
            if (!(view instanceof EditText)) {
                return false;
            }
            EditText editText = (EditText) view;

            if(!isHint){
                return expectedError.equals(editText.getError());
            }
            else {
                return expectedError.equals(editText.getHint());
            }
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(expectedError);
        }
    }

}
