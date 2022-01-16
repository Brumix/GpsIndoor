package bruno.p.pereira.gpsindoorf.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import bruno.p.pereira.gpsindoorf.database.SQLiteHelper
import bruno.p.pereira.gpsindoorf.databinding.FragmentDetailBinding
import bruno.p.pereira.gpsindoorf.enums.BundleEnum
import bruno.p.pereira.gpsindoorf.models.Beacon
import bruno.p.pereira.gpsindoorf.services.HttpRequest
import com.clj.fastble.BleManager
import com.clj.fastble.data.BleScanState


class DetailFragment : Fragment() {


    private lateinit var _data: Beacon
    private var _origin: String? = null
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val db: SQLiteHelper by lazy {
        SQLiteHelper(this.requireContext())
    }



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

        HttpRequest.startActionGETHist(this.requireContext(),_data.mac)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvItemId.text = _data.id.toString()
        binding.tvItemName.text = _data.name
        binding.tvItemMac.text = _data.mac
        binding.tvItemRssi.text = _data.rssi.toString()

       /* root.setOnClickListener {
            if (_origin == LIST_DATABASE) {
                Navigation.findNavController(root).navigate(R.id.navigation_database)
            } else {
                Navigation.findNavController(root).navigate(R.id.navigation_sync)
            }
        }*/
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = HistoryAdpter(db,_data.mac,)
        }



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}