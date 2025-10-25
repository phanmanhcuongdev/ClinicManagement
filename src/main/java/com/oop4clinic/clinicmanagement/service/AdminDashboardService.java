package com.oop4clinic.clinicmanagement.service;

import com.oop4clinic.clinicmanagement.model.dto.DashboardSummaryDTO;

import java.util.*;
import java.time.*;

public interface AdminDashboardService {
    DashboardSummaryDTO loadDashboardData();
}
