package com.roselake.jbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class HomeController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    MessageRepository messageRepository;

    @RequestMapping("/")
    public String index(Model model){
        if(userService.getAuthenticatedUser() != null) {
            model.addAttribute("user", userService.getAuthenticatedUser());
        }
        model.addAttribute("messages", messageRepository.findAll());
        return "index";
    }

    @RequestMapping("/login")
    public String login(Model model){
        System.out.println("in login method, about to call up login page :: REQUEST MAPPING handles both");
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        System.out.println("in register method, will create new User() and call userform");
        model.addAttribute("user", new User());
        return "userform";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user,
                                          BindingResult result,
                                          Model model) {
        System.out.println("In register post mapping method");
        // if there are validation errors ::
        if (result.hasErrors()) {
            System.out.println("*** BINDING result had errors ***");
            System.out.println(result.getAllErrors().toString());
            return "userform";
        }

        // if the username already exists ::
        if (userService.countByUsername(user.getUsername()) > 0) {
            System.out.println("user name existed");
            model.addAttribute("message", "username already exists");
            return "userform";
        }

        // if the email already exists ::
        if (userService.countByEmail(user.getEmail()) > 0) {
            System.out.println("email existed");
            model.addAttribute("message", "this email has already been used to register");
            return "userform";
        }

        // if we get this far, then the username and email are valid, and we can move on...
        System.out.println("all seemed well, will save new user via user service");
        userService.saveNewUserUser(user);
        model.addAttribute("message", "User Account Created");
        return "login";
    }

}
