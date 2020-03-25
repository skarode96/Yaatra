package com.tcd.yaatra.ui.activities;

import android.view.View;
import android.widget.EditText;
import androidx.test.rule.ActivityTestRule;
import com.tcd.yaatra.R;
import com.tcd.yaatra.repository.UserInfoRepository;
import com.tcd.yaatra.repository.models.AsyncData;
import com.tcd.yaatra.services.api.yaatra.models.RegisterResponse;
import com.tcd.yaatra.ui.viewmodels.RegisterActivityViewModel;
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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Checks.checkNotNull;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(MockitoJUnitRunner.class)
public class RegisterTest {

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityTestRule = new ActivityTestRule<>(RegisterActivity.class);

    @Mock
    RegisterActivityViewModel registerActivityViewModel;

    @Mock
    UserInfoRepository userInfoRepository;

    RegisterActivity testRegisterActivity;

    @Captor
    ArgumentCaptor<AsyncData<RegisterResponse>> captor;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        testRegisterActivity = mActivityTestRule.getActivity();

        testRegisterActivity.setRegisterActivityViewModel(registerActivityViewModel);
        testRegisterActivity.setUserInfoRepository(userInfoRepository);
    }

    @Test
    public void test_ErrorShown_IfRegisterIsClickedWithoutEnteringUserName() {

        onView(withId(R.id.userName)).perform(clearText());
        onView(withId(R.id.btnRegister))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.userName)).check(matches(hasErrorText("Username is required", false)));
        onView(withId(R.id.userName)).check(matches(hasErrorText("Please enter username", true)));

    }
    @Test
    public void test_ErrorShown_IfConfirmedPasswordIsBlank() {

        onView(withId(R.id.userName)).perform(clearText());
        onView(withId(R.id.userName)).perform(typeText("DummyUser"));

        onView(withId(R.id.firstName)).perform(clearText());
        onView(withId(R.id.firstName)).perform(typeText("U"));

        onView(withId(R.id.lastName)).perform(clearText());
        onView(withId(R.id.lastName)).perform(typeText("C"));

        onView(withId(R.id.country)).perform(scrollTo()).perform(clearText());
        onView(withId(R.id.country)).perform(scrollTo()).perform(typeText("Ireland"));

        onView(withId(R.id.emailId)).perform(clearText());
        onView(withId(R.id.emailId)).perform(typeText("DummyUser@email.com"));

        onView(withId(R.id.age)).perform(scrollTo()).perform(clearText());
        onView(withId(R.id.age)).perform(scrollTo()).perform(typeText("20"));

        onView(withId(R.id.phoneNum)).perform(scrollTo()).perform(clearText());
        onView(withId(R.id.phoneNum)).perform(scrollTo()).perform(typeText("1234520"));

        onView(withId(R.id.userPassword)).perform(scrollTo()).perform(clearText());
        onView(withId(R.id.userPassword)).perform(scrollTo()).perform(typeText("Password"));

        onView(withId(R.id.btnRegister))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.userCPassword)).perform(scrollTo()).check(matches(hasErrorText("Confirmed Password is required", false)));
        onView(withId(R.id.userCPassword)).perform(scrollTo()).check(matches(hasErrorText("Please enter confirmed password", true)));

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
