package bruno.p.pereira.gpsindoorf.ui.beacons

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import bruno.p.pereira.gpsindoorf.TAG
import bruno.p.pereira.gpsindoorf.database.SQLiteHelper
import bruno.p.pereira.gpsindoorf.databinding.FragmentBeaconsBinding
import bruno.p.pereira.gpsindoorf.services.HttpRequest
import com.clj.fastble.BleManager
import com.clj.fastble.data.BleScanState

class BeaconsFragment : Fragment() {

    private val db: SQLiteHelper by lazy {
        SQLiteHelper(this.requireContext())
    }

    private val _beaconAdapt: BeaconsAdpter by lazy {
        BeaconsAdpter(db)
    }


    private var _binding: FragmentBeaconsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BleManager.getInstance().scanSate == BleScanState.STATE_SCANNING)
            BleManager.getInstance().cancelScan()


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBeaconsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.rvListDataBase.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = _beaconAdapt
        }

        val sync = binding.floatingSync
        sync.setOnClickListener {
            Toast.makeText(activity, "Syncing Information", Toast.LENGTH_LONG).show()

            HttpRequest.startActionGETBeacons(this.requireContext())
            HttpRequest.startActionGETLocation(this.requireContext())


            sync.animate().apply {
                duration = 2000
                rotationBy(360f)
            }.start()

            root.postDelayed({
                _beaconAdapt.notify_changes()
                Log.v(TAG, "[BEACONSLIST] UI Updated")
            }, 3000)


        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}