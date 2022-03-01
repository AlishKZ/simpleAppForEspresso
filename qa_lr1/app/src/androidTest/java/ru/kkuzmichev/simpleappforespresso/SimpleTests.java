package ru.kkuzmichev.simpleappforespresso;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.junit.Rule;
import org.junit.Test;


public class SimpleTests {

    @Rule
    public ActivityScenarioRule<MainActivity> ActivityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void positiveCheck() {
        onView(withId(R.id.text_home))
                .check(matches(withText("This is home fragment")));
    }

    @Test
    public void negativeCheck() {
        onView(withId(R.id.text_home))
                .check(matches(withText("Error")));
    }
}