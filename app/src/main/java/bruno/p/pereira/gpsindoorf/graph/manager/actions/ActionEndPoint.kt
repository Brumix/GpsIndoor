package bruno.p.pereira.gpsindoorf.graph.manager.actions

import bruno.p.pereira.gpsindoorf.graph.drawable.DrawableNode
import bruno.p.pereira.gpsindoorf.graph.manager.Action
import bruno.p.pereira.gpsindoorf.graph.manager.HistoryAction


class ActionEndPoint(val node: DrawableNode): Action {
    private val type = HistoryAction.END_POINT

    override fun getType(): HistoryAction {
        return type
    }
}