
package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.DoctorRepository;
import com.oop4clinic.clinicmanagement.dao.impl.DoctorRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.impl.UserRepositoryImp;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.entity.Department;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;
import com.oop4clinic.clinicmanagement.model.entity.User;
import com.oop4clinic.clinicmanagement.model.enums.DoctorStatus;
import com.oop4clinic.clinicmanagement.model.enums.Gender;
import com.oop4clinic.clinicmanagement.model.enums.UserRole;
import com.oop4clinic.clinicmanagement.service.UserService;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;

import static com.oop4clinic.clinicmanagement.util.ValidationUtils.*;

public class AuthService implements UserService {

    private final DoctorRepository doctorRepository = new DoctorRepositoryImpl();
    @Override
    public User login(String username, String password) throws Exception {
        EntityManager em = EntityManagerProvider.em();
        try {
            if (isBlank(username) || isBlank(password)) {
                throw new Exception("Tên đăng nhập và mật khẩu không được để trống!");
            }

            UserRepositoryImp userDAO = new UserRepositoryImp();
            User user = userDAO.getUserbyUsername(em, username);
            if (user == null || !user.getPassword().equals(password)) {
                throw new Exception("Tài khoản hoặc mật khẩu không chính xác.");
            }

            return user;
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // ném tiếp cho tầng gọi
        } finally {
            em.close();
        }
    }

    @Override
    public boolean register(String username, String pass, String confirmpass) throws Exception {
        EntityManager em = EntityManagerProvider.em();
        try {
            em.getTransaction().begin();
            if (isBlank(username) || isBlank(pass) || isBlank(confirmpass)) {
                throw new Exception("Vui lòng nhập đầy đủ thông tin.");
            }

            if (!pass.equals(confirmpass)) {
                throw new Exception("Mật khẩu không khớp.");
            }

            if (!isValidPhone(username)) {
                throw new Exception("Số điện thoại không hợp lệ.");
            }

            UserRepositoryImp userRepositoryImp = new UserRepositoryImp();
            User existing = userRepositoryImp.getUserbyUsername(em, username);
            if (existing != null) {
                throw new Exception("Tài khoản đã tồn tại.");
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(pass);
            newUser.setActive(true);
           newUser.setRole(UserRole.PATIENT);
            //  newUser.setRole(UserRole.DOCTOR);

            userRepositoryImp.save(em, newUser);
            em.getTransaction().commit();

            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }

//    @Override
//    public boolean register(String username, String pass, String confirmpass) throws Exception {
//        EntityManager em = EntityManagerProvider.em();
//        try {
//            em.getTransaction().begin();
//
//            if (isBlank(username) || isBlank(pass) || isBlank(confirmpass)) {
//                throw new Exception("Vui lòng nhập đầy đủ thông tin.");
//            }
//
//            if (!pass.equals(confirmpass)) {
//                throw new Exception("Mật khẩu không khớp.");
//            }
//
//            if (!isValidPhone(username)) {
//                throw new Exception("Số điện thoại không hợp lệ.");
//            }
//
//            UserRepositoryImp userRepositoryImp = new UserRepositoryImp();
//            User existing = userRepositoryImp.getUserbyUsername(em, username);
//            if (existing != null) {
//                throw new Exception("Tài khoản đã tồn tại.");
//            }
//
//            User newUser = new User();
//            newUser.setUsername(username);
//            newUser.setPassword(pass);
//            newUser.setActive(true);
//            newUser.setRole(UserRole.DOCTOR);
//
//            userRepositoryImp.save(em, newUser);
//
//
//            if (newUser.getRole() == UserRole.DOCTOR) {
//                Doctor doctor = new Doctor();
//                doctor.setPhone(username);
//                doctor.setFullName("Bác sĩ mới");
//                doctor.setEmail(username + "@example.com");
//
//                doctor.setGender(Gender.MALE); // hoặc FEMALE
//                doctor.setDateOfBirth(LocalDate.of(1990, 1, 1));
//
//
//                var dept = em.createQuery("SELECT d FROM Department d", Department.class)
//                        .setMaxResults(1)
//                        .getSingleResult();
//                doctor.setDepartment(dept);
//
//                doctor.setStatus(DoctorStatus.ACTIVE);
//
//                doctorRepository.save(em, doctor);
//            }
//
//            em.getTransaction().commit();
//            return true;
//
//        } catch (Exception e) {
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            e.printStackTrace();
//            throw e;
//        } finally {
//            em.close();
//        }
    }



