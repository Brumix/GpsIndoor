package bruno.p.pereira.gpsindoorf.graph.manager.actions

import bruno.p.pereira.gpsindoorf.graph.drawable.WeighBox
import bruno.p.pereira.gpsindoorf.graph.manager.Action
import bruno.p.pereira.gpsindoorf.graph.manager.HistoryAction


class ActionWeigh(val weighBox: WeighBox, val weight: Double): Action {
    private val type = HistoryAction.WEIGH

    override fun getType(): HistoryAction {
        return type
    }

}