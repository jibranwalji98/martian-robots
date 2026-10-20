package martianrobots

import martianrobots.error.InvalidInputError
import martianrobots.utils.TestAssertions.assertIsAppError
import martianrobots.utils.TestAssertions.assertSuccessEquals
import kotlin.test.Test

class MartianRobotsIntegrationTest {
    @Test
    fun `sample input produces the expected output`() {
        assertOutput(input = "input.txt", expectedOutput = "output.txt")
    }

    @Test
    fun `scent from a lost robot stops later robots leaving the grid from that point in any direction`() {
        assertOutput(input = "scenarios/scent-input.txt", expectedOutput = "scenarios/scent-output.txt")
    }

    @Test
    fun `multiple robots can be lost in one run`() {
        assertOutput(input = "scenarios/multiple-lost-input.txt", expectedOutput = "scenarios/multiple-lost-output.txt")
    }

    @Test
    fun `malformed input fails`() {
        assertInvalidInput("errors/malformed-orientation.txt", "Expected orientation")
    }

    @Test
    fun `grid larger than the maximum coordinate fails`() {
        assertInvalidInput("errors/grid-too-large.txt", "Grid size must be integers in the range")
    }

    @Test
    fun `robot starting outside the grid fails`() {
        assertInvalidInput("errors/start-outside-grid.txt", "robot must start within")
    }

    @Test
    fun `invalid later robot fails the whole run`() {
        assertInvalidInput("errors/invalid-later-robot.txt", "Expected instructions containing only")
    }

    private fun assertOutput(
        input: String,
        expectedOutput: String,
    ) {
        assertSuccessEquals(readResource(expectedOutput).trimEnd(), MartianRobots.run(readResource(input)))
    }

    private fun assertInvalidInput(
        input: String,
        expectedMessage: String,
    ) {
        assertIsAppError<InvalidInputError>(expectedMessage, MartianRobots.run(readResource(input)))
    }

    private fun readResource(name: String): String =
        requireNotNull(javaClass.getResource("/$name")) { "Missing test resource: $name" }.readText()
}
