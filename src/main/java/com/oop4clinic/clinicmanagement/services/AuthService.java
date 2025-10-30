
package com.oop4clinic.clinicmanagement.services;

import com.oop4clinic.clinicmanagement.dao.UserDAO;
import com.oop4clinic.clinicmanagement.model.entity.User;
import com.oop4clinic.clinicmanagement.model.enums.UserRole;
import com.oop4clinic.clinicmanagement.util.EntityManagerProvider;
import jakarta.persistence.EntityManager;

import static com.oop4clinic.clinicmanagement.util.ValidationUtils.*;

public class AuthService {

    public User login(String username, String password) throws Exception {
        EntityManager em = EntityManagerProvider.em();
        try {
            if (isBlank(username) || isBlank(password)) {
                throw new Exception("Tên đăng nhập và mật khẩu không được để trống!");
            }

            UserDAO userDAO = new UserDAO();
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

    public User register(String username, String pass, String confirmpass) throws Exception {
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

            UserDAO userDAO = new UserDAO();
            User existing = userDAO.getUserbyUsername(em, username);
            if (existing != null) {
                throw new Exception("Tài khoản đã tồn tại.");
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(pass);
            newUser.setActive(true);
            newUser.setRole(UserRole.PATIENT);

            String saveMessage = userDAO.save(em, newUser);
            if (saveMessage != null) {

                throw new Exception(saveMessage);
            }
            return newUser;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }
}
