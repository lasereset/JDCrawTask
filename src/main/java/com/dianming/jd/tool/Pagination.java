package com.dianming.jd.tool;

import com.fasterxml.jackson.annotation.JsonView;


public class Pagination {
    @JsonView({Object.class})
    private int page;
    @JsonView({Object.class})
    private int pageSize;
    @JsonView({Object.class})
    private boolean hasNext;
    @JsonView({Object.class})
    private boolean hasPrev;
    @JsonView({Object.class})
    private int total;

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isHasNext() {
        return this.hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrev() {
        return this.hasPrev;
    }

    public void setHasPrev(boolean hasPrev) {
        this.hasPrev = hasPrev;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}