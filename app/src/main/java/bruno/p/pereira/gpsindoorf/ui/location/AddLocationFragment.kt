package bruno.p.pereira.gpsindoorf.ui.location

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bruno.p.pereira.gpsindoorf.databinding.FragmentAddLocationBinding



class AddLocationFragment : Fragment() {


    private var _binding: FragmentAddLocationBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

}