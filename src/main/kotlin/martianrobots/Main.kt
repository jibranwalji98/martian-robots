package martianrobots

import dev.forkhandles.result4k.peek
import dev.forkhandles.result4k.peekFailure
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.system.exitProcess

private val DATA_DIR = Path("data")

private const val USAGE = "Usage: ./gradlew run --args=\"<input-filename>\""

fun main(args: Array<String>) {
    val inputFile =
        DATA_DIR /
            args.getOrElse(0) {
                System.err.println("No input file provided. $USAGE")
                exitProcess(1)
            }
    val outputFile = DATA_DIR / args.getOrElse(1) { "output.txt" }

    if (!inputFile.exists()) {
        System.err.println("Input file not found: ${inputFile.absolutePathString()}")
        exitProcess(1)
    }

    MartianRobots
        .run(inputFile.readText())
        .peek { outputFile.writeText(it + "\n") }
        .peekFailure {
            System.err.println("${it::class.simpleName}: ${it.message}")
            exitProcess(1)
        }
}
