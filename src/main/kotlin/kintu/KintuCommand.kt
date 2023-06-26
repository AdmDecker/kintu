package kintu

import com.sksamuel.hoplite.ConfigAlias
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addStreamSource
import io.micronaut.configuration.picocli.PicocliRunner
import jakarta.inject.Inject
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import picocli.CommandLine.*
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.inputStream

@Command(name = "kintu", description = ["..."],
        mixinStandardHelpOptions = true)
class KintuCommand : Runnable {
    var fileSystem: FileSystem = FileSystems.getDefault()
    @Inject lateinit var kintuProcessor: KintuFileProcessor

    @Option(names = ["-v", "--verbose"], description = ["..."])
    private var verbose : Boolean = false

    @Parameters
    private var kintuFile: String = ""

    @Spec
    lateinit var spec: Model.CommandSpec

    override fun run() {
        val config = readConfig()
        if (config != null) {
            if (kintuFile.isNotBlank()) {
                val file = getFileOrNull("$kintuFile.kintu")
                if (file != null) {
                    val t = Files.readString(file)
                    val kintuFile = Json.decodeFromString<KintuFile>(t)
                    kintuProcessor.processFile(config, kintuFile)
                }
            }
        }
        else spec.commandLine().err.println(
            "Missing kintu.config. Run 'kintu init' to generate")
    }

    private fun getFileOrNull(fileName: String): Path? {
        val workingDir = fileSystem.getPath("").toAbsolutePath()
        return climbForFile(workingDir, fileName)
    }


    private fun climbForFile(dir: Path, fileName: String): Path? {
        val file = dir.resolve(fileName)
        if (file.exists()) {
            return file
        }

        return if (dir.parent != null) climbForFile(dir.parent, fileName)
        else null
    }

    private fun readConfig(): Config? {
        val configFile = getFileOrNull("kintu.conf")?.inputStream()
        return if (configFile != null) {
            return ConfigLoaderBuilder.default()
                .addStreamSource(configFile, "conf")
                .build()
                .loadConfigOrThrow<Config>()
        } else null
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            PicocliRunner.run(KintuCommand::class.java, *args)
        }
    }
}

data class Config(
    val environment: String,
    @ConfigAlias("kafka") val kafkaConfig: KafkaConfig
)

data class KafkaConfig(
    val servers: String
)

@Serializable
data class KintuFile(
    val topic: String,
    val randomize: List<String>? = null,
    val payload: JsonObject
)

interface KintuFileProcessor {
    fun processFile(config: Config, kintuFile: KintuFile)
}

