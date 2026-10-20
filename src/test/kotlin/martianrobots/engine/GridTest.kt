package martianrobots.engine

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GridTest {
    private val grid = Grid(5, 3)

    @Test
    fun `test isOutOfBounds false`() {
        listOf(0 to 0, 5 to 0, 0 to 3, 5 to 3, 2 to 1).forEach { (x, y) ->
            assertFalse(grid.isOutOfBounds(x, y), "($x,$y) should be in bounds")
        }
    }

    @Test
    fun `test isOutOfBounds true`() {
        listOf(-1 to 0, 6 to 0, 0 to -1, 0 to 4, -1 to -1, 6 to 4).forEach { (x, y) ->
            assertTrue(grid.isOutOfBounds(x, y), "($x,$y) should be out of bounds")
        }
    }

    @Test
    fun `test leaveScent`() {
        assertFalse(grid.isScented(1, 2))
        grid.leaveScent(1, 2)
        assertTrue(grid.isScented(1, 2), "(1,2) should be scented")
    }
}
