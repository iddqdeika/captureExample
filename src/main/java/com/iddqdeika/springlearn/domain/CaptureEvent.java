package com.iddqdeika.springlearn.domain;

// example:
// {
//     "table":"[HPM_MASTER].dbo.ArticleRevision",
//     "operation":7,
//     "lsn":"8b203f4b-cc4b-4178-9a47-6c1f9a908e43",
//     "values":{
//         "ID":"1034",
//         "Identifier":"100000000051",
//         "DeletionUserID":""
//     }
// }

import java.util.HashMap;
import java.util.Map;

public class CaptureEvent {

    String table;
    Integer operation;
    Map<String, String> values = new HashMap<>();

    public CaptureEvent(){}

    public CaptureEvent(String table, Integer operation){
        this.table = table;
        this.operation = operation;
    }

    public CaptureEvent(String table, Integer operation, String key, String value){
        this.table = table;
        this.operation = operation;
        this.values.put(key, value);
    }

    public CaptureEvent(String table, Integer operation, Map<String, String> values){
        this.table = table;
        this.operation = operation;
        this.values = values;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }


}
