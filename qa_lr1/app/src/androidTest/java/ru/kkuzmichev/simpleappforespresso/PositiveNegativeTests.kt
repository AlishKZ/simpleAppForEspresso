package ru.kkuzmichev.simpleappforespresso;

import org.junit.Test
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*

class SimpleTests {
    @Test
    fun positive() {
        onView(withId(R.id.text_home))
            .check(matches(withText("This is home fragment")))
    }

    @Test
    fun negative() {
        onView(withId(R.id.text_home))
            .check(matches(withText("Error")))
    }
}