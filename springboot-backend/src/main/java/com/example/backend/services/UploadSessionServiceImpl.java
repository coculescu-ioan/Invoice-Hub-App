package com.example.backend.services;

import com.example.backend.enums.UserRoleEnum;
import com.example.backend.models.UploadSession;
import com.example.backend.models.User;
import com.example.backend.repositories.UploadSessionRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UploadSessionServiceImpl implements UploadSessionService{

    @Autowired
    private UploadSessionRepository uploadSessionRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public List<UploadSession> loadAll() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return Objects.equals(user.getRole(), UserRoleEnum.ADMIN) ?
                    uploadSessionRepository.findAll() :
                    uploadSessionRepository.findAllByUserId(user.getId());
        }
        else {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
        }
    }
}