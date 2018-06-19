package com.ajax_systems.akka.service.impl;

import akka.Done;
import akka.actor.ActorSystem;
import akka.stream.*;
import akka.stream.javadsl.*;
import akka.util.ByteString;
import com.ajax_systems.akka.model.SomeTxtLevel;
import com.ajax_systems.akka.service.ActorModelHelper;
import com.ajax_systems.akka.service.dto.SomeTxtLevelDTO;
import com.ajax_systems.akka.service.mapper.SomeTxtLevelMapper;
import com.ajax_systems.akka.service.mapper.SomeTxtMapper;
import com.ajax_systems.akka.service.mapper.impl.SomeTxtLevelMapperImpl;
import com.ajax_systems.akka.dao.SomeTxtRepository;
import com.ajax_systems.akka.model.SomeTxt;
import com.ajax_systems.akka.service.SomeTxtService;
import com.ajax_systems.akka.service.dto.SomeTxtDTO;
import com.ajax_systems.akka.service.mapper.impl.SomeTxtMapperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import java.util.Collection;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;


public class SomeTxtServiceImpl implements SomeTxtService {

    private static final Logger log = LoggerFactory.getLogger(SomeTxtServiceImpl.class);

    private ActorSystem system = ActorSystem.create("system-txt");

    private SomeTxtRepository someTxtRepository;

    private SomeTxtMapper someTxtMapper;

    private SomeTxtLevelMapper someTxtLevelMapper;

    private ActorModelHelper actorModelHelper;

    @Inject
    public SomeTxtServiceImpl(SomeTxtRepository someTxtRepository,
                              SomeTxtMapperImpl someTxtMapper,
                              SomeTxtLevelMapperImpl someTxtLevelMapper,
                              ActorModelHelper actorModelHelper) {
        this.someTxtRepository = someTxtRepository;
        this.someTxtMapper = someTxtMapper;
        this.someTxtLevelMapper = someTxtLevelMapper;
        this.actorModelHelper = actorModelHelper.init(someTxtRepository);
    }

    @Override
    public void doUpload(Source<ByteString, Object> data) {
        log.debug("upload ByteString data");
        Graph<ClosedShape, CompletionStage<Done>> doUploadGraph = actorModelHelper.getDoUploadGraph(data);
        RunnableGraph.fromGraph(doUploadGraph).run(ActorMaterializer.create(system));
    }

    @Override
    public SomeTxtDTO findSomeTxtOne(String level) {
        log.debug("get SomeTxt by level={}", level);
        SomeTxt result = someTxtRepository.findOne(level);

        return someTxtMapper.toDto(result);
    }

    @Override
    public Collection<SomeTxtDTO> findSomeTxt() {
        log.debug("get list SomeTxt");
        Collection<SomeTxt> result = someTxtRepository.find();

        return result.stream()
                .map(someTxtMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<SomeTxtLevelDTO> findSomeTxtLevelByLevel(String level) {
        log.debug("get SomeTxtLevel by level={}", level);
        Graph<ClosedShape, CompletionStage<Done>> fromCacheGraph = actorModelHelper.getDoDownloadGraph(level);
        RunnableGraph.fromGraph(fromCacheGraph).run(ActorMaterializer.create(system));

        Collection<SomeTxtLevel> result = actorModelHelper.getSomeTxtLevelCache(level);

        return result.stream()
                .map(someTxtLevelMapper::toDto)
                .collect(Collectors.toList());
    }
}
