package com.github.darylyeung.zentaoplugin.extension.zentao.model;

import java.util.List;

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-15 21:59:53
 */
public class ZentaoProductPage {
    private int page;
    private int total;
    private int limit;
    private List<ZentaoProduct> products;

    public ZentaoProductPage(int page, int total, int limit, List<ZentaoProduct> products) {
        this.page = page;
        this.total = total;
        this.limit = limit;
        this.products = products;
    }

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

    public List<ZentaoProduct> getProducts() {
        return products;
    }

    public void setProducts(List<ZentaoProduct> products) {
        this.products = products;
    }
}
