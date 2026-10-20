# Martian Robots

## Key Design Decisions

- **`result4k` over exceptions:** more explicit, readable and efficient but requires a dependency.
- **Exhaustive command handling:** Instructions are parsed into a `Command` enum and mapped to robot
  behaviour with an exhaustive `when`. Adding a new command without an implementation is a compile error.
- **Directions as vectors:** Orientation is a unit vector, so moving becomes `position + direction` and turning is a 90° rotation, with no per-direction lookups or list cycling.
- **Addition of integration test suite:** A sanity check that runs real input files through the full pipeline
  and compares the result with expected output. Any new scenarios can be added as plain text files without writing any code.

## Prerequisites

- A local JDK or JRE installation of 17+.
- Ensure you have execution permissions on the gradle wrapper script:` sudo chmod+ gradlew`

## Assumptions

- Scent applies to a grid point, not a direction. On a corner this covers both edges, even though the first robot only fell off one of them.
- Grid state is not maintained between runs.
- No instructions should be executed if any line of input fails to parse.

## Run

In order to run the program, use the following command:

```
./gradlew run --args="<input-filename> <output-filename>"
```

Input files should be in `.txt` format and placed in the `/data` directory. Output files
will also be saved to same directory as `.txt` files. If the output filename is not specified
it will default to `output.txt`.

## Tests

Test coverage is measured with JaCoCo across both unit and integration tests with a failure threshold of 80%.
The HTML report is written to `build/reports/jacoco/test/html/index.html` as part of the build `./gralew build`.

In order to run the unit tests, use the following command:

```
./gradlew test
```

In order to run the integration tests, use the following command:

```
./gradlew integrationTest
```

## Future Improvements

- Validation has been conflated with parsing, this could be pulled into a separate unit.
- Better error reporting on parsing failure, specifying the line and column.
