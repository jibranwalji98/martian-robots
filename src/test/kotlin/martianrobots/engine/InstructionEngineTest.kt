package martianrobots.engine

import martianrobots.domain.Command
import martianrobots.domain.GridSize
import martianrobots.domain.Input
import martianrobots.domain.Orientation
import martianrobots.domain.RobotInstruction
import martianrobots.domain.RobotState
import martianrobots.utils.TestAssertions.assertSuccessEquals
import kotlin.test.Test

class InstructionEngineTest {
    @Test
    fun `test successful execute`() {
        val input =
            Input(
                GridSize(5, 3),
                listOf(
                    RobotInstruction(RobotState(1, 1, Orientation.E), commands("RFRFRFRF")),
                    RobotInstruction(RobotState(3, 2, Orientation.N), commands("FRRFLLFFRRFLL")),
                    RobotInstruction(RobotState(0, 3, Orientation.W), commands("LLFFFLFLFL")),
                ),
            )

        val expected =
            listOf(
                RobotState(1, 1, Orientation.E),
                RobotState(3, 3, Orientation.N, lost = true),
                RobotState(2, 3, Orientation.S),
            )
        assertSuccessEquals(expected, InstructionEngine.execute(input))
    }

    @Test
    fun `test lost robot ignores its remaining instructions`() {
        val input =
            Input(
                GridSize(5, 3),
                listOf(RobotInstruction(RobotState(0, 0, Orientation.S), commands("FRRF"))),
            )

        assertSuccessEquals(listOf(RobotState(0, 0, Orientation.S, lost = true)), InstructionEngine.execute(input))
    }

    @Test
    fun `test scent from previous robot protects later robots`() {
        val input =
            Input(
                GridSize(5, 3),
                listOf(
                    RobotInstruction(RobotState(5, 3, Orientation.E), commands("F")),
                    RobotInstruction(RobotState(5, 3, Orientation.E), commands("FL")),
                ),
            )

        val expected =
            listOf(
                RobotState(5, 3, Orientation.E, lost = true),
                RobotState(5, 3, Orientation.N),
            )
        assertSuccessEquals(expected, InstructionEngine.execute(input))
    }

    @Test
    fun `test robots with no instructions stay where they started`() {
        val input = Input(GridSize(5, 3), listOf(RobotInstruction(RobotState(2, 2, Orientation.W), emptyList())))

        assertSuccessEquals(listOf(RobotState(2, 2, Orientation.W)), InstructionEngine.execute(input))
    }

    private fun commands(instruction: String): List<Command> = instruction.map { Command.valueOf(it.toString()) }
}
