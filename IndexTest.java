import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class IndexTest {

    Index index;

    @BeforeEach
    public void setup() throws IOException {
        Files.write(Paths.get("testBlob.txt"), "Hello World".getBytes());
        Files.write(Paths.get("testBlob2.txt"), "Goodbye World".getBytes());

        deleteDirectory(Paths.get("objects"));
        Files.deleteIfExists(Paths.get("index"));
        index = new Index();
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get("testBlob.txt"));
        Files.deleteIfExists(Paths.get("testBlob2.txt"));
        Files.deleteIfExists(Paths.get("index"));
        deleteDirectory(Paths.get("objects"));
    }

    @Test
    void testInit() {
        index.init();

        // tests if init files are made

        assertTrue(new File("index").exists());
        assertTrue(new File("objects").exists());

    }

    @Test
    void testAdd() throws IOException {
        testInit();

        index.add("testBlob.txt");
        index.add("testBlob2.txt");

        // tests if it adds a file
        assertTrue(new File("objects").listFiles().length > 0);

        String indexText = Tree.read("index");

        String[] lineSplit = indexText.split("\\r?\\n");
        String[] str = lineSplit[0].split("\\s+");
        String[] str2 = lineSplit[1].split("\\s+");

        // test if the file is added to index
        assertEquals("testBlob.txt", str[0]);
        assertEquals("testBlob2.txt", str2[0]);
    }

    @Test
    void testDelete() throws IOException {
        testAdd();

        index.remove("testBlob.txt");

        // ig its okay if you dont delete the object
        // assertTrue(new File("objects").listFiles().length == 0);

        String indexText = Blob.read(new File("index"));

        String[] str = indexText.split("\\s+");

        // tests that only testBlob2 is in the index
        assertEquals("testBlob2.txt", str[0]);
    }

    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
