package com.oop4clinic.clinicmanagement.service.query;

import com.oop4clinic.clinicmanagement.model.enums.Gender;
import java.time.LocalDate;

public class PatientFilter {
    private String keyword;        // tìm theo tên/phone/email/insurance/address
    private Gender gender;         // null = không lọc
    private LocalDate dobFrom;     // null = không giới hạn dưới
    private LocalDate dobTo;       // null = không giới hạn trên

    public PatientFilter(String keyword, Gender gender, LocalDate dobFrom, LocalDate dobTo) {
        this.keyword = keyword;
        this.gender = gender;
        this.dobFrom = dobFrom;
        this.dobTo = dobTo;
    }

    public PatientFilter() {
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDobFrom() {
        return dobFrom;
    }

    public void setDobFrom(LocalDate dobFrom) {
        this.dobFrom = dobFrom;
    }

    public LocalDate getDobTo() {
        return dobTo;
    }

    public void setDobTo(LocalDate dobTo) {
        this.dobTo = dobTo;
    }

    // getters/setters + builder(optional)
}