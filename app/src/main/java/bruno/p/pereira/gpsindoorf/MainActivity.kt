package bruno.p.pereira.gpsindoorf

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import bruno.p.pereira.gpsindoorf.databinding.ActivityMainBinding
import bruno.p.pereira.gpsindoorf.services.HttpRequest
import com.google.android.material.bottomnavigation.BottomNavigationView


const val TAG = "MYTAG"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_sync,
                R.id.navigation_database,
                R.id.navigation_graph,
                R.id.navigation_info
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val config = AppBarConfiguration(navController.graph)
        findViewById<Toolbar>(R.id.action_bar).setupWithNavController(navController, config)

        syncInformation()
        TODO("resolve error delete and reboot")
    }

    private fun syncInformation() {
        HttpRequest.startActionGETUser(this)
        HttpRequest.startActionGETBeacons(this)
        HttpRequest.startActionGETLocation(this)
        HttpRequest.startActionGETEdge(this)
    }

}


