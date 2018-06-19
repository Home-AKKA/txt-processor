package com.ajax_systems.akka.web.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Cli {

    private static final Logger log = Logger.getLogger(Cli.class.getName());

    private String[] args;

    private Options options = new Options();

    private String app;

    private String filter;

    public Cli(String... args) {
        this.args = args;

        options.addOption("h", "help", false, "show help.");
        options.addOption("a", "app", true, "Here you can set parameter 'console' or 'web'.");
        options.addOption("l", "level", true, "Here you can set parameter...");
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getLevel() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void actions() {
        CommandLineParser parser = new BasicParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h")){
                help();
            }

            if (cmd.hasOption("a") || cmd.hasOption("l")) {
                log.log(Level.INFO, "Using argument -a=" + cmd.getOptionValue("a"));
                app = cmd.getOptionValue("a");
                log.log(Level.INFO, "Using argument -l=" + cmd.getOptionValue("l"));
                filter = cmd.getOptionValue("l");
            } else {
                log.log(Level.SEVERE, "Missing a option");
                help();
            }
        } catch (ParseException ex) {
            log.log(Level.SEVERE, "Failed to parse command line properties", ex);
            help();
        }
    }

    private void help() {
        HelpFormatter formater = new HelpFormatter();
        formater.printHelp("Main", options);
        System.exit(0);
    }
}
