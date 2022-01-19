package bruno.p.pereira.gpsindoorf.ui.sync

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import bruno.p.pereira.gpsindoorf.MainActivity
import bruno.p.pereira.gpsindoorf.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class NavigationTest {

    @Test
    fun testFragmentNavigation() {
        val activityScenario= ActivityScenario.launch(MainActivity::class.java)
        //nav to another fragment
        onView(withId(R.id.navigation_graph)).perform(click())
        //verify
        onView(withId(R.id.floatingActionButton)).check(matches(isDisplayed()))


    }
}