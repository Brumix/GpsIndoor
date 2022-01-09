package bruno.p.pereira.gpsindoorf.graph.manager.actions

import bruno.p.pereira.gpsindoorf.graph.drawable.DrawableNode
import bruno.p.pereira.gpsindoorf.graph.manager.Action
import bruno.p.pereira.gpsindoorf.graph.manager.HistoryAction


class ActionStartPoint(val node: DrawableNode): Action {
    private val type = HistoryAction.START_POINT

    override fun getType(): HistoryAction {
        return type
    }
}