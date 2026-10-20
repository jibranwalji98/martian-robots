package martianrobots.serdes

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asResultOr
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapAllValues
import dev.forkhandles.result4k.mapFailure
import dev.forkhandles.result4k.resultFrom
import dev.forkhandles.result4k.zip
import martianrobots.domain.Command
import martianrobots.domain.GridSize
import martianrobots.domain.Input
import martianrobots.domain.Orientation
import martianrobots.domain.RobotInstruction
import martianrobots.domain.RobotState
import martianrobots.error.AppError
import martianrobots.error.InvalidInputError

object Parser {
    private val GRID_SIZE = Regex("""(\d+)\s+(\d+)""")
    private val ROBOT_POSITION = Regex("""(\d+)\s+(\d+)\s+(\S+)""")
    private const val MAX_COORDINATE = 50
    private const val MAX_INSTRUCTIONS = 99

    fun parse(input: String): Result<Input, AppError> {
        val lines = input.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) {
            return InvalidInputError("Input is empty").asFailure()
        }

        if (lines.size % 2 == 0) {
            return InvalidInputError(
                """
                Expected a grid size line followed by two lines per robot:
                <width> <height>
                <x> <y> <orientation>
                <instructions>
                ...
                """.trimIndent(),
            ).asFailure()
        }

        return parseGridSize(lines.first()).flatMap { gridSize ->
            lines
                .drop(1)
                .chunked(2)
                .mapAllValues { (positionLine, instruction) -> parseRobotInstruction(gridSize, positionLine, instruction) }
                .map { robotInstructions -> Input(gridSize, robotInstructions) }
        }
    }

    private fun parseGridSize(line: String): Result<GridSize, AppError> {
        val match =
            GRID_SIZE.matchEntire(line)
                ?: return InvalidInputError("Expected grid size as '<width> <height>' but got '$line'").asFailure()
        val (width, height) = match.destructured
        return validateGridSize(width, height)
    }

    private fun validateGridSize(
        x: String,
        y: String,
    ): Result<GridSize, AppError> {
        val error = InvalidInputError("Grid size must be integers in the range 0-$MAX_COORDINATE")
        return resultFrom { x.toInt() to y.toInt() }
            .mapFailure { error }
            .flatMap { (xInt, yInt) ->
                if (xInt !in 0..MAX_COORDINATE || yInt !in 0..MAX_COORDINATE) {
                    error.asFailure()
                } else {
                    GridSize(xInt, yInt).asSuccess()
                }
            }
    }

    private fun parseRobotInstruction(
        gridSize: GridSize,
        positionLine: String,
        instruction: String,
    ): Result<RobotInstruction, AppError> {
        val match =
            ROBOT_POSITION.matchEntire(positionLine)
                ?: return InvalidInputError("Expected robot position as '<x> <y> <orientation>' but got '$positionLine'").asFailure()
        val (x, y, orientation) = match.destructured
        return zip(
            validateRobotStartPosition(gridSize, x, y),
            parseOrientation(orientation),
            parseCommands(instruction),
        ) { (xInt, yInt), orientation, commands ->
            RobotInstruction(RobotState(xInt, yInt, orientation), commands)
        }
    }

    private fun parseCommands(commands: String): Result<List<Command>, AppError> {
        if (commands.length >
            MAX_INSTRUCTIONS
        ) {
            return InvalidInputError("Maximum number of instructions ($MAX_INSTRUCTIONS) exceeded").asFailure()
        }
        return commands.toList().mapAllValues { c ->
            Command.entries
                .find { it.name == c.toString() }
                .asResultOr { InvalidInputError("Expected instructions containing only ${Command.entries.joinToString()}, but got '$c'") }
        }
    }

    private fun validateRobotStartPosition(
        gridSize: GridSize,
        x: String,
        y: String,
    ): Result<Pair<Int, Int>, AppError> =
        resultFrom { x.toInt() to y.toInt() }
            .mapFailure { InvalidInputError("robot starting coordinates should be 32 bit integers") }
            .flatMap { (xInt, yInt) ->
                if (xInt !in 0..gridSize.maxX || yInt !in 0..gridSize.maxY) {
                    InvalidInputError("robot must start within the specific grid").asFailure()
                } else {
                    (xInt to yInt).asSuccess()
                }
            }

    private fun parseOrientation(orientation: String): Result<Orientation, AppError> =
        Orientation.entries
            .find { it.name == orientation }
            .asResultOr {
                InvalidInputError(
                    "Expected orientation to be one of ${Orientation.entries.joinToString()} but got '$orientation'",
                )
            }
}
