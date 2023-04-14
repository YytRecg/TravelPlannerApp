package com.example.myapplication2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import com.example.myapplication2.ui.activity.LoginActivity;
import com.example.myapplication2.ui.fragment.LoginFragment;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule
            = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.myapplication2", appContext.getPackageName());
    }

    @Test
    public void testLoginPass1() throws InterruptedException {
        onView(withId(R.id.email_id)).perform(typeText("michaelchiou2010@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password_text)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.location)).check(matches(withText("Current Location")));
    }

    @Test
    public void testLoginFail1() throws InterruptedException {
        onView(withId(R.id.email_id)).perform(typeText("michaelchiou2010@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password_text)).perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.location)).check(doesNotExist());
    }

    @Test
    public void testLoginFail2() throws InterruptedException {
        onView(withId(R.id.email_id)).perform(typeText("michaelchiou2010@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password_text)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.new_user_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.location)).check(doesNotExist());
    }

}