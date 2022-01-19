package bruno.p.pereira.gpsindoorf.ui.sync

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import bruno.p.pereira.gpsindoorf.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SyncFragmentTest{



    @Test
    fun buttonDisplay(){
        var frag= launchFragmentInContainer<SyncFragment>()
        Espresso.onView(ViewMatchers.withId(R.id.btScan))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun buttonDisplayText(){
        var frag= launchFragmentInContainer<SyncFragment>()
        Espresso.onView(ViewMatchers.withId(R.id.btScan))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.scan)))
    }
}