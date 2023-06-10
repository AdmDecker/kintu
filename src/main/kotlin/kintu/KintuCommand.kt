package kintu

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import io.micronaut.configuration.picocli.PicocliRunner
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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
        val json = Json.decodeFromString<KintuFile>(file)
        println(json.topic)
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

@Serializable
data class KintuFile(
    val topic: String
)
