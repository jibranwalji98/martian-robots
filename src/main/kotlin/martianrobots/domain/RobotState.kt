package martianrobots.domain

data class RobotState(
    val x: Int,
    val y: Int,
    val orientation: Orientation,
    val lost: Boolean = false,
)
