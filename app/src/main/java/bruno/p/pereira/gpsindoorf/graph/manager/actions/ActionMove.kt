package bruno.p.pereira.gpsindoorf.graph.manager.actions

import bruno.p.pereira.gpsindoorf.graph.drawable.DrawableNode
import bruno.p.pereira.gpsindoorf.graph.manager.Action
import bruno.p.pereira.gpsindoorf.graph.manager.HistoryAction


class ActionMove(val node: DrawableNode, val initialPosition: Pair<Float, Float>,
                 val finalPosition: Pair<Float, Float>): Action {
    private val type = HistoryAction.MOVE

    override fun getType(): HistoryAction {
        return type
    }
}