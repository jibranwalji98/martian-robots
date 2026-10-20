package martianrobots.domain

data class Input(
    val gridSize: GridSize,
    val robotInstructions: List<RobotInstruction>,
)

data class GridSize(
    val maxX: Int,
    val maxY: Int,
)

data class RobotInstruction(
    val initialState: RobotState,
    val instructions: List<Command>,
)
