package com.github.darylyeung.zentaoplugin.extension.zentao.model;

import java.util.List;

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-15 22:28:35
 */
public class ZentaoBugPage {
    private int page;
    private int total;
    private int limit;
    private List<ZentaoBug> bugs;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<ZentaoBug> getBugs() {
        return bugs;
    }

    public void setBugs(List<ZentaoBug> bugs) {
        this.bugs = bugs;
    }

    public ZentaoBugPage(int page, int total, int limit, List<ZentaoBug> bugs) {
        this.page = page;
        this.total = total;
        this.limit = limit;
        this.bugs = bugs;
    }
}
