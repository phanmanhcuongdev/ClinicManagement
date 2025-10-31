
package com.oop4clinic.clinicmanagement.service.impl;

import com.oop4clinic.clinicmanagement.dao.impl.UserRepositoryImp;
import com.oop4clinic.clinicmanagement.model.entity.User;
import com.oop4clinic.clinicmanagement.model.enums.UserRole;
import com.oop4clinic.clinicmanagement.service.UserService;
import com.oop4clinic.clinicmanagement.util.EntityManagerProvider;
import jakarta.persistence.EntityManager;

import static com.oop4clinic.clinicmanagement.util.ValidationUtils.*;

public class AuthService implements UserService {

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

            String saveMessage = userRepositoryImp.save(em, newUser);
            if (saveMessage != null) {

                throw new Exception(saveMessage);
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
            //return false;
        } finally {
            em.close();
        }
    }
}
