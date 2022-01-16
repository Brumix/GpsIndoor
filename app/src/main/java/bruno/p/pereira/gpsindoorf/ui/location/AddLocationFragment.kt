package bruno.p.pereira.gpsindoorf.ui.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bruno.p.pereira.gpsindoorf.TAG
import bruno.p.pereira.gpsindoorf.database.SQLiteHelper
import bruno.p.pereira.gpsindoorf.databinding.FragmentAddLocationBinding
import bruno.p.pereira.gpsindoorf.models.DtoLocation
import bruno.p.pereira.gpsindoorf.services.HttpRequest


class AddLocationFragment : Fragment() {


    private var _binding: FragmentAddLocationBinding? = null
    private val binding get() = _binding!!
    private var beaconMac: String = ""
    private val db: SQLiteHelper by lazy {
        SQLiteHelper(this.requireContext())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            this.beaconMac = it.getString("mac")!!
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        var loc = db.getFirstLocationbyMac(beaconMac)
        if (loc != null) {
            binding.etDivisionAL.setText(loc.division)
            binding.etPlaceAL.setText(loc.place)
        }

        binding.btSaveLocationAL.setOnClickListener {
            val division = binding.etDivisionAL.text.toString()
            val place = binding.etPlaceAL.text.toString()

            if (division =="" || place=="") return@setOnClickListener

            val dto = DtoLocation(beaconMac, place, division, "0", "0")

            HttpRequest.startActionPOSTLoc(context!!, dto)
            loc = db.getFirstLocationbyMac(dto.mac)
            if (loc != null) {
                Log.v(TAG,"[ADDLOCATION] Update Location")
                db.updateLocation(dto)
            }
            else {
                Log.v(TAG,"[ADDLOCATION] Insert Location")
                db.insertLocation(dto)
            }


        }
        return root
    }

}