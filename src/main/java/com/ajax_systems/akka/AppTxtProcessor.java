package com.ajax_systems.akka;

import com.ajax_systems.akka.console.SomeTxtConsole;
import com.ajax_systems.akka.module.ConsoleModule;
import com.ajax_systems.akka.module.WebModule;
import com.ajax_systems.akka.web.util.Cli;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ajax_systems.akka.web.route.SomeTxtRoute;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.concurrent.ExecutionException;


public class AppTxtProcessor {

    private static final Config config = ConfigFactory.load();

    public static void main(String... args) throws InterruptedException, ExecutionException {

        Cli cli = new Cli(args);
        cli.actions();

        switch (cli.getApp()) {
            case "console" : {
                System.out.println("You starting the application as CONSOLE APP!");
                Injector injector = Guice.createInjector(new ConsoleModule());
                injector.getInstance(SomeTxtConsole.class)
                        .run(cli.getLevel());
                break;
            }
            case "web" : {
                System.out.println("You starting the application as WEB APP!");
                Injector injector = Guice.createInjector(new WebModule());
                injector.getInstance(SomeTxtRoute.class)
                        .startServer(config.getString("csvRoute.host"), config.getInt("csvRoute.port"));
                break;
            }
            default:
                System.out.println("Here you can set parameter 'console' or 'web'?");
                break;
        }
    }
}
