package bruno.p.pereira.gpsindoorf.ui.graph


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils.loadAnimation
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.TAG
import bruno.p.pereira.gpsindoorf.database.SQLiteHelper
import bruno.p.pereira.gpsindoorf.databinding.FragmentGraphBinding
import bruno.p.pereira.gpsindoorf.graph.drawable.DrawableGraphView
import bruno.p.pereira.gpsindoorf.graph.manager.ActionsManager
import bruno.p.pereira.gpsindoorf.services.HttpRequest
import bruno.p.pereira.gpsindoorf.ui.sync.SyncViewModel
import com.clj.fastble.BleManager
import com.clj.fastble.data.BleScanState


class GraphFragment : Fragment() {

    private val actionsManager = ActionsManager()
    private val viewModel: SyncViewModel by activityViewModels()
    private val drawView: DrawableGraphView by lazy {
        binding.drawableGraphView
    }
    private val db: SQLiteHelper by lazy {
        SQLiteHelper(this.requireContext())
    }
    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!

    //LOAD ANIMATIONS
    private val rotateOpen: Animation by lazy {
        loadAnimation(
            this.requireContext(),
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        loadAnimation(
            this.requireContext(),
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        loadAnimation(
            this.requireContext(),
            R.anim.from_bootm_anim
        )
    }
    private val toBottom: Animation by lazy {
        loadAnimation(
            this.requireContext(),
            R.anim.to_bottom_anim
        )
    }
    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BleManager.getInstance().scanSate == BleScanState.STATE_SCANNING)
            BleManager.getInstance().cancelScan()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentGraphBinding.inflate(inflater, container, false)
        drawView.setResources(actionsManager,viewModel,binding.tvDvision,binding.tvMac)

        binding.floatingActionButton.setOnClickListener { AddButtonClicked() }

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
            this.db.deleteAllEdges()
            HttpRequest.startActionDELETELoc(this.requireContext())
            HttpRequest.startActionDELETEEdge(this.requireContext())

        }
        builder.setNeutralButton(getString(R.string.cancel)) { _, _ -> }
        builder.create().show()
    }

    private fun selectNewNode() {
        val allBeacons = this.db.getAllLocations()
        var beaconsTitles: Array<String> = arrayOf()
        var macbeacons: Array<String> = arrayOf()
        for (i in allBeacons)
            if (i.latitude == "-1") {
                beaconsTitles = beaconsTitles.plus(i.division)
                macbeacons = macbeacons.plus(i.mac)
            }

        if (beaconsTitles.isNotEmpty()) {
            AlertDialog.Builder(this.requireContext()).apply {
                setTitle("Beacons to Add")
                setItems(beaconsTitles) { _, which ->
                    Log.v(TAG, beaconsTitles[which])
                    val dto = db.getFirstLocationbyMac(macbeacons[which])!!
                    dto.longitude = "540"
                    dto.latitude = "540"
                    db.updateLocation(dto)
                    Log.v(TAG, "[GRAPHFRAGMENT] Beacon added to the graph")
                    Navigation.findNavController(binding.root).navigate(R.id.navigation_graph)
                }
                show()
            }
        } else {
            AlertDialog.Builder(this.requireContext()).apply {
                setTitle("Beacons to Add")
                setMessage("You don`t have any Beacon to add")
                show()
            }
        }
    }


    private fun AddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.tvDvision.visibility = View.INVISIBLE
            binding.tvMac.visibility = View.INVISIBLE
            binding.btUndo.visibility = View.VISIBLE
            binding.btRemove.visibility = View.VISIBLE
            binding.btRedo.visibility = View.VISIBLE
            binding.btReset.visibility = View.VISIBLE
            binding.btRun.visibility = View.VISIBLE
            binding.btAdd.visibility = View.VISIBLE
        } else {
            binding.btUndo.visibility = View.INVISIBLE
            binding.btRemove.visibility = View.INVISIBLE
            binding.btRedo.visibility = View.INVISIBLE
            binding.btReset.visibility = View.INVISIBLE
            binding.btRun.visibility = View.INVISIBLE
            binding.btAdd.visibility = View.INVISIBLE
            binding.tvDvision.visibility = View.VISIBLE
            binding.tvMac.visibility = View.VISIBLE
        }

    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.btUndo.startAnimation(fromBottom)
            binding.btRemove.startAnimation(fromBottom)
            binding.btRedo.startAnimation(fromBottom)
            binding.btReset.startAnimation(fromBottom)
            binding.btRun.startAnimation(fromBottom)
            binding.btAdd.startAnimation(fromBottom)
            binding.floatingActionButton.startAnimation(rotateOpen)


        } else {
            binding.btUndo.startAnimation(toBottom)
            binding.btRemove.startAnimation(toBottom)
            binding.btRedo.startAnimation(toBottom)
            binding.btReset.startAnimation(toBottom)
            binding.btRun.startAnimation(toBottom)
            binding.btAdd.startAnimation(toBottom)
            binding.floatingActionButton.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean) {
        binding.btUndo.isClickable = !clicked
        binding.btRemove.isClickable = !clicked
        binding.btRedo.isClickable = !clicked
        binding.btReset.isClickable = !clicked
        binding.btRun.isClickable = !clicked
        binding.btAdd.isClickable = !clicked
    }


}

