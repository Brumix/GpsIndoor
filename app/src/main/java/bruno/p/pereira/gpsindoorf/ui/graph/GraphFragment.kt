package bruno.p.pereira.gpsindoorf.ui.graph


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.database.SQLiteHelper
import bruno.p.pereira.gpsindoorf.databinding.FragmentGraphBinding
import bruno.p.pereira.gpsindoorf.graph.drawable.DrawableGraphView
import bruno.p.pereira.gpsindoorf.graph.manager.ActionsManager
import bruno.p.pereira.gpsindoorf.services.HttpRequest


class GraphFragment : Fragment() {

    private val actionsManager = ActionsManager()
    private val drawView: DrawableGraphView by lazy {
        binding.drawableGraphView
    }
    private val db: SQLiteHelper by lazy {
        SQLiteHelper(this.requireContext())
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
            btReset.setOnClickListener { resetGraph() }
            btAdd.setOnClickListener { selectNewNode() }
        }
        return binding.root
    }

    private fun resetGraph() {
        val builder = AlertDialog.Builder(this@GraphFragment.requireContext())
        builder.setTitle(getString(R.string.delete_graph))
        builder.setMessage(getString(R.string.confirm_delete))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            drawView.reset()
            this.db.deleteALLLocation()
            HttpRequest.startActionDELETELoc(this.requireContext())

        }
        builder.setNeutralButton(getString(R.string.cancel)) { _, _ -> }
        builder.create().show()
    }

    private fun selectNewNode() {
        val allBeacons = this.db.getAllBeacons()
        val items = arrayListOf<String>()
        for (node in allBeacons) {
            items.add(node.mac)
        }
        val builder = AlertDialog.Builder(this@GraphFragment.requireContext())
        builder.setTitle("ADD NODE")
        builder.setItems(R.array.popup_array){_, index ->
            Toast.makeText(this@GraphFragment.requireContext(), index.toString(), Toast.LENGTH_SHORT).show()
        }

        builder.create().show()
    }

}

