package uk.echosoft.garage.opener;

import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsActivityTest {

    @Rule
    public ActivityTestRule<SettingsActivity> activityRule = new ActivityTestRule<>(SettingsActivity.class);

    @Before
    public void setUp() throws Exception {
        SharedPreferences settings = getInstrumentation().getTargetContext().getSharedPreferences("settings", 0);
        settings.edit().clear().apply();
    }

    @Test
    public void shouldNotSetInvalidUrl() {
        // Given
        onView(withId(R.id.garage_opener_url)).perform(replaceText("google.com"), closeSoftKeyboard());

        // When
        onView(withId(R.id.action_save)).perform(click());

        // Then
        onView(withId(R.id.garage_opener_url)).check(matches(hasErrorText("malformed url, must start with http:// or https://")));
        SharedPreferences settings = getInstrumentation().getTargetContext().getSharedPreferences("settings", 0);
        assertNull(settings.getString("uri", null));
    }

    @Test
    public void shouldSetValidUrl() {
        // Given
        onView(withId(R.id.garage_opener_url)).perform(replaceText("http://google.com"), closeSoftKeyboard());

        // When
        onView(withId(R.id.action_save)).perform(click());

        // Then
        SharedPreferences settings = getInstrumentation().getTargetContext().getSharedPreferences("settings", 0);
        assertEquals(settings.getString("uri", null), "http://google.com");
    }

    @Test
    public void shouldSaveEmptyUrl() {
        // Given
        onView(withId(R.id.garage_opener_url)).perform(replaceText(""), closeSoftKeyboard());

        // When
        onView(withId(R.id.action_save)).perform(click());

        // Then
        SharedPreferences settings = getInstrumentation().getTargetContext().getSharedPreferences("settings", 0);
        assertEquals(settings.getString("uri", null), "");
    }

}
