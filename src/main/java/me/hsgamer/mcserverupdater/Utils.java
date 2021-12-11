package me.hsgamer.mcserverupdater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;

import java.io.*;

import static me.hsgamer.mcserverupdater.MCServerUpdater.LOGGER;

public class Utils {
    private Utils() {
        // EMPTY
    }

    public static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static boolean checkInternetConnection() {
        try {
            WebUtils.openConnection("https://www.google.com", UserAgent.CHROME);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFailedToCreateFile(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            LOGGER.severe("Could not create parent directory");
            return true;
        }
        if (!file.createNewFile()) {
            LOGGER.severe("Could not create output file");
            return true;
        }
        return false;
    }

    public static String getString(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }
    }

    public static void writeString(File file, String string) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(string);
        }
    }

    public static File getFile(String path) throws IOException {
        File file = new File(path);
        createFile(file);
        return file;
    }

    public static File getFile(File parent, String path) throws IOException {
        File file = new File(parent, path);
        createFile(file);
        return file;
    }

    private static void createFile(File file) throws IOException {
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Can't create file " + file.getAbsolutePath());
        }
    }
}
