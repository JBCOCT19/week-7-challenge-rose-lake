package com.roselake.jbc;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    CloudinaryConfig cloudc;



    //***************************
    // PUBLIC ACCESS ROUTES
    //***************************

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

    //*******************************
    // VIEW SELECTED USER
    //*******************************
    @RequestMapping("/viewuser/{username}")
    public String viewSelectedUser(@PathVariable("username") String username, Model model){
        System.out.println("in VIEW SELECTED USER method, viewing " + username);
        if(userRepository.findByUsername(username)!=null){
            model.addAttribute("user", userRepository.findByUsername(username));
        } else {
            model.addAttribute("infoMessage", "there was an error trying to access USER with USERNAME " + username);
            return "redirect:/";
        }
        return "userdetail";
    }

    @RequestMapping("/viewmessage/{id}")
    public String viewMessage(@PathVariable("id") long id, Model model){
        System.out.println("asked to view message with ID = " + id);
        if(userService.getAuthenticatedUser()!=null){
            model.addAttribute("user", userService.getAuthenticatedUser());
        }
        model.addAttribute("message", messageRepository.findById(id).get());
        return "messagedetail";
    }

    //**********************
    // REGISTRATION
    //**********************
    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        System.out.println("in register method, will create new User() and call userform");
        model.addAttribute("user", new User());
        return "userform";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user,
                                          BindingResult result,
                                          @RequestParam("file") MultipartFile file,
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
            model.addAttribute("infoMessage", "username already exists");
            return "userform";
        }

        // if the email already exists ::
        if (userService.countByEmail(user.getEmail()) > 0) {
            System.out.println("email existed");
            model.addAttribute("infoMessage", "this email has already been used to register");
            return "userform";
        }

        // if we get this far, then the username and email are valid,
        // and we can move on...

        // PROCESS IMAGE, if any
        if (!file.isEmpty()) {
            try {
                Map uploadResult = cloudc.upload(file.getBytes(),
                        ObjectUtils.asMap("resourcetype", "auto"));
                user.setImage(uploadResult.get("url").toString());
            } catch (IOException e) {
                e.printStackTrace();
                user.setImage("");
                // this fails semi-quietly :: a stack trace is printed
                // but otherwise the application behaves as if the student was successfully saved (with no image)
            }
        }
        // else :: no image file was selected :: it's okay, don't do anything extra,
        // just continue on to the SAVE step, below ...

        System.out.println("all seemed well, will save new user via user service");
        userService.saveNewUserUser(user);
        model.addAttribute("infoMessage", "User Account Created");
        return "login";
    }


    //***************************
    // AUTHENTICATED ACCESS ONLY
    //***************************


    //***************************
    // VIEW YOURSELF ::
    // It's a special method because didn't have an "id" linked to this request path in the nav bar
    //***************************
    @RequestMapping("/viewcurrentuser")
    public String viewCurrentUser(Model model){
        System.out.println("in VIEW CURRENT USER method");
        if(userService.getAuthenticatedUser() != null){
            model.addAttribute("user", userService.getAuthenticatedUser());
        } else {
            model.addAttribute("infoMessage", "there was an error trying to access CURRENT AUTHENTICATED USER");
            return "redirect:/";
        }
        return "userdetail";
    }

    //**********************
    // MANAGING MESSAGES
    //**********************
    @GetMapping("/addmessage")
    public String addMessage(Model model){
        model.addAttribute("message", new Message());
        return "messageform";
    }

    @GetMapping("/editmessage/{id}")
    public String editMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message", messageRepository.findById(id).get());
        return "messageform";
    }

    @GetMapping("/deletemessage/{id}")
    public String deleteMessage(@PathVariable("id") long id, Model model){
        // do the deleting code
        return "redirect:/";    // this will run the code inside of "/" mapping!
    }

    @PostMapping("/processmessage")
    public String processMessage(@Valid @ModelAttribute("message") Message message,
                                 BindingResult result,
                                 @RequestParam("file") MultipartFile file,
                                 Model model){

        System.out.println("at the top of processMessage method!");
        // check for errrors
        if (result.hasErrors()){
            model.addAttribute("infoMessage", "validation error");
            return "messageform";
        }

        // establish message in the db
        Message processMessage;
        long messageId = message.getId();
        System.out.println("** Processing MESSAGE with ID = " + messageId);
        if(messageRepository.existsById(messageId)){
            // we are editing an existing message
            processMessage = messageRepository.findById(messageId).get();

            // do not allow them to edit username or author of a message :: these pass-through
            // only re-save values that may have changed
            processMessage.setTitle(message.getTitle());
            processMessage.setDate(message.getDate());
            processMessage.setContent(message.getContent());
            messageRepository.save(processMessage);
        }
        else {
            // we are creating a new message
            processMessage = messageRepository.save(message);
            // this saves everything that the user entered

            // Now, be sure to establish the LINKS to the USER !!

            // FETCH USER and LINK message into its "messages" field
            User currentUser = userService.getAuthenticatedUser();
            currentUser.addMessage(processMessage);
            userRepository.save(currentUser);

            // LINK USER into message's "user" field
            processMessage.setUser(currentUser);
            processMessage.setUsername(currentUser.getUsername());
            messageRepository.save(processMessage);
        }

        // PROCESS IMAGE, if any
        if (!file.isEmpty()) {
            try {
                Map uploadResult = cloudc.upload(file.getBytes(),
                        ObjectUtils.asMap("resourcetype", "auto"));
                processMessage.setImage(uploadResult.get("url").toString());
            } catch (IOException e) {
                e.printStackTrace();
                processMessage.setImage("");
                // this fails semi-quietly :: a stack trace is printed
                // but otherwise the application behaves as if the student was successfully saved (with no image)
            }
        }
        // else :: no image file was selected :: it's okay, don't do anything extra, just...
        // SAVE result of image step
        messageRepository.save(processMessage);

        model.addAttribute("infMessage", "Successfully created new message '" + message.getTitle() + "'");
        System.out.println("processed and saved MESSAGE with the following values..." +
                "\n\tID = " + processMessage.getId() +
                "\n\tTITLE = " + processMessage.getTitle() +
                "\n\tCONTENT = " + processMessage.getContent() +
                "\n\tusername = " + processMessage.getUsername() +
                "\n\tUSER's first name = " + processMessage.getUser().getFirstName() +
                "\n\timage url = " + processMessage.getImage());

        return "redirect:/";

    }


}
