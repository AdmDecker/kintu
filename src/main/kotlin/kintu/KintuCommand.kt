package kintu

import io.micronaut.configuration.picocli.PicocliRunner
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(name = "kintu", description = ["..."],
        mixinStandardHelpOptions = true)
class KintuCommand : Runnable {

    @Option(names = ["-v", "--verbose"], description = ["..."])
    private var verbose : Boolean = false

    override fun run() {
        // business logic here
        if (verbose) {
            println("Hello!")
        }
        else println("Hi!")
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            PicocliRunner.run(KintuCommand::class.java, *args)
        }
    }
}
