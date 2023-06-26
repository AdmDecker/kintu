package kintu

import io.micronaut.configuration.picocli.PicocliRunner
import picocli.CommandLine.Command


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
            PicocliRunner.execute(KintuCommand::class.java, *args)
        }
    }
}