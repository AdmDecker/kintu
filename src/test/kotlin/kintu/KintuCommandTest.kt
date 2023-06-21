package kintu

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.should
import io.kotest.matchers.string.beEmpty
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.FileSystem
import java.nio.file.Files

class KintuCommandTest {
    private lateinit var fakeFs: FileSystem
    private var err: StringWriter = StringWriter()
    private var capturedConfig = CapturingSlot<Config>()
    private var capturedKintuFile = CapturingSlot<KintuFile>()
    private val mockKintuProcessor = mockk<KintuFileProcessor>(relaxed = true)
    private var workingDirectory = "/home/work"

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
        every {
            mockKintuProcessor.processFile(
                capture(capturedConfig),
                capture(capturedKintuFile))
        } answers { nothing }

        val configuration = Configuration.unix().toBuilder()
            .setWorkingDirectory(workingDirectory)
            .build()
        fakeFs = Jimfs.newFileSystem(configuration)
    }

    @Test
    fun testHappyPath() {
        addTestFile("kintu.conf", "environment=myenv")
        givenTypicalTestFile()

        whenCommandExecuted()

        err.toString() should beEmpty()
        kintuProcessorShouldHaveBeenCalled()
        capturedKintuFile.captured.topic shouldBeEqual "testTopic"
        capturedConfig.captured.environment shouldBeEqual "myenv"
    }

    @Test
    fun testFileInParentDirectory() {
        givenTypicalTestFileInParentDirectory()

        whenCommandExecuted()

        err.toString() should beEmpty()
    }

    private fun kintuProcessorShouldHaveBeenCalled() {
        verify(exactly = 1) { mockKintuProcessor.processFile(any(), any()) }
    }

    private val typicalFileContent: String =
        """
           { "topic": "testTopic" }
        """

    private fun givenTypicalTestFile() {
        addTestFile(
            "kintu.kintu",
            typicalFileContent
        )
    }

    private fun givenTypicalTestFileInParentDirectory() {
        addTestFile(
            "/home/kintu.kintu",
            typicalFileContent
        )
    }

    private fun whenCommandExecuted(): Int {
        val app = KintuCommand(fakeFs, mockKintuProcessor)
        val cmd = CommandLine(app)

        cmd.err = PrintWriter(err)

        return cmd.execute("kintu")
    }

    private fun addTestFile(path: String, content: String) {
        val file = fakeFs.getPath(path)
        Files.createFile(file)
        Files.write(
            file,
            content.split("\r?\n|\r".toRegex()).toList()
        )
    }
}
