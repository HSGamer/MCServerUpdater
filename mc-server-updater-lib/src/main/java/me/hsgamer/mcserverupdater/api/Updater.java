package me.hsgamer.mcserverupdater.api;

import java.io.File;
import java.util.Optional;

public interface Updater {
    boolean update(File file) throws Exception;

    void debug(String message);

    default void debug(String format, Object... args) {
        debug(String.format(format, args));
    }

    default void debug(Throwable throwable) {
        debug(throwable.getClass().getName() + ": " + throwable.getMessage());
        for (StackTraceElement element : throwable.getStackTrace()) {
            debug("    " + element.toString());
        }
        Optional.ofNullable(throwable.getCause()).ifPresent(cause -> debug("Caused by: " + cause.getMessage(), cause));
    }

    default void debug(String message, Throwable throwable) {
        debug(message);
        debug(throwable);
    }
}
