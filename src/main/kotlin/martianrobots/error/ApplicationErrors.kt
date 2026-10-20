package martianrobots.error

sealed interface AppError {
    val message: String
}

data class RobotLostError(
    override val message: String,
) : AppError

data class InvalidInputError(
    override val message: String,
) : AppError
