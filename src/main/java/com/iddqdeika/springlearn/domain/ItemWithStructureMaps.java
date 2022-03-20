package com.iddqdeika.springlearn.domain;

import java.util.HashMap;
import java.util.Map;

public class ItemWithStructureMaps {

    public String id;
    public CaptureEvent revision;
    public Map<Long, CaptureEvent> maps = new HashMap<>();

    public ItemWithStructureMaps withId(String id){
        this.id = id;
        return this;
    }

    public ItemWithStructureMaps withRevision(CaptureEvent revision){
        this.revision = revision;
        return this;
    }

    public ItemWithStructureMaps addMap(CaptureEvent map){
        maps.put(1L, map);
        return this;
    }

    @Override
    public String toString() {
        return "ItemWithStructureMaps{" +
                "id='" + id + '\'' +
                ", revision=" + revision +
                ", maps=" + maps +
                '}';
    }
}
