package kintu

import picocli.CommandLine.Command
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import kotlin.io.path.createFile
import kotlin.io.path.writeText

@Command(name = "init", description = ["initialize kintu with a kintu.conf file"])
class KintuInitCommand: Runnable {
    var fileSystem: FileSystem = FileSystems.getDefault()

    private val alreadyInitErrorMessage =
        "kintu.conf already exists. Use 'kintu new <name>' to generate a kintu file"

    override fun run() {
        val fileGetter = FileGetter(fileSystem)

        if (fileGetter.getFileOrNull("kintu.conf") != null) {
            println(alreadyInitErrorMessage)
            return
        }

        val file = fileSystem.getPath("kintu.conf").createFile()
        file.writeText(
            KintuInitCommand::class.java.getResource("/kintu.conf")!!.readText()
        )
    }
}