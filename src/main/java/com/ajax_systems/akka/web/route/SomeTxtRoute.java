package com.ajax_systems.akka.web.route;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import com.ajax_systems.akka.service.SomeTxtService;
import com.ajax_systems.akka.service.dto.SomeTxtLevelDTO;
import com.ajax_systems.akka.service.impl.SomeTxtServiceImpl;
import com.ajax_systems.akka.service.dto.SomeTxtDTO;
import com.ajax_systems.akka.web.util.SomeTxtUtil;
import javax.inject.Inject;
import java.util.Collection;
import java.util.function.Function;


public class SomeTxtRoute extends HttpApp {

    private SomeTxtService someTxtService;

    @Inject
    public SomeTxtRoute(SomeTxtServiceImpl someTxtService) {
        this.someTxtService = someTxtService;
    }

    public Route findSomeTxt() {
        Collection<SomeTxtDTO> someTxts = someTxtService.findSomeTxt();
        return complete(StatusCodes.OK, someTxts, Jackson.marshaller());
    }

    public Function<String, Route> findSomeTxtOne = id -> {
        SomeTxtDTO someTxt = someTxtService.findSomeTxtOne(id);
        return (someTxt == null) ? reject() : complete(StatusCodes.OK, someTxt, Jackson.marshaller());
    };

    public Function<String, Route> findSomeTxtLevelByLevel = level -> {
        Collection<SomeTxtLevelDTO> someTxtLevels = someTxtService.findSomeTxtLevelByLevel(level);
        return (someTxtLevels == null) ? reject() : complete(StatusCodes.OK, someTxtLevels, Jackson.marshaller());
    };

    @Override
    public Route routes() {
        return route(
                path("", () ->
                        getFromResource("web/index.html")
                ),

                pathPrefix("txt-levels", () ->
                        pathEndOrSingleSlash(() -> route(
//                              log.debug("get http://localhost:8080/txt-levels");
                                get(() -> findSomeTxt())
                        ))),

//              log.debug("get http://localhost:8080/txt-levels/{}", level);
                pathPrefix("txt-levels", () ->
                        path(level -> route(
                                get(() -> findSomeTxtOne.apply(level))
                        ))),

                pathPrefix("txt", () ->
                        pathEndOrSingleSlash(() -> route(
//                              log.debug("post http://localhost:8080/txt");
                                post(() ->
                                        entity(Unmarshaller.entityToMultipartFormData(), data -> {
                                            someTxtService.doUpload(data.toEntity().getDataBytes());
                                            return findSomeTxt();
                                        }))
                        ))),

//              log.debug("get http://localhost:8080/txt/{}", fileName);
                pathPrefix("txt", () ->
                        path(fileName -> route(
                                getFromResource("web/" + fileName + "." + SomeTxtUtil.OUT_EXT)
                        ))),

//              log.debug("get http://localhost:8080/txt-logs-by-level/{}", level);
                pathPrefix("txt-logs-by-level", () ->
                        path(level -> route(
                                get(() -> findSomeTxtLevelByLevel.apply(level))
                        )))
        );
    }
}
