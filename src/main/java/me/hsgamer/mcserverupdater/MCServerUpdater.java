package me.hsgamer.mcserverupdater;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.mcserverupdater.api.Checksum;
import me.hsgamer.mcserverupdater.api.LatestBuild;
import me.hsgamer.mcserverupdater.api.Updater;
import me.hsgamer.mcserverupdater.updater.PaperUpdater;
import me.hsgamer.mcserverupdater.updater.PurpurUpdater;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.*;

public final class MCServerUpdater {
    public static final Logger LOGGER = Logger.getLogger("MCServerUpdater");
    private static final Map<String, Supplier<Updater>> UPDATERS = new CaseInsensitiveStringHashMap<>();

    static {
        UPDATERS.put("paper", PaperUpdater::new);
        UPDATERS.put("purpur", PurpurUpdater::new);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord logRecord) {
                return "[" + logRecord.getLevel() + "] " + logRecord.getMessage() + "\n";
            }
        });
        LOGGER.addHandler(handler);
        LOGGER.setUseParentHandlers(false);
    }

    public static void main(String[] args) throws Exception {
        OptionParser parser = new OptionParser();
        OptionSpec<Void> help = parser.accepts("help").forHelp();
        OptionSpec<String> project = parser.accepts("project", "The Minecraft project to download").withRequiredArg().ofType(String.class).defaultsTo("paper");
        OptionSpec<String> version = parser.accepts("version", "The Minecraft version").withRequiredArg().ofType(String.class).defaultsTo("1.17.1");
        OptionSpec<String> build = parser.accepts("build", "The build of the project to download").withRequiredArg().ofType(String.class).defaultsTo("latest");
        OptionSpec<String> output = parser.accepts("output", "The output file path").withRequiredArg().ofType(String.class).defaultsTo("server.jar");
        OptionSpec<Boolean> skipInternetCheck = parser.accepts("skip-internet-check", "Skip the internet check").withOptionalArg().ofType(Boolean.class).defaultsTo(false);
        OptionSet options = parser.parse(args);
        if (options.has(help)) {
            StringWriter writer = new StringWriter();
            parser.printHelpOn(writer);
            BufferedReader reader = new BufferedReader(new StringReader(writer.toString()));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                LOGGER.info(line);
            }
            System.exit(0);
            return;
        }
        String projectName = options.valueOf(project);
        String versionName = options.valueOf(version);
        String buildName = options.valueOf(build);
        String outputName = options.valueOf(output);

        if (!options.valueOf(skipInternetCheck) && !Utils.checkInternetConnection()) {
            LOGGER.severe("No internet connection");
            System.exit(1);
            return;
        }

        Optional<Updater> optionalUpdater = Optional.ofNullable(UPDATERS.get(projectName)).map(Supplier::get);
        if (optionalUpdater.isEmpty()) {
            LOGGER.severe("Project not found");
            System.exit(1);
            return;
        }
        Updater updater = optionalUpdater.get();

        if (buildName.equalsIgnoreCase("latest") && updater instanceof LatestBuild) {
            buildName = ((LatestBuild) updater).getLatestBuild(versionName);
            if (buildName == null) {
                LOGGER.severe("No build found");
                System.exit(1);
                return;
            }
        }

        File outputFile = new File(outputName);
        if (outputFile.exists()) {
            if (updater instanceof Checksum) {
                LOGGER.info("Checking checksum...");
                Checksum checksum = (Checksum) updater;
                if (checksum.checksum(outputFile, versionName, buildName)) {
                    LOGGER.info("Checksum match. File already up to date.");
                    System.exit(0);
                    return;
                }
            }
        } else {
            File parent = outputFile.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                LOGGER.severe("Could not create parent directory");
                System.exit(1);
                return;
            }
            if (!outputFile.createNewFile()) {
                LOGGER.severe("Could not create output file");
                System.exit(1);
                return;
            }
        }

        LOGGER.log(Level.INFO, "Downloading {0} {1} {2}", new Object[]{projectName, versionName, buildName});
        if (updater.update(outputFile, versionName, buildName)) {
            LOGGER.info("Downloaded to " + outputFile.getAbsolutePath());
            System.exit(0);
        } else {
            LOGGER.severe("Failed to download");
            System.exit(1);
        }
    }
}
