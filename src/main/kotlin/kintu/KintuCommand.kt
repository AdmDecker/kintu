package kintu

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addStreamSource
import io.micronaut.configuration.picocli.PicocliRunner
import jakarta.inject.Inject
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import picocli.CommandLine.*
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.inputStream

@Command(name = "kintu", description = ["..."],
        mixinStandardHelpOptions = true)
class KintuCommand(
    @Inject val fileSystem: FileSystem = FileSystems.getDefault(),
    @Inject val kintuProcessor: KintuFileProcessor = KintuProcessor()
) : Runnable {

    @Option(names = ["-v", "--verbose"], description = ["..."])
    private var verbose : Boolean = false

    @Parameters
    private var kintuFile: String = ""

    override fun run() {
        val config = readConfig()
        if (config != null) {
            if (kintuFile.isNotBlank()) {
                val file = getFileOrNull("$kintuFile.kintu")
                val t = Files.readString(file!!)
                val kintuFile = Json.decodeFromString<KintuFile>(t)
                kintuProcessor.processFile(config, kintuFile)
            }
        }
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
    val environment: String
)

@Serializable
data class KintuFile(
    val topic: String
)

interface KintuFileProcessor {
    fun processFile(config: Config, kintuFile: KintuFile)
}

class KintuProcessor: KintuFileProcessor {
    override fun processFile(config: Config, kintuFile: KintuFile) {
        println(kintuFile.topic)
    }
}