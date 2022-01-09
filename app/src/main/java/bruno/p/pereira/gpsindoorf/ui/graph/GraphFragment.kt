package bruno.p.pereira.gpsindoorf.ui.graph

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.databinding.FragmentBeaconsBinding
import bruno.p.pereira.gpsindoorf.databinding.FragmentGraphBinding
import bruno.p.pereira.gpsindoorf.ui.graph.activity.GraphActivity


class GraphFragment : Fragment() {


    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentGraphBinding.inflate(inflater, container, false)

        binding.floatingGraph.setOnClickListener {
                startActivity(Intent(this.requireContext(),GraphActivity::class.java))
        }

        return binding.root
    }
}