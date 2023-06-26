package kintu

import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.writeText

@Command(name = "new")
class KintuNewCommand: Runnable {
    @Parameters
    private var kintuFile: String = ""

    var fileSystem: FileSystem = FileSystems.getDefault()

    private val fileAlreadyExistsErrorMessage =
        "$kintuFile.conf already exists. Use a different name"

    override fun run() {
        val path = fileSystem.getPath("$kintuFile.kintu")
        if (path.exists()) {
            println(fileAlreadyExistsErrorMessage)
            return
        }

        val file = fileSystem.getPath("$kintuFile.kintu").createFile()
        file.writeText(
            KintuInitCommand::class.java.getResource("/sample.kintu")!!.readText()
        )
    }
}