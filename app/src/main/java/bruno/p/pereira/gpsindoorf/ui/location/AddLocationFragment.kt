package bruno.p.pereira.gpsindoorf.ui.location

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bruno.p.pereira.gpsindoorf.TAG
import bruno.p.pereira.gpsindoorf.database.SQLiteHelper
import bruno.p.pereira.gpsindoorf.databinding.FragmentAddLocationBinding
import bruno.p.pereira.gpsindoorf.models.Beacon


class AddLocationFragment : Fragment() {


    private var _binding: FragmentAddLocationBinding? = null
    private val binding get() = _binding!!
    private var beaconMac: String = ""
    private lateinit var beaconLoc : Beacon
    private val db: SQLiteHelper by lazy {
        SQLiteHelper(this.requireContext())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            this.beaconMac = it.getString("mac")!!
        }

        beaconLoc = this.db.getFirstBeaconbyMac(this.beaconMac)!!
        Log.v(TAG,beaconLoc.toString())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding


        return root
    }

}