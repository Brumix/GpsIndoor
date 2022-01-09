package bruno.p.pereira.gpsindoorf.ui.graph

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.databinding.FragmentGraphBinding
import bruno.p.pereira.gpsindoorf.graph.manager.ActionsManager


class GraphFragment : Fragment() {

    private val actionsManager = ActionsManager()
    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.drawableGraphView.setActionsManager(actionsManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    fun runAlgorithm(view: View) = binding.drawableGraphView.runAlgorithm()


    fun redo(view: View) =binding.drawableGraphView.redo()


    fun removeNode(view: View) = binding.drawableGraphView.removeSelectedNode()


    fun reset(view: View) {
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle(getString(R.string.delete_graph))
        builder.setMessage(getString(R.string.confirm_delete))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            binding.drawableGraphView.reset()

        }
        builder.setNeutralButton(getString(R.string.cancel)) { _, _ -> }
        builder.create().show()
    }

    fun undo(view: View) = binding.drawableGraphView.undo()

}