package martianrobots.engine

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.peekFailure
import martianrobots.domain.Command
import martianrobots.domain.Input
import martianrobots.domain.RobotState
import martianrobots.error.AppError
import martianrobots.error.RobotLostError

object InstructionEngine {
    fun execute(input: Input): Result<List<RobotState>, AppError> {
        val grid = Grid(input.gridSize.maxX, input.gridSize.maxY)
        val finalStates = mutableListOf<RobotState>()
        for ((initialState, instructions) in input.robotInstructions) {
            val robot = Robot(initialState, grid)
            loop@ for (command in instructions) {
                command.action()(robot).peekFailure { error ->
                    when (error) {
                        is RobotLostError -> break@loop
                        else -> return error.asFailure()
                    }
                }
            }
            finalStates.add(robot.getState())
        }
        return finalStates.asSuccess()
    }

    private fun Command.action(): (Robot) -> Result<Unit, AppError> =
        when (this) {
            Command.L -> Robot::rotateLeft
            Command.R -> Robot::rotateRight
            Command.F -> Robot::moveForward
        }
}
