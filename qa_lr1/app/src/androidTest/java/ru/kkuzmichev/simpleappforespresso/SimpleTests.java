package ru.kkuzmichev.simpleappforespresso;

import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static org.hamcrest.Matchers.anyOf;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static ru.kkuzmichev.simpleappforespresso.DisplayMatcher.elementVisibilityPosition;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import androidx.test.rule.GrantPermissionRule;
import android.os.Environment;
import android.Manifest;
import androidx.test.uiautomator.UiDevice;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import org.junit.After;
import org.junit.Before;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.rule.ActivityTestRule;
import io.qameta.allure.android.runners.AllureAndroidJUnit4;
import io.qameta.allure.kotlin.Allure;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.junit.rules.TestWatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AllureAndroidJUnit4.class)
public class SimpleTests {

    @Rule
    public androidx.test.rule.ActivityTestRule<MainActivity> ActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, org.junit.runner.Description description) {
            String className = description.getClassName();
            className = className.substring(className.lastIndexOf('.') + 1);
            String methodName = description.getMethodName();
            takeScreenshot(className + "#" + methodName);
        }
    };

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            );

    private void takeScreenshot(String name) {
        File path = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/screenshots/");
        if (!path.exists()) {
            path.mkdirs();
        }
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        String filename = name + ".png";
        device.takeScreenshot(new File(path, filename));
        try {
            Allure.attachment(filename, new FileInputStream(new File(path, filename)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void positiveCheck() {
        Allure.feature("проверка позитивного теста");
        onView(withId(R.id.text_home))
                .check(matches(withText("This is home fragment")));
    }

    @Test
    public void negativeCheck() {
        Allure.feature("проверка негативного теста");
        onView(withId(R.id.text_home))
                .check(matches(withText("Error")));
    }

    @Test
    public void intentCheck() {
        Allure.feature("проверка исполнения намерения/механизма");
        Intents.init();
        try {
            openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        } catch (Exception e) {
        }
        onView(anyOf(withText(R.string.action_settings), withId(R.id.action_settings))).perform(click());
        Intents.intended(hasData("https://google.com"));
    }

    @Before
    public void registerIdlingResources() { //Подключаемся к “счетчику”
        IdlingRegistry.getInstance().register(EspressoIdlingResources.idlingResource);
    }

    @After // Выполняется после тестов
    public void unregisterIdlingResources() { //Отключаемся от “счетчика”
        IdlingRegistry.getInstance().unregister(EspressoIdlingResources.idlingResource);
    }

    @Test
    public void galleryCheck() {
        Allure.feature("проверка галереи");
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_gallery)).perform(click());
    }

    @Test
    public void checkElementFromList() {
        Allure.feature("проверка элементов из списка");
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_gallery)).perform(click());
        ViewInteraction recyclerView = onView(withId(R.id.recycle_view));
        recyclerView.check(matches(elementVisibilityPosition(0)));
    }
}

class DisplayMatcher {

    public static Matcher<View> elementVisibilityPosition(final int position) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("position of visible element" + position + ": ");
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    return false;
                }
                return viewHolder.itemView.getVisibility() == View.VISIBLE;
            }
        };
    }
}