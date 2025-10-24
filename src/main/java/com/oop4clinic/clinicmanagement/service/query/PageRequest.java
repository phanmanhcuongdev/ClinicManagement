package com.oop4clinic.clinicmanagement.service.query;

public class PageRequest {
    private int page;     // 0-based
    private int size;     // ví dụ 20

    public PageRequest() {
    }

    public PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
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
    // ctor/getters/setters
}
