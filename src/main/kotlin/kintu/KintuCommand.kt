package kintu

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import io.micronaut.configuration.picocli.PicocliRunner
import picocli.CommandLine.*
import java.io.File

@Command(name = "kintu", description = ["..."],
        mixinStandardHelpOptions = true)
class KintuCommand : Runnable {

    @Option(names = ["-v", "--verbose"], description = ["..."])
    private var verbose : Boolean = false

    @Parameters
    private var kintuFile: String = ""

    override fun run() {
        val config = readConfig()
        val fileName = "$kintuFile.kintu"
        val file = File(fileName).readText()
        println(file)
    }

    private fun readConfig(): Config {
        return ConfigLoaderBuilder.default()
            .addFileSource("kintu.conf")
            .build()
            .loadConfigOrThrow<Config>()
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            PicocliRunner.run(KintuCommand::class.java, *args)
        }
    }
}

data class Config(
    val environment: String
)
