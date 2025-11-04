
package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.DoctorRepository;
import com.oop4clinic.clinicmanagement.dao.impl.DoctorRepositoryImpl;
import com.oop4clinic.clinicmanagement.dao.impl.UserRepositoryImp;
import com.oop4clinic.clinicmanagement.dao.jpa.EntityManagerProvider;
import com.oop4clinic.clinicmanagement.model.dto.UserDTO;
import com.oop4clinic.clinicmanagement.model.entity.Department;
import com.oop4clinic.clinicmanagement.model.entity.Doctor;
import com.oop4clinic.clinicmanagement.model.entity.User;
import com.oop4clinic.clinicmanagement.model.enums.DoctorStatus;
import com.oop4clinic.clinicmanagement.model.enums.Gender;
import com.oop4clinic.clinicmanagement.model.enums.UserRole;
import com.oop4clinic.clinicmanagement.service.DoctorService;
import com.oop4clinic.clinicmanagement.service.UserService;
import com.oop4clinic.clinicmanagement.util.ValidationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

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

    @Override
    public UserDTO createOrResetDoctorAccount(Integer doctorId) throws Exception {
        EntityManager em = EntityManagerProvider.em();

        try {
            em.getTransaction().begin();

            DoctorRepositoryImpl doctorRepo = new DoctorRepositoryImpl();
            UserRepositoryImp userRepo = new UserRepositoryImp();

            // Lấy thông tin bác sĩ
            Doctor doctor = doctorRepo.findById(em, doctorId);
            if (doctor == null)
                throw new Exception("Không tìm thấy bác sĩ với ID: " + doctorId);

            if (doctor.getPhone() == null || doctor.getPhone().isBlank())
                throw new Exception("Bác sĩ chưa có số điện thoại, không thể tạo tài khoản.");

            // Username = SĐT
            String username = doctor.getPhone();

            // Password = ngày sinh (ddMMyy)
            String password = ValidationUtils.formatDobAsPassword(doctor.getDateOfBirth());

            // Kiểm tra tài khoản
            User existing = null;
            try {
                existing = userRepo.getUserbyUsername(em, username);
            } catch (NoResultException ignored) {}

            if (existing == null) {
                // 🟢 Chưa có tài khoản → tạo mới
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setActive(true);
                newUser.setRole(UserRole.DOCTOR);
                userRepo.save(em, newUser);
            } else {
                // 🟠 Đã có → reset mật khẩu
                existing.setPassword(password);
                userRepo.update(em, existing);
            }

            em.getTransaction().commit();

            // Trả về thông tin để controller hiển thị
            UserDTO dto = new UserDTO();
            dto.setUsername(username);
            dto.setPassword(password);
            dto.setRole(UserRole.DOCTOR);
            dto.setActive(true);
            return dto;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
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



