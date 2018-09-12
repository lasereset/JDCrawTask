package com.dianming.jd.tool;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;


@JsonInclude(Include.NON_NULL)
public class QueryResponse<T> extends ApiResponse {
    @JsonView({Object.class})
    private List<T> items;
    @JsonView({Object.class})
    private Pagination page;

    QueryResponse() {
    }

    public QueryResponse(int code, String result) {
        super(code, result);
        toString();
    }

    public QueryResponse(ApiResponse response, List<T> items) {
        super(response.code, response.result);
        this.items = items;
        toString();
    }

    public QueryResponse(ApiResponse response) {
        super(response.code, response.result);
        toString();
    }

    public QueryResponse(int code, String result, List<T> items) {
        super(code, null);
        this.items = items;
        this.result = result;
        toString();
    }

    public QueryResponse(ApiResponse response, List<T> items, Pagination page) {
        super(response.code, response.result);
        this.items = items;
        this.page = page;
        toString();
    }

    public QueryResponse(int code, String result, List<T> items, Pagination page) {
        super(code, null);
        this.items = items;
        this.page = page;
        this.result = result;
        toString();
    }

    public List<T> getItems() {
        return this.items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Pagination getPage() {
        return this.page;
    }

    public void setPage(Pagination page) {
        this.page = page;
    }
}