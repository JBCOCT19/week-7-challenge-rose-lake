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

    //****************************************************
    // this is called by the registration processing code
    // registration == self-registration == USER only
    //****************************************************
    public void saveNewUserUser(User user){

        System.out.println("In Save New USER user method, in User Service");
        // set user's Role to USER
        user.setRoles(Arrays.asList(roleRepository.findByName("USER")));

        // ensure user is active/enabled
        user.setEnabled(true);

        // encrypt password
        passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println("set encoded password");

        userRepository.save(user);
        System.out.println("just saved the user to the user repository");

    }

    //***********************************************************
    // possibly may need a saveExistingUser method, later
    // this would be for processing a form which edits the user,
    //                      including the whole password mess...
    //***********************************************************


//    // for future use
//    public void saveNewAdminUser(User user){
//
//        // set user's Role to ADMIN
//        user.setRoles(Arrays.asList(roleRepository.findByName("ADMIN")));
//
//        // ensure user is active/enabled
//        user.setEnabled(true);
//
//        // encrypt password
//        passwordEncoder = new BCryptPasswordEncoder();
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//        userRepository.save(user);
//    }

    // this returns the currently logged-in user... so cool!...
    public User getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        return userRepository.findByUsername(currentUserName);
    }

}
