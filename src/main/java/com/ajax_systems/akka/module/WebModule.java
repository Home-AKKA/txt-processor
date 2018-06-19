package com.ajax_systems.akka.module;

import com.ajax_systems.akka.web.route.SomeTxtRoute;
import com.google.inject.AbstractModule;


public class WebModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SomeTxtRoute.class);
    }
}
