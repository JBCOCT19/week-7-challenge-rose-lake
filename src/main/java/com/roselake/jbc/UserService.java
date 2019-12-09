package com.roselake.jbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Long countByEmail(String email){
        return userRepository.countByEmail(email);
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public Long countByUsername(String username){
        return userRepository.countByUsername(username);
    }

    public void saveUser(User user){
        user.setRoles(Arrays.asList(roleRepository.findByName("USER")));
        // sets user's Role to USER
        user.setEnabled(true);
        // ensures this user is active/enabled
        userRepository.save(user);
    }

    public void saveAdmin(User user){
        user.setRoles(Arrays.asList(roleRepository.findByName("ADMIN")));
        // sets this user's Role to ADMIN
        user.setEnabled(true);
        userRepository.save(user);
    }

    // NOTE: this UserService does not currently allow for setting MULTIPLE roles on a user!...

    // this returns the currently logged-in user... so cool!...
    public User getUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String currentUserName = authentication.getName();
        System.out.println("Current User Name from Authentication is " + currentUserName);

        User user = userRepository.findByUsername(currentUserName);
        if (user!=null) {
            System.out.println("ran findByUsername and it returned a user by the name of " + user.getUsername());
        } else {
            System.out.println("user was null, currentUserName was " + currentUserName);
        }
        return userRepository.findByUsername(currentUserName);

    }

}
