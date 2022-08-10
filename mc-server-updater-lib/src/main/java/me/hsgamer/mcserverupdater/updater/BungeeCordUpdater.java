package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.UpdateBuilder;
import me.hsgamer.mcserverupdater.api.JenkinsUpdater;

import java.util.regex.Pattern;

public class BungeeCordUpdater extends JenkinsUpdater {
    private final UpdateBuilder updateBuilder;

    public BungeeCordUpdater(UpdateBuilder updateBuilder) {
        super("https://ci.md-5.net/");
        this.updateBuilder = updateBuilder;
    }

    @Override
    public String[] getJob(String version) {
        return new String[]{"BungeeCord"};
    }

    @Override
    public Pattern getArtifactRegex(String version, String build) {
        return Pattern.compile(Pattern.quote("BungeeCord.jar"));
    }

    @Override
    public String getDefaultVersion() {
        return "1.17.1";
    }

    @Override
    public UpdateBuilder getUpdateBuilder() {
        return updateBuilder;
    }
}
