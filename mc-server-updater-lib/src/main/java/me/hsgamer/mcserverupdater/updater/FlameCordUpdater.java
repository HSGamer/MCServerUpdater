package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.UpdateBuilder;
import me.hsgamer.mcserverupdater.api.JenkinsUpdater;

import java.util.regex.Pattern;

public class FlameCordUpdater extends JenkinsUpdater {
    private final UpdateBuilder updateBuilder;

    public FlameCordUpdater(UpdateBuilder updateBuilder) {
        super("https://ci.2lstudios.dev/");
        this.updateBuilder = updateBuilder;
    }

    @Override
    public String[] getJob(String version) {
        return new String[]{"FlameCord"};
    }

    @Override
    public Pattern getArtifactRegex(String version, String build) {
        return Pattern.compile(Pattern.quote("FlameCord.jar"));
    }

    @Override
    public String getDefaultVersion() {
        return "1.8.8";
    }

    @Override
    public UpdateBuilder getUpdateBuilder() {
        return updateBuilder;
    }
}
