package com.tcd.yaatra.ui.activities;

import android.widget.Button;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.tcd.yaatra.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static java.util.EnumSet.allOf;

@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityTestRule = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void registerActivityUserNameCannotBeBlank() {

        onView(withId(R.id.btnRegister)).perform(click());

        /*ViewInteraction registerButton = onView(with(""));
                allOf(withId(R.id.btnRegister),withParent(withId(R.layout.activity_register)),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.tiet_note_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.til_title),
                                        0),
                                0),
                        isDisplayed()));
        editText.check(matches(isDisplayed()));*/

    }

}
