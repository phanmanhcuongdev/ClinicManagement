package com.oop4clinic.clinicmanagement.model.entity;

import com.oop4clinic.clinicmanagement.model.enums.Gender;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;


@Entity
@Table(name = "patients",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_patient_phone", columnNames = "phone"),
        @UniqueConstraint(name = "uk_patient_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_patient_cccd", columnNames = "cccd"),
        @UniqueConstraint(name = "uk_patient_insurance", columnNames = "insurance_code")
})
public class Patient {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // PK int


    @Column(nullable = false)
    private String fullName;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;


    @Column(nullable = false)
    private String dateOfBirth;


    @Column(nullable = false)
    private String phone;


    @Column
    private String email;


    @Column
    private String address;


    @Column
    private String cccd;


    @Column(name = "insurance_code")
    private String insuranceCode;


    @OneToMany(mappedBy = "patient")
    private Set<Appointment> appointments = new LinkedHashSet<>();


    @OneToMany(mappedBy = "patient")
    private Set<Invoice> invoices = new LinkedHashSet<>();


    @OneToMany(mappedBy = "patient")
    private Set<MedicalRecord> medicalRecords = new LinkedHashSet<>();


    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public String getInsuranceCode() { return insuranceCode; }
    public void setInsuranceCode(String insuranceCode) { this.insuranceCode = insuranceCode; }
    public Set<Appointment> getAppointments() { return appointments; }
    public void setAppointments(Set<Appointment> appointments) { this.appointments = appointments; }
    public Set<Invoice> getInvoices() { return invoices; }
    public void setInvoices(Set<Invoice> invoices) { this.invoices = invoices; }
    public Set<MedicalRecord> getMedicalRecords() { return medicalRecords; }
    public void setMedicalRecords(Set<MedicalRecord> medicalRecords) { this.medicalRecords = medicalRecords; }
}