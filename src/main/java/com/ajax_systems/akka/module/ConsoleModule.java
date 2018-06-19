package com.ajax_systems.akka.module;

import com.ajax_systems.akka.console.SomeTxtConsole;
import com.google.inject.AbstractModule;


public class ConsoleModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SomeTxtConsole.class);
    }
}
