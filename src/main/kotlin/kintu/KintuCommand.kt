package kintu

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import io.micronaut.configuration.picocli.PicocliRunner
import jakarta.inject.Inject
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import picocli.CommandLine.*
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files

@Command(name = "kintu", description = ["..."],
        mixinStandardHelpOptions = true)
class KintuCommand(
    @Inject val fileSystem: FileSystem = FileSystems.getDefault(),
    @Inject val kintuProcessor: KintuFileProcessor
) : Runnable {

    @Option(names = ["-v", "--verbose"], description = ["..."])
    private var verbose : Boolean = false

    @Parameters
    private var kintuFile: String = ""

    override fun run() {
        val config = readConfig()
        if (kintuFile.isNotBlank()) {
            val fileName = "$kintuFile.kintu"
            val file = fileSystem.getPath(fileName)
            val t = Files.readString(file)
            val kintuFile = Json.decodeFromString<KintuFile>(t)
            kintuProcessor.processFile(kintuFile)
        }

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

interface KintuFileProcessor {
    fun processFile(kintuFile: KintuFile)
}
