package bruno.p.pereira.gpsindoorf.graph.manager.actions

import bruno.p.pereira.gpsindoorf.graph.drawable.DrawableNode
import bruno.p.pereira.gpsindoorf.graph.manager.Action
import bruno.p.pereira.gpsindoorf.graph.manager.HistoryAction


class ActionAdd(val drawableNode: DrawableNode):
    Action {
    private val type = HistoryAction.ADD

    override fun getType(): HistoryAction {
        return type
    }
}