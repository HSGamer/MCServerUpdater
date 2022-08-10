package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.JenkinsUpdater;

import java.util.regex.Pattern;

public class PufferfishUpdater extends JenkinsUpdater {
    public PufferfishUpdater() {
        super("https://ci.pufferfish.host/");
    }

    @Override
    public String[] getJob(String version) {
        Version v = Version.getVersion(version);
        return new String[]{v == null ? "INVALID" : v.job};
    }

    @Override
    public Pattern getArtifactRegex(String version, String build) {
        Version v = Version.getVersion(version);
        return v == null ? Pattern.compile("INVALID") : v.artifactRegex;
    }

    @Override
    public String getDefaultVersion() {
        return "1.17.1";
    }

    private enum Version {
        PURPUR_1_17(Pattern.compile("1\\.17(\\.\\d+)?-purpur"), "Pufferfish-Purpur-1.17", Pattern.compile(Pattern.quote("Pufferfish-1.17.1-R0.1-SNAPSHOT.jar"))),
        PURPUR_1_18(Pattern.compile("1\\.18(\\.\\d+)?-purpur"), "Pufferfish-Purpur-1.18", Pattern.compile("pufferfish-paperclip-1\\.18(\\.\\d+)?-R0\\.1-SNAPSHOT-reobf\\.jar")),
        NORMAL_1_17(Pattern.compile("1\\.17(\\.\\d+)?"), "Pufferfish-1.17", Pattern.compile(Pattern.quote("Pufferfish-1.17.1-R0.1-SNAPSHOT.jar"))),
        NORMAL_1_18(Pattern.compile("1\\.18(\\.\\d+)?"), "Pufferfish-1.18", Pattern.compile("pufferfish-paperclip-1\\.18(\\.\\d+)?-R0\\.1-SNAPSHOT-reobf\\.jar")),
        NORMAL_1_19(Pattern.compile("1\\.19(\\.\\d+)?"), "Pufferfish-1.19", Pattern.compile("pufferfish-paperclip-1\\.19(\\.\\d+)?-R0\\.1-SNAPSHOT-reobf\\.jar"));
        public final Pattern versionRegex;
        public final String job;
        public final Pattern artifactRegex;

        Version(Pattern versionRegex, String job, Pattern artifactRegex) {
            this.versionRegex = versionRegex;
            this.job = job;
            this.artifactRegex = artifactRegex;
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
