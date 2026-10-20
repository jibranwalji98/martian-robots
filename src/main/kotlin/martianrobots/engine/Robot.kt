package martianrobots.engine

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import martianrobots.domain.Orientation
import martianrobots.domain.RobotState
import martianrobots.domain.Vector
import martianrobots.error.AppError
import martianrobots.error.RobotLostError

class Robot(
    initialState: RobotState,
    private val grid: Grid,
) {
    private var position = Vector(initialState.x, initialState.y)
    private var orientation = initialState.orientation.vec
    private var lost = initialState.lost

    fun rotateLeft(): Result<Unit, AppError> {
        orientation = orientation.rotateLeft()
        return Unit.asSuccess()
    }

    fun rotateRight(): Result<Unit, AppError> {
        orientation = orientation.rotateRight()
        return Unit.asSuccess()
    }

    fun moveForward(): Result<Unit, AppError> {
        val newPosition = position + orientation
        if (grid.isOutOfBounds(newPosition.x, newPosition.y)) {
            if (grid.isScented(position.x, position.y)) {
                return Unit.asSuccess()
            } else {
                grid.leaveScent(position.x, position.y)
                lost = true
                return RobotLostError("engine.Robot lost at position (${position.x},${position.y}").asFailure()
            }
        }
        position = newPosition
        return Unit.asSuccess()
    }

    fun getState(): RobotState =
        RobotState(
            position.x,
            position.y,
            checkNotNull(Orientation.fromVector(orientation)) { "Unexpected error: No orientation for vector $orientation" },
            lost,
        )
}
