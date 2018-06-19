package com.ajax_systems.akka.service;

import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.ajax_systems.akka.service.dto.SomeTxtLevelDTO;
import com.ajax_systems.akka.service.dto.SomeTxtDTO;

import java.util.Collection;


public interface SomeTxtService {

    /**
     *
     * @param data
     * @return
     */
    void doUpload(Source<ByteString, Object> data);

    /**
     *
     * @return
     */
    SomeTxtDTO findSomeTxtOne(String id);

    Collection<SomeTxtDTO> findSomeTxt();

    Collection<SomeTxtLevelDTO> findSomeTxtLevelByLevel(String level);
}
