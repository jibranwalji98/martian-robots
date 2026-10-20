package martianrobots

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import martianrobots.engine.InstructionEngine
import martianrobots.error.AppError
import martianrobots.serdes.OutputFormatter
import martianrobots.serdes.Parser

object MartianRobots {
    fun run(input: String): Result<String, AppError> =
        Parser
            .parse(input)
            .flatMap { InstructionEngine.execute(it) }
            .map { OutputFormatter.format(it) }
}
