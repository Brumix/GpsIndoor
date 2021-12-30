package bruno.p.pereira.gpsindoorf.ui.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.databinding.LayoutDetailItemBinding
import bruno.p.pereira.gpsindoorf.enums.BundleEnum
import bruno.p.pereira.gpsindoorf.models.Beacon
import bruno.p.pereira.gpsindoorf.ui.beacons.LIST_DATABASE
import com.clj.fastble.BleManager
import com.clj.fastble.data.BleScanState


/**
 * A simple [Fragment] subclass.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailFragment : Fragment() {

    private lateinit var _data: Beacon
    private var _origin: String? = null
    private var _binding: LayoutDetailItemBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BleManager.getInstance().scanSate == BleScanState.STATE_SCANNING)
            BleManager.getInstance().cancelScan()

        arguments?.let {
            val id = it.getInt(BundleEnum.BEACON_ID.name)
            val name = it.getString(BundleEnum.BEACON_NAME.name)
            val mac = it.getString(BundleEnum.BEACON_MAC.name)
            val rssi = it.getInt(BundleEnum.BEACON_RSSI.name)
            _origin = it.getString(BundleEnum.ORIGIN.name)
            _data = Beacon(id, name!!, mac!!, rssi)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = LayoutDetailItemBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvItemId.text = _data.id.toString()
        binding.tvItemName.text = _data.name
        root.setOnClickListener {
            if (_origin == LIST_DATABASE) {
                Navigation.findNavController(root).navigate(R.id.navigation_database)
            } else {
                Navigation.findNavController(root).navigate(R.id.navigation_sync)
            }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

}