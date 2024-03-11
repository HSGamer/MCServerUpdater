package me.hsgamer.mcserverupdater;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.hsgamer.hscore.logger.jul.JulLogger;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.*;

public final class MCServerUpdater {
    public static final Logger LOGGER = Logger.getLogger("MCServerUpdater");

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord logRecord) {
                StringBuilder builder = new StringBuilder();
                builder.append("[").append(logRecord.getLevel()).append("] ").append(logRecord.getMessage()).append("\n");
                if (logRecord.getThrown() != null) {
                    StringWriter writer = new StringWriter();
                    logRecord.getThrown().printStackTrace(new PrintWriter(writer));
                    builder.append(writer);
                }
                return builder.toString();
            }
        });
        LOGGER.addHandler(handler);
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
    }

    public static void main(String[] args) throws Exception {
        OptionParser parser = new OptionParser();
        OptionSpec<Void> help = parser.accepts("help", "Get the list of arguments").forHelp();
        OptionSpec<Void> projects = parser.accepts("projects", "Get the list of projects").forHelp();
        OptionSpec<String> project = parser.accepts("project", "The project to download").withOptionalArg().ofType(String.class).defaultsTo("paper");
        OptionSpec<String> version = parser.accepts("version", "The project version").withOptionalArg().ofType(String.class).defaultsTo("default");
        OptionSpec<String> output = parser.accepts("output", "The output file path").withOptionalArg().ofType(String.class).defaultsTo("server.jar");
        OptionSpec<String> checksumFile = parser.accepts("checksum", "The checksum file path").withOptionalArg().ofType(String.class).defaultsTo("checksum.txt");
        OptionSpec<String> workingDirectory = parser.accepts("working-directory", "The working directory").withOptionalArg().ofType(String.class).defaultsTo(".");
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
        if (options.has(projects)) {
            for (String key : UpdateBuilder.getUpdaterNames()) {
                LOGGER.info(key);
            }
            System.exit(0);
            return;
        }
        String projectName = options.valueOf(project);
        String versionName = options.valueOf(version);
        String outputName = options.valueOf(output);
        String checksumFileName = options.valueOf(checksumFile);
        String workingDirectoryName = options.valueOf(workingDirectory);

        UpdateBuilder builder = UpdateBuilder.updateProject(projectName)
                .version(versionName)
                .workingDirectory(workingDirectoryName)
                .outputFile(outputName)
                .checksumFile(checksumFileName)
                .logger(new JulLogger(LOGGER));

        try {
            LOGGER.info("Start updating...");
            UpdateStatus status = builder.execute();
            if (status.isSuccessStatus()) {
                LOGGER.info(status.getMessage());
                System.exit(0);
            } else {
                LOGGER.log(Level.SEVERE, "Failed to update", status.getThrowable());
                System.exit(1);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred", e);
            System.exit(1);
        }
    }
}
