package martianrobots.domain

enum class Orientation(
    val vec: Vector,
) {
    N(Vector(0, 1)),
    S(Vector(0, -1)),
    E(Vector(1, 0)),
    W(Vector(-1, 0)),
    ;

    companion object {
        fun fromVector(vec: Vector): Orientation? = entries.find { it.vec == vec }
    }
}
