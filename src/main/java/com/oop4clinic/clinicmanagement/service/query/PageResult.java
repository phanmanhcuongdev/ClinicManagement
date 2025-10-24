package com.oop4clinic.clinicmanagement.service.query;

import java.util.List;

public class PageResult<T> {
    private java.util.List<T> content;
    private long totalElements;
    private int page;        // 0-based
    private int size;
    private int totalPages;  // ceil(total/size)

    public PageResult() {
    }

    public PageResult(List<T> content, long totalElements, int page, int size, int totalPages) {
        this.content = content;
        this.totalElements = totalElements;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    // ctor/getters/setters
}