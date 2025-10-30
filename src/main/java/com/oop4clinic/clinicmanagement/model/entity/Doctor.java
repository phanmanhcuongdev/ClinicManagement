package com.oop4clinic.clinicmanagement.model.entity;


import com.oop4clinic.clinicmanagement.model.enums.DoctorStatus;
import com.oop4clinic.clinicmanagement.model.enums.Gender;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "doctors",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_doctor_phone", columnNames = "phone"),
        @UniqueConstraint(name = "uk_doctor_email", columnNames = "email")
})
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // PK int


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_doctor_department"))
    private Department department;


    @Column(nullable = false)
    private String fullName;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;


    @Column(nullable = false)
    private String dateOfBirth;


    @Column(nullable = false)
    private String phone;


    @Column(nullable = false)
    private String email;


    @Column
    private String address;


    @Column
    private Double consultationFee;

    public DoctorStatus getStatus() {
        return status;
    }

    public void setStatus(DoctorStatus status) {
        this.status = status;
    }

    @Column
    private DoctorStatus status;

    @OneToMany(mappedBy = "doctor")
    private Set<Appointment> appointments = new LinkedHashSet<>();


    @OneToMany(mappedBy = "doctor")
    private Set<MedicalRecord> medicalRecords = new LinkedHashSet<>();


    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
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
    public Double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Double consultationFee) { this.consultationFee = consultationFee; }
    public Set<Appointment> getAppointments() { return appointments; }
    public void setAppointments(Set<Appointment> appointments) { this.appointments = appointments; }
    public Set<MedicalRecord> getMedicalRecords() { return medicalRecords; }
    public void setMedicalRecords(Set<MedicalRecord> medicalRecords) { this.medicalRecords = medicalRecords; }
}