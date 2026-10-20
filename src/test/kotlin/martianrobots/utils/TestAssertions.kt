package martianrobots.utils

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.onFailure
import martianrobots.error.AppError
import org.junit.jupiter.api.fail
import kotlin.test.assertContains
import kotlin.test.assertEquals

object TestAssertions {
    fun <T> assertSuccessEquals(
        expected: T,
        actual: Result<T, *>,
    ) {
        val value = assertIsSuccess(actual)
        assertEquals(expected, value)
    }

    inline fun <reified E : AppError> assertIsAppError(
        expectedMessage: String,
        actual: Result<*, AppError>,
    ) {
        when (val error = assertIsFailure(actual)) {
            is E -> assertContains(error.message, expectedMessage)
            else -> fail("Expected error of type: ${E::class.simpleName}, found: ${error.javaClass.simpleName}: ${error.message}")
        }
    }

    fun <T> assertIsSuccess(actual: Result<T, *>): T = actual.onFailure { fail("Expected success, but got failure: $it") }

    fun <E> assertIsFailure(actual: Result<*, E>): E =
        when (actual) {
            is Success -> fail("Expected failure, but got success: ${actual.value}")
            is Failure -> actual.reason
        }
}
