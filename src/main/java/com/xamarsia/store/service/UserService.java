package com.xamarsia.store.service;

import com.xamarsia.store.entity.User;
import com.xamarsia.store.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with email " + email));
    }

    public boolean isEmailUsed(String email) {
        return repository.existsByEmail(email);
    }

    public User save(User user) {
        return repository.save(user);
    }
}
