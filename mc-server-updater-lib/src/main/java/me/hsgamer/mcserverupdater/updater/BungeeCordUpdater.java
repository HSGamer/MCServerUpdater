package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.JenkinsUpdater;

import java.util.regex.Pattern;

public class BungeeCordUpdater extends JenkinsUpdater {
    public BungeeCordUpdater() {
        super("https://ci.md-5.net/");
    }

    @Override
    public String getJob(String version) {
        return "BungeeCord";
    }

    @Override
    public Pattern getArtifactRegex(String version, String build) {
        return Pattern.compile(Pattern.quote("BungeeCord.jar"));
    }

    @Override
    public String getDefaultVersion() {
        return "1.17.1";
    }
}
