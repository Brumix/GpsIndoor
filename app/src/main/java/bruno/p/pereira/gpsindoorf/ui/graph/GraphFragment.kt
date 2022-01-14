package bruno.p.pereira.gpsindoorf.ui.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.databinding.FragmentGraphBinding
import bruno.p.pereira.gpsindoorf.graph.drawable.DrawableGraphView
import bruno.p.pereira.gpsindoorf.graph.manager.ActionsManager


class GraphFragment : Fragment() {

    private val actionsManager = ActionsManager()
    private val drawView: DrawableGraphView by lazy {
        binding.drawableGraphView
    }
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
        drawView.setActionsManager(actionsManager)

        binding.apply {
            btRun.setOnClickListener { drawView.runAlgorithm() }
            btRedo.setOnClickListener { drawableGraphView.redo() }
            btUndo.setOnClickListener { drawableGraphView.undo() }
            btRemove.setOnClickListener { drawableGraphView.removeSelectedNode() }
            btReset.setOnClickListener {
                val builder = AlertDialog.Builder(this@GraphFragment.requireContext())
                builder.setTitle(getString(R.string.delete_graph))
                builder.setMessage(getString(R.string.confirm_delete))
                builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    drawView.reset()

                }
                builder.setNeutralButton(getString(R.string.cancel)) { _, _ -> }
                builder.create().show()
            }
        }
        return binding.root
    }

}

