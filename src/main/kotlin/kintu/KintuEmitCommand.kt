package kintu

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addStreamSource
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import picocli.CommandLine.Command
import picocli.CommandLine.Model
import picocli.CommandLine.Parameters
import picocli.CommandLine.Spec
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.inputStream

@Command(name = "emit", description = ["emit an event defined by a .kintu file"],
        mixinStandardHelpOptions = true)
class KintuEmitCommand : Runnable {
    var fileSystem: FileSystem = FileSystems.getDefault()
    var kintuProcessor: KintuFileProcessor = KintuProcessor(KafkaClient())

    @Spec
    lateinit var spec: Model.CommandSpec

    @Parameters
    private var kintuFile: String = ""

    override fun run() {
        val config = readConfig()
        if (config != null) {
            if (kintuFile.isNotBlank()) {
                val file = FileGetter(fileSystem).getFileOrNull("$kintuFile.kintu")
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

    private fun readConfig(): Config? {
        val configFile = FileGetter(fileSystem).getFileOrNull("kintu.conf")?.inputStream()
        return if (configFile != null) {
            return ConfigLoaderBuilder.default()
                .addStreamSource(configFile, "conf")
                .build()
                .loadConfigOrThrow<Config>()
        } else null
    }
}

class FileGetter(val fileSystem: FileSystem) {
    fun getFileOrNull(fileName: String): Path? {
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
}

@Serializable
data class Config(
    val environment: String,
    val kafka: Map<String, String>
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

