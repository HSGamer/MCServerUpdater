package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.UpdateBuilder;
import me.hsgamer.mcserverupdater.api.Updater;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class SpigotUpdater implements Updater {
    private final UpdateBuilder updateBuilder;

    public SpigotUpdater(UpdateBuilder updateBuilder) {
        this.updateBuilder = updateBuilder;
    }

    private File downloadBuildTools() {
        File file = new File(updateBuilder.workingDirectory(), "BuildTools.jar");
        try {
            String buildToolsURL = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar";
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(buildToolsURL));
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
        File outputDir = new File(updateBuilder.workingDirectory(), "output");
        if (!runBuildTools(buildTools, outputDir, version)) {
            return false;
        }
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

    private boolean runBuildTools(File buildTools, File outputDir, String version) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "java",
                "-jar",
                buildTools.getAbsolutePath(),
                "--rev", version,
                "--output-dir", outputDir.getAbsolutePath(),
                "--compile-if-changed"
        );
        processBuilder.directory(updateBuilder.workingDirectory());
        processBuilder.redirectErrorStream(true);
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        process.waitFor();
        return process.exitValue() == 0;
    }

    @Override
    public String getDefaultVersion() {
        return "latest";
    }

    @Override
    public UpdateBuilder getUpdateBuilder() {
        return updateBuilder;
    }
}
