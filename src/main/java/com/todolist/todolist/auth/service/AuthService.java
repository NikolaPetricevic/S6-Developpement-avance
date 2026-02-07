package com.todolist.todolist.auth.service;

import com.todolist.todolist.users.entity.User;
import com.todolist.todolist.users.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private static final String SESSION_USER_KEY = "loggedUser";

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public User login(String username, String password, HttpSession session) {
        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute(SESSION_USER_KEY, user);
            return user;
        }

        return null;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute(SESSION_USER_KEY);
    }

    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(SESSION_USER_KEY) != null;
    }

}
