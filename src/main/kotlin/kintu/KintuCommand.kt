package kintu

import picocli.CommandLine
import picocli.CommandLine.Command
import kotlin.system.exitProcess


@Command(name = "kintu", description = ["..."],
    mixinStandardHelpOptions = true,
    subcommands = [
        KintuInitCommand::class,
        KintuNewCommand::class,
        KintuEmitCommand::class
    ])
class KintuCommand {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val exitCode: Int = CommandLine(KintuCommand()).execute(*args)
            exitProcess(exitCode)
        }
    }
}