package bruno.p.pereira.gpsindoorf.graph.manager.actions

import bruno.p.pereira.gpsindoorf.graph.drawable.DrawableNode
import bruno.p.pereira.gpsindoorf.graph.manager.Action
import bruno.p.pereira.gpsindoorf.graph.manager.HistoryAction


class ActionConnect(val nodeA: DrawableNode, val nodeB: DrawableNode): Action {
    private val type = HistoryAction.CONNECT

    override fun getType(): HistoryAction {
        return type
    }
}