package kintu

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.kotest.matchers.equals.shouldBeEqual
import org.junit.jupiter.api.Test
import kotlin.io.path.exists

class KintuInitCommandTest {
    @Test
    fun testSomething() {
        val workingDirectory = "/home/work"
        val configuration = Configuration.unix().toBuilder()
            .setWorkingDirectory(workingDirectory)
            .build()
        val fakeFs = Jimfs.newFileSystem(configuration)

        val sut = KintuInitCommand()
        sut.fileSystem = fakeFs

        sut.run()

        fakeFs.getPath("kintu.conf").exists() shouldBeEqual true
    }
}