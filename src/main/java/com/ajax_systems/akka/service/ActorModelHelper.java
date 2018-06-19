package com.ajax_systems.akka.service;

import akka.Done;
import akka.NotUsed;
import akka.stream.*;
import akka.stream.javadsl.*;
import akka.util.ByteString;
import com.ajax_systems.akka.dao.SomeTxtLevelRepository;
import com.ajax_systems.akka.dao.SomeTxtRepository;
import com.ajax_systems.akka.model.SomeTxt;
import com.ajax_systems.akka.model.SomeTxtLevel;
import com.ajax_systems.akka.service.dto.SomeTxtLevelDTO;
import com.ajax_systems.akka.service.mapper.SomeTxtLevelMapper;
import com.ajax_systems.akka.service.mapper.impl.SomeTxtLevelMapperImpl;
import com.ajax_systems.akka.web.util.PrintUtil;
import com.ajax_systems.akka.web.util.SomeTxtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;


public class ActorModelHelper {

    private static final Logger log = LoggerFactory.getLogger(ActorModelHelper.class);

    private static final int MAXIMUM_FRAME_LENGTH = 256;

    private SomeTxtRepository someTxtRepository;

    private SomeTxtLevelRepository someTxtLevelRepository;

    private SomeTxtLevelMapper someTxtLevelMapper;

    @Inject
    public ActorModelHelper(SomeTxtLevelMapperImpl someTxtLevelMapper) {
        this.someTxtLevelRepository = new SomeTxtLevelRepository();
        this.someTxtLevelMapper = someTxtLevelMapper;
    }

    public ActorModelHelper init(SomeTxtRepository someTxtRepository) {
        this.someTxtRepository = someTxtRepository;
        return this;
    }

    /**
     *
     */
    private Flow<ByteString, String, NotUsed> splitFlow = Framing
            .delimiter(ByteString.fromString("\n"), MAXIMUM_FRAME_LENGTH, FramingTruncation.ALLOW)
            .map(byteString -> byteString.decodeString(ByteString.UTF_8()));

    /**
     *
     */
    private Sink<SomeTxtLevel, CompletionStage<Done>> logProcess = Sink.foreach(someTxtLevel -> log.info("{}", someTxtLevelMapper.toDto(someTxtLevel)));

    private Sink<SomeTxtLevel, CompletionStage<Done>> consoleProcess = Sink.foreach(someTxtLevel -> PrintUtil.print(someTxtLevelMapper.toDto(someTxtLevel)));

    private Sink<SomeTxtLevel, CompletionStage<Done>> repositoryProcess = Sink.foreach(someTxtLevel -> {
        someTxtLevelRepository.save(someTxtLevel);

        String level = someTxtLevel.getLevel();
        Collection<SomeTxtLevel> someTxtLevels = someTxtLevelRepository.findByLevel(level);
        final int total = someTxtLevels.size() + 1;
        SomeTxt someTxt = new SomeTxt()
                .level(level)
                .total(total);
        someTxtRepository.save(someTxt);
    });

    private Sink<SomeTxtLevel, CompletionStage<Done>> fileProcess = Sink.foreach(someTxtLevel -> {
        String outPath = SomeTxtUtil.OUT_PATH + "/" + someTxtLevel.getLevel() + "." + SomeTxtUtil.OUT_EXT;
        try (FileWriter prWr = new FileWriter(new File(outPath), true)) {
            prWr.write(someTxtLevel.getDescription() + '\n');
        }
    });


    private String matchLevel(String description) {
        Matcher matcher = SomeTxtUtil.PATTERN.matcher(description);
        if (matcher.find())
            return matcher.group(1);
        else
            return "UNKNOWN";
    }

    private Graph<ClosedShape, CompletionStage<Done>> doUploadGraph(Source<SomeTxtLevel, Object> dataSource) {
        return GraphDSL.create(logProcess, (builder, sink) -> {
            UniformFanOutShape<SomeTxtLevel, SomeTxtLevel> broadcast = builder.add(Broadcast.create(3));
            SourceShape<SomeTxtLevel> source = builder.add(dataSource);
            SinkShape<SomeTxtLevel> memoSink = builder.add(repositoryProcess);
            SinkShape<SomeTxtLevel> fileSink = builder.add(fileProcess);

            builder.from(source).viaFanOut(broadcast).to(sink)
                    .from(broadcast).to(memoSink)
                    .from(broadcast).to(fileSink);

            return ClosedShape.getInstance();
        });
    }

    private Graph<ClosedShape, CompletionStage<Done>> doDownloadGraph(Source<SomeTxtLevel, CompletionStage<IOResult>> dataSource) {
        return GraphDSL.create(logProcess, (builder, sink) -> {
            UniformFanOutShape<SomeTxtLevel, SomeTxtLevel> broadcast = builder.add(Broadcast.create(3));
            SourceShape<SomeTxtLevel> source = builder.add(dataSource);
            SinkShape<SomeTxtLevel> memoSink = builder.add(repositoryProcess);
            SinkShape<SomeTxtLevel> consoleSink = builder.add(consoleProcess);

            builder.from(source).viaFanOut(broadcast).to(sink)
                    .from(broadcast).to(memoSink)
                    .from(broadcast).to(consoleSink);

            return ClosedShape.getInstance();
        });
    }

    public Graph<ClosedShape, CompletionStage<Done>> getDoUploadGraph(Source<ByteString, Object> data) {
        Source<SomeTxtLevel, Object> dataSource = data
                .via(splitFlow)
                .map(description -> {
                    String level = matchLevel(description);
                    int size = description.length();
                    return new SomeTxtLevel()
                            .level(level)
                            .description(description)
                            .size(size);
                });
        return doUploadGraph(dataSource);
    }

    public Graph<ClosedShape, CompletionStage<Done>> getDoDownloadGraph(final String level) {
        String outPath = SomeTxtUtil.OUT_PATH + "/" + level + "." + SomeTxtUtil.OUT_EXT;

        Source<SomeTxtLevel, CompletionStage<IOResult>> dataSource = FileIO.fromFile(new File(outPath))
                .via(splitFlow)
                .map(description -> {
                    int size = description.length();
                    return new SomeTxtLevel()
                            .level(level)
                            .description(description)
                            .size(size);
                });
        return doDownloadGraph(dataSource);
    }

    public Collection<SomeTxtLevel> getSomeTxtLevelCache(String level) {
        return someTxtLevelRepository.findByLevel(level);
    }
}
