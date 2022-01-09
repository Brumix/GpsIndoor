package bruno.p.pereira.gpsindoorf.graph.manager.actions

import bruno.p.pereira.gpsindoorf.graph.data.Edge
import bruno.p.pereira.gpsindoorf.graph.drawable.DrawableNode
import bruno.p.pereira.gpsindoorf.graph.drawable.WeighBox
import bruno.p.pereira.gpsindoorf.graph.manager.Action
import bruno.p.pereira.gpsindoorf.graph.manager.HistoryAction


class ActionRemove(private val drawableNode: DrawableNode,
                   private val weighBoxes: List<WeighBox>,
                   private val edgesConnections: List<Boolean>
): Action {
    private val type = HistoryAction.REMOVE

    override fun getType(): HistoryAction {
        return type
    }

    fun getNode(): DrawableNode {
        return drawableNode
    }

    fun getDrawableEdges(): List<WeighBox> {
        return weighBoxes
    }

    fun getEdges(): List<Edge?> {
        return weighBoxes.map { drawableEdge -> drawableEdge.edge }
    }

    fun getConnections(): List<Boolean> {
        return edgesConnections
    }
}