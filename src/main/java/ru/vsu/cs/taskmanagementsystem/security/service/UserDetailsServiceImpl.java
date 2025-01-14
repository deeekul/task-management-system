package ru.vsu.cs.taskmanagementsystem.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.vsu.cs.taskmanagementsystem.user.adapter.jpa.UserRepository;
import ru.vsu.cs.taskmanagementsystem.security.entity.UserDetailsImpl;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByLogin(username)
                .map(UserDetailsImpl::new).
                orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден!")
        );
        return user;
    }
}