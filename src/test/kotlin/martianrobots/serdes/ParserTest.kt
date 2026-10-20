package martianrobots.serdes

import martianrobots.domain.Command
import martianrobots.domain.GridSize
import martianrobots.domain.Input
import martianrobots.domain.Orientation
import martianrobots.domain.RobotInstruction
import martianrobots.domain.RobotState
import martianrobots.error.InvalidInputError
import martianrobots.serdes.Parser.parse
import martianrobots.utils.TestAssertions.assertIsAppError
import martianrobots.utils.TestAssertions.assertSuccessEquals
import kotlin.test.Test

class ParserTest {
    @Test
    fun `parse grid size and robot instructions`() {
        val input =
            """
            5 3
            1 1 E
            RFRFRFRF
            3 2 N
            FRRFLLFFRRFLL
            0 3 W
            LLFFFLFLFL
            """.trimIndent()

        val expected =
            Input(
                gridSize = GridSize(5, 3),
                robotInstructions =
                    listOf(
                        RobotInstruction(RobotState(1, 1, Orientation.E), commands("RFRFRFRF")),
                        RobotInstruction(RobotState(3, 2, Orientation.N), commands("FRRFLLFFRRFLL")),
                        RobotInstruction(RobotState(0, 3, Orientation.W), commands("LLFFFLFLFL")),
                    ),
            )
        assertSuccessEquals(expected, parse(input))
    }

    @Test
    fun `ignore blank lines and surrounding whitespace`() {
        val input = "\n  5 3  \n\n1 1 E\nRFRF\n\n"

        val expected = Input(GridSize(5, 3), listOf(RobotInstruction(RobotState(1, 1, Orientation.E), commands("RFRF"))))
        assertSuccessEquals(expected, parse(input))
    }

    @Test
    fun `parse grid with no robots`() {
        assertSuccessEquals(Input(GridSize(5, 3), emptyList()), parse("5 3"))
    }

    @Test
    fun `fail on empty input`() {
        assertIsAppError<InvalidInputError>("Input is empty", parse(""))
    }

    @Test
    fun `fail on malformed grid size`() {
        assertIsAppError<InvalidInputError>("Expected grid size", parse("5\n1 1 E\nF"))
        assertIsAppError<InvalidInputError>("Expected grid size", parse("5 x\n1 1 E\nF"))
        assertIsAppError<InvalidInputError>("Expected grid size", parse("5 3 2\n1 1 E\nF"))
        assertIsAppError<InvalidInputError>("Expected grid size", parse("-5 3\n1 1 E\nF"))
        assertIsAppError<InvalidInputError>("Grid size must be integers in the range", parse("9999999999 3\n1 1 E\nF"))
    }

    @Test
    fun `fail on malformed robot position`() {
        assertIsAppError<InvalidInputError>("Expected robot position", parse("5 3\n1 1\nF"))
        assertIsAppError<InvalidInputError>("Expected robot position", parse("5 3\n1 a E\nF"))
        assertIsAppError<InvalidInputError>("Expected orientation", parse("5 3\n1 1 Q\nF"))
        assertIsAppError<InvalidInputError>("Expected orientation", parse("5 3\n1 1 e\nF"))
        assertIsAppError<InvalidInputError>("Expected orientation", parse("5 3\n1 1 EN\nF"))
        assertIsAppError<InvalidInputError>("Expected robot position", parse("5 3\n-1 1 E\nF"))
    }

    @Test
    fun `fail when a robot has no instruction line`() {
        assertIsAppError<InvalidInputError>("two lines per robot", parse("5 3\n1 1 E\nRF\n3 2 N"))
    }

    @Test
    fun `fail on unrecognised instruction characters`() {
        assertIsAppError<InvalidInputError>("Expected instructions containing only", parse("5 3\n1 1 E\nRFX"))
    }

    @Test
    fun `fail when grid size exceeds the maximum coordinate`() {
        assertIsAppError<InvalidInputError>("Grid size must be integers in the range", parse("51 3"))
        assertIsAppError<InvalidInputError>("Grid size must be integers in the range", parse("5 51"))
    }

    @Test
    fun `fail when a robot starts outside the grid`() {
        assertIsAppError<InvalidInputError>("robot must start within", parse("5 3\n6 0 N\nF"))
        assertIsAppError<InvalidInputError>("robot must start within", parse("5 3\n0 4 N\nF"))
        assertIsAppError<InvalidInputError>("robot must start within", parse("5 3\n6 4 N\nF"))
    }

    @Test
    fun `fail when a robot start coordinate is too large for an Int`() {
        assertIsAppError<InvalidInputError>("robot starting coordinates should be 32 bit integers", parse("5 3\n99999999999 1 E\nF"))
        assertIsAppError<InvalidInputError>("robot starting coordinates should be 32 bit integers", parse("5 3\n1 99999999999 E\nF"))
    }

    @Test
    fun `fail when instructions greater than 99`() {
        assertIsAppError<InvalidInputError>("Maximum number of instructions", parse("5 3\n1 1 E\n${"F".repeat(100)}"))
    }

    private fun commands(instruction: String): List<Command> = instruction.map { Command.valueOf(it.toString()) }
}
