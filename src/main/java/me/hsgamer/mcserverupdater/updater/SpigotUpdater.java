package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.api.Updater;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class SpigotUpdater implements Updater {

    private File downloadBuildTools() {
        File file = new File("BuildTools.jar");
        try {
            String buildToolsURL = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar";
            URLConnection connection = WebUtils.openConnection(buildToolsURL, UserAgent.CHROME);
            InputStream inputStream = connection.getInputStream();
            Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(File file, String version, String build) throws Exception {
        File buildTools = downloadBuildTools();
        if (buildTools == null) {
            return false;
        }
        File outputDir = new File("output");
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{buildTools.toURI().toURL()}, getClass().getClassLoader())) {
            Class<?> clazz = Class.forName("org.spigotmc.builder.Bootstrap", true, classLoader);
            Method method = clazz.getMethod("main", String[].class);
            method.invoke(null, (Object) new String[]{
                    "--rev", version,
                    "--output-dir", outputDir.getAbsolutePath(),
                    "--compile-if-changed"
            });

            for (File outputFile : Objects.requireNonNull(outputDir.listFiles())) {
                String name = outputFile.getName();
                if (name.startsWith("spigot-") && name.endsWith(".jar")) {
                    Files.copy(outputFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Files.delete(outputFile.toPath());
                    break;
                }
            }
            return true;
        }
    }

    @Override
    public String getDefaultVersion() {
        return "latest";
    }
}
