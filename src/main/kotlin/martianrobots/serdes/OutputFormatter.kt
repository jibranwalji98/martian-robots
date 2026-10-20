package martianrobots.serdes

import martianrobots.domain.RobotState

object OutputFormatter {
    fun format(output: List<RobotState>): String = output.joinToString("\n") { formatRobotState(it) }

    private fun formatRobotState(state: RobotState): String {
        val position = "${state.x} ${state.y} ${state.orientation.name}"
        return if (state.lost) "$position LOST" else position
    }
}
