package com.todolist.todolist.utils;

import java.util.Map;

public class QueryBuilder {
    private final String query;
    private final Map<String, Object> parameters;

    public QueryBuilder(String query, Map<String, Object> parameters) {
        this.query = query;
        this.parameters = parameters;
    }

    public String getQuery() {
        return query;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
