package com.recsys.service;

import com.recsys.dao.UserDAO;
import com.recsys.exception.AuthenticationException;
import com.recsys.exception.UserNotFoundException;
import com.recsys.exception.ValidationException;
import com.recsys.model.Role;
import com.recsys.model.User;
import com.recsys.util.PasswordUtil;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class AuthService {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String password)
            throws SQLException, UserNotFoundException, AuthenticationException {
        User user = userDAO.findByEmail(normalise(email))
                .orElseThrow(() -> new UserNotFoundException("No account for " + email));
        if (!PasswordUtil.verify(password == null ? "" : password, user.getPasswordHash())) {
            throw new AuthenticationException("Wrong password");
        }
        return user;
    }

    public void register(String name, String email, String password) throws SQLException, ValidationException {
        if (name == null || name.isBlank() || name.length() > 80) {
            throw new ValidationException("Enter your name (up to 80 characters).");
        }
        String mail = normalise(email);
        if (!EMAIL.matcher(mail).matches()) {
            throw new ValidationException("Enter a valid email address.");
        }
        if (password == null || password.length() < 8) {
            throw new ValidationException("Use a password with at least 8 characters.");
        }
        if (userDAO.findByEmail(mail).isPresent()) {
            throw new ValidationException("That email is already registered. Try signing in.");
        }
        userDAO.create(name.trim(), mail, PasswordUtil.hash(password), Role.USER);
    }

    private String normalise(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
