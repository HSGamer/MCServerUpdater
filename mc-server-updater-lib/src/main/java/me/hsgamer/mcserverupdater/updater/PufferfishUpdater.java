package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.JenkinsUpdater;
import me.hsgamer.mcserverupdater.util.VersionQuery;

import java.util.regex.Pattern;

public class PufferfishUpdater extends JenkinsUpdater {
    public PufferfishUpdater(VersionQuery versionQuery) {
        super(versionQuery, "https://ci.pufferfish.host/");
    }

    @Override
    public String[] getJob() {
        Version v = Version.getVersion(version);
        return new String[]{v == null ? "INVALID" : v.job};
    }

    @Override
    public Pattern getArtifactRegex() {
        Version v = Version.getVersion(version);
        return v == null ? Pattern.compile("INVALID") : v.artifactRegex;
    }

    @Override
    public String getDefaultVersion() {
        return "1.17.1";
    }

    private enum Version {
        PURPUR_1_17(Pattern.compile("1\\.17(\\.\\d+)?-purpur"), "Pufferfish-Purpur-1.17"),
        PURPUR_1_18(Pattern.compile("1\\.18(\\.\\d+)?-purpur"), "Pufferfish-Purpur-1.18"),
        NORMAL_1_17(Pattern.compile("1\\.17(\\.\\d+)?"), "Pufferfish-1.17"),
        NORMAL_1_18(Pattern.compile("1\\.18(\\.\\d+)?"), "Pufferfish-1.18"),
        NORMAL_1_19(Pattern.compile("1\\.19(\\.\\d+)?"), "Pufferfish-1.19"),
        PLUS_1_18(Pattern.compile("1\\.18(\\.\\d+)?-plus"), "PufferfishPlus-1.18"),
        PLUS_1_19(Pattern.compile("1\\.19(\\.\\d+)?-plus"), "PufferfishPlus-1.19"),
        PLUS_PURPUR_1_18(Pattern.compile("1\\.18(\\.\\d+)?-plus-purpur"), "PufferfishPlus-1.18-Purpur"),
        PLUS_PURPUR_1_19(Pattern.compile("1\\.19(\\.\\d+)?-plus-purpur"), "PufferfishPlus-1.19-Purpur"),
        ;
        public final Pattern versionRegex;
        public final String job;
        public final Pattern artifactRegex;

        Version(Pattern versionRegex, String job, Pattern artifactRegex) {
            this.versionRegex = versionRegex;
            this.job = job;
            this.artifactRegex = artifactRegex;
        }

        Version(Pattern versionRegex, String job) {
            this(versionRegex, job, Pattern.compile(".*\\.jar"));
        }

        public static Version getVersion(String version) {
            for (Version v : values()) {
                if (v.versionRegex.matcher(version).matches()) {
                    return v;
                }
            }
            return null;
        }
    }
}
