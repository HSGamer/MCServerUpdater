package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.UpdateBuilder;
import me.hsgamer.mcserverupdater.api.GithubBranchUpdater;

import java.util.regex.Pattern;

public class PatinaUpdater extends GithubBranchUpdater {
    private final UpdateBuilder updateBuilder;

    public PatinaUpdater(UpdateBuilder updateBuilder) {
        super("PatinaMC/Patina");
        this.updateBuilder = updateBuilder;
    }

    @Override
    public String getBranch(String version) {
        return "releases/" + version;
    }

    @Override
    public Pattern getFilePattern(String version, String build) {
        return Pattern.compile(".*\\.jar");
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
