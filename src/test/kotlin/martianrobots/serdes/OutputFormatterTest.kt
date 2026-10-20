package martianrobots.serdes

import martianrobots.domain.Orientation
import martianrobots.domain.RobotState
import martianrobots.serdes.OutputFormatter.format
import kotlin.test.Test
import kotlin.test.assertEquals

class OutputFormatterTest {
    @Test
    fun `formats final robot states one per line`() {
        val finalStates =
            listOf(
                RobotState(1, 1, Orientation.E),
                RobotState(3, 3, Orientation.N, lost = true),
                RobotState(2, 3, Orientation.S),
            )

        assertEquals("1 1 E\n3 3 N LOST\n2 3 S", format(finalStates))
    }

    @Test
    fun `formats no robots as empty string`() {
        assertEquals("", format(emptyList()))
    }
}
