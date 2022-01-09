package bruno.p.pereira.gpsindoorf.graph.manager

import java.util.*


class ActionsManager() {
    private val actions = Stack<Action>()
    private val redoActions = Stack<Action>()

    fun undo(): Action? {
        if (actions.isEmpty()) return null
        val action = actions.pop()
        redoActions.add(action)

        return action
    }

    fun redo(): Action? {
        if (redoActions.isEmpty()) return null
        val action = redoActions.pop()
        actions.add(action)

        return action
    }

    fun addHistory(action: Action) {
        actions.push(action)
        redoActions.clear()
    }

    fun clearHistory() {
        actions.clear()
        redoActions.clear()
    }
}