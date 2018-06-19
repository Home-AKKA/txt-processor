package com.ajax_systems.akka.dao;

import com.ajax_systems.akka.model.SomeTxtLevel;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class SomeTxtLevelRepository {

    private final ConcurrentHashMap<String, SomeTxtLevel> someTxtLevels = new ConcurrentHashMap<>();

    {
        String id = "";

        someTxtLevels.put(id,
                new SomeTxtLevel()
                        .id(id)
                        .level("UNKNOWN")
                        .description(id)
                        .size(0));
    }

    public Collection<SomeTxtLevel> find() {
        return someTxtLevels.values();
    }

    public Collection<SomeTxtLevel> findByLevel(String level) {
        return someTxtLevels.values().stream()
                .filter(someTxtLevel -> someTxtLevel.getLevel().equals(level))
                .collect(Collectors.toList());
    }

    public SomeTxtLevel findOne(String id) {
        return someTxtLevels.get(id);
    }

    public SomeTxtLevel save(SomeTxtLevel someTxtLevel) {
        String id = someTxtLevel.getDescription();

        SomeTxtLevel newSomeTxtLevelWith = new SomeTxtLevel()
                .id(id)
                .level(someTxtLevel.getLevel())
                .description(someTxtLevel.getDescription())
                .size(someTxtLevel.getSize());
        someTxtLevels.put(id, newSomeTxtLevelWith);
        return newSomeTxtLevelWith;
    }

    public void update(String id, SomeTxtLevel someTxtLevel) {
        someTxtLevels.put(id, someTxtLevel);
    }

    public void delete(String id) {
        someTxtLevels.remove(id);
    }
}
