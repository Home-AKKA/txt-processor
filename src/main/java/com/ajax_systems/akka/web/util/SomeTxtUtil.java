package com.ajax_systems.akka.web.util;

import java.util.regex.Pattern;


public class SomeTxtUtil {

    public static final String OUT_PATH = "src/main/resources/web";

    public static final String OUT_EXT = "txt";

    public static final Pattern PATTERN = Pattern.compile(".*\\[(DEBUG|INFO|WARN|ERROR)\\].*");

}
