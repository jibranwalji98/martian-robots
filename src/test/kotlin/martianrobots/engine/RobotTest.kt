package martianrobots.engine

import martianrobots.domain.Orientation
import martianrobots.domain.RobotState
import martianrobots.error.RobotLostError
import martianrobots.utils.TestAssertions.assertIsAppError
import martianrobots.utils.TestAssertions.assertIsSuccess
import kotlin.test.Test
import kotlin.test.assertEquals

class RobotTest {
    private val grid = Grid(5, 3)

    private fun robotAt(
        x: Int,
        y: Int,
        orientation: Orientation,
    ) = Robot(RobotState(x, y, orientation), grid)

    @Test
    fun `test rotateLeft`() {
        val robot = robotAt(1, 1, Orientation.N)

        val headings =
            (1..4).map {
                robot.rotateLeft()
                robot.getState().orientation
            }

        assertEquals(listOf(Orientation.W, Orientation.S, Orientation.E, Orientation.N), headings)
        val state = robot.getState()
        assertEquals(1 to 1, state.x to state.y)
    }

    @Test
    fun `test rotateRight`() {
        val robot = robotAt(1, 1, Orientation.N)

        val headings =
            (1..4).map {
                robot.rotateRight()
                robot.getState().orientation
            }

        assertEquals(listOf(Orientation.E, Orientation.S, Orientation.W, Orientation.N), headings)
        val state = robot.getState()
        assertEquals(1 to 1, state.x to state.y)
    }

    @Test
    fun `test moveForward`() {
        assertEquals(RobotState(2, 2, Orientation.N), robotAt(2, 1, Orientation.N).apply { moveForward() }.getState())
        assertEquals(RobotState(3, 1, Orientation.E), robotAt(2, 1, Orientation.E).apply { moveForward() }.getState())
        assertEquals(RobotState(2, 0, Orientation.S), robotAt(2, 1, Orientation.S).apply { moveForward() }.getState())
        assertEquals(RobotState(1, 1, Orientation.W), robotAt(2, 1, Orientation.W).apply { moveForward() }.getState())
    }

    @Test
    fun `test moveForward with robot loss`() {
        val robot = robotAt(3, 3, Orientation.N)

        val result = robot.moveForward()

        assertIsAppError<RobotLostError>("lost at position", result)
        assertEquals(RobotState(3, 3, Orientation.N, lost = true), robot.getState())
        assertEquals(true, grid.isScented(3, 3))
    }

    @Test
    fun `test moveForward from scented point is ignored`() {
        robotAt(3, 3, Orientation.N).moveForward()
        val secondRobot = robotAt(3, 3, Orientation.N)

        assertIsSuccess(secondRobot.moveForward())
        assertEquals(RobotState(3, 3, Orientation.N), secondRobot.getState())
    }

    @Test
    fun `test moveForward on scented point does not ignore moves that stay on the grid`() {
        robotAt(3, 3, Orientation.N).moveForward()
        val secondRobot = robotAt(3, 3, Orientation.E)

        secondRobot.moveForward()

        assertEquals(RobotState(4, 3, Orientation.E), secondRobot.getState())
    }
}
