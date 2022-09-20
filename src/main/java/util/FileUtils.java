package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {

    public static final String webappPath = "./webapp";

    public static boolean isStaticFile(String path) {
        File file = new File(webappPath + path);
        return file.exists() && file.isFile();
    }

    public static byte[] getBytesFromStaticFilePath(String path) throws IOException {
        return Files.readAllBytes(new File(webappPath + path).toPath());
    }
}
