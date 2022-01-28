package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.GithubBranchUpdater;

public class PatinaUpdater extends GithubBranchUpdater {
    public PatinaUpdater() {
        super("PatinaMC/Patina");
    }

    @Override
    public String getBranch(String version) {
        return "releases/" + version;
    }

    @Override
    public String getFile(String version, String build) {
        if (version.equalsIgnoreCase("1.18.1")) {
            return "patina-bundler-1.18.1-R0.1-SNAPSHOT-reobf.jar";
        } else if (version.equalsIgnoreCase("1.16.5")) {
            return "1.16.5-paperclip.jar";
        } else {
            return "Patina-" + version + "-R0.1-SNAPSHOT.jar";
        }
    }

    @Override
    public String getDefaultVersion() {
        return "1.17.1";
    }
}
