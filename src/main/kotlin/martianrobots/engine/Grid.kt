package martianrobots.engine

class Grid(
    private val maxX: Int,
    private val maxY: Int,
) {
    private val scented = Array(maxY + 1) { IntArray(maxX + 1) { 0 } }

    fun leaveScent(
        x: Int,
        y: Int,
    ) {
        scented[y][x] = 1
    }

    fun isScented(
        x: Int,
        y: Int,
    ): Boolean = scented[y][x] == 1

    fun isOutOfBounds(
        x: Int,
        y: Int,
    ): Boolean = x !in 0..maxX || y !in 0..maxY
}
