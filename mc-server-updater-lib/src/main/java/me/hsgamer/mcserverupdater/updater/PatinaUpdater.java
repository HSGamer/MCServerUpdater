package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.GithubBranchUpdater;

import java.util.regex.Pattern;

public class PatinaUpdater extends GithubBranchUpdater {
    public PatinaUpdater() {
        super("PatinaMC/Patina");
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
}
