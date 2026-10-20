package martianrobots.domain

data class Vector(
    val x: Int,
    val y: Int,
) {
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)

    fun rotateLeft() = Vector(-y, x)

    fun rotateRight() = Vector(y, -x)

    override fun toString(): String = "($x, $y)"
}
