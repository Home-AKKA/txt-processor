package com.ajax_systems.akka.console;

import com.ajax_systems.akka.service.SomeTxtService;
import com.ajax_systems.akka.service.impl.SomeTxtServiceImpl;
import com.ajax_systems.akka.service.mapper.SomeTxtLevelMapper;
import com.ajax_systems.akka.service.mapper.impl.SomeTxtLevelMapperImpl;
import com.ajax_systems.akka.web.util.PrintUtil;

import javax.inject.Inject;


public class SomeTxtConsole {

    private SomeTxtService someTxtService;

    private SomeTxtLevelMapper someTxtLevelMapper;

    @Inject
    public SomeTxtConsole(SomeTxtServiceImpl someTxtService,
                          SomeTxtLevelMapperImpl someTxtLevelMapper) {
        this.someTxtService = someTxtService;
        this.someTxtLevelMapper = someTxtLevelMapper;
    }

    public void run(String... args) {
        if (args != null) {
            String level = args[0];
            someTxtService.findSomeTxtLevelByLevel(level)
                    .stream()
                    .forEach(PrintUtil::print);
        }
    }
}
