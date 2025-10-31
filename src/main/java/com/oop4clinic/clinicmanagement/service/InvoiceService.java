package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.InvoiceDTO;
import com.oop4clinic.clinicmanagement.model.enums.InvoiceStatus;

import java.util.List;

public interface InvoiceService {
    List<InvoiceDTO> getAll();

    boolean updateInvoiceStatus(int id, InvoiceStatus newStatus);
}
