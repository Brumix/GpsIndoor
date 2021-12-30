package bruno.p.pereira.gpsindoorf.ui.beacons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.database.SQLiteHelper
import bruno.p.pereira.gpsindoorf.databinding.FragmentBeaconsBinding
import bruno.p.pereira.gpsindoorf.databinding.FragmentInfoBinding
import com.clj.fastble.BleManager
import com.clj.fastble.data.BleScanState

class BeaconsFragment : Fragment() {

    private val bd: SQLiteHelper by lazy {
        SQLiteHelper(this.requireContext())
    }

    private val _beaconAdapt: BeaconsAdpter by lazy {
        BeaconsAdpter(bd)

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

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}