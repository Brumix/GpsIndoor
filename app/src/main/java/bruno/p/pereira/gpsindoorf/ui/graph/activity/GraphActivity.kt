package bruno.p.pereira.gpsindoorf.ui.graph.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.graph.drawable.DrawableGraphView
import bruno.p.pereira.gpsindoorf.graph.manager.ActionsManager

class GraphActivity : AppCompatActivity() {

    private val actionsManager = ActionsManager()
    private val drawView: DrawableGraphView by lazy {
        findViewById(R.id.drawableGraphView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        drawView.setActionsManager(actionsManager)
    }

    fun runAlgorithm(view: View) = drawView.runAlgorithm()


    fun redoAlgorithm(view: View) = drawView.redo()


    fun removeNode(view: View) = drawView.removeSelectedNode()


    fun reset(view: View) {
        val builder = AlertDialog.Builder(this@GraphActivity)
        builder.setTitle(getString(R.string.delete_graph))
        builder.setMessage(getString(R.string.confirm_delete))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            drawView.reset()

        }
        builder.setNeutralButton(getString(R.string.cancel)) { _, _ -> }
        builder.create().show()
    }

    fun undo(view: View) = drawView.undo()
}