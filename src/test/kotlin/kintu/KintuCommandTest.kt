package kintu

import com.google.common.jimfs.Jimfs
import io.kotest.matchers.should
import io.kotest.matchers.string.beEmpty
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.FileSystem
import java.nio.file.Files

class KintuCommandTest {
    private var fakeFs: FileSystem = Jimfs.newFileSystem()
    private var output: StringWriter = StringWriter()

    @Test
    fun testHappyPath() {
        val content =
            """
               { "topic": "testTopic" }
            """
        addTestFile(
            "kintu.kintu",
            content
        )

        val sw = runCommand()

        sw.toString() should beEmpty()
    }

    private fun KintuCommandTest.runCommand(): StringWriter {
        val app = KintuCommand(fakeFs)
        val cmd = CommandLine(app)
        val sw = output

        cmd.err = PrintWriter(sw)

        val exitCode = cmd.execute("kintu")
        return sw
    }

    private fun addTestFile(fileName: String, content: String) {
        val file = fakeFs.getPath(fileName)
        Files.createFile(file)
        Files.write(
            file,
            content.split("\r?\n|\r".toRegex()).toList()
        )
    }
}
