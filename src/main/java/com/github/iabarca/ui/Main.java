
package com.github.iabarca.ui;

import static java.util.Arrays.asList;

import com.github.iabarca.util.StartLogger;
import com.github.iabarca.util.Utils;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger log = Logger.getLogger("stats");

    public static void main(String[] args) {
        new StartLogger("stats").toConsole(Level.FINE).toFile(Level.FINE);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, final Throwable e) {
                log.log(Level.SEVERE, "Unhandled exception", e);
            }
        });
        try {
            log.info("StatsTools. Arguments: " + asList(args));
            Options options = new Options();
            OptionSet arguments = options.parse(args);
            if (arguments.has("s") || arguments.has("u")) {
                new Presenter(options).start();
            } else {
                options.getParser().printHelpOn(System.out);
            }
        } catch (OptionException | IOException e) {
            e.printStackTrace();
        }
    }
}
