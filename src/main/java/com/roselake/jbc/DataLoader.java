package com.roselake.jbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... strings) throws Exception {

        if (roleRepository.count() == 0) {

            //*******************************************
            // Making Roles :: for Security Layer
            // Role Constructor :: name
            //*******************************************
            roleRepository.save(new Role("USER"));
            roleRepository.save(new Role("ADMIN"));
            Role adminRole = roleRepository.findByName("ADMIN");
            Role userRole = roleRepository.findByName("USER");

            //*******************************************
            // Making Users :: for Security Layer
            // User Constructors ::
            // first, last, email, username, password, image,  enabled
            // first, last, email, username, password, enabled
            //*******************************************
            passwordEncoder = new BCryptPasswordEncoder();

            userRepository.save(new User("User", "Last", "user@user.com", "user", passwordEncoder.encode("user"), true));
            User user = userRepository.findByUsername("user");
            user.setRoles(Arrays.asList(userRole));
            userRepository.save(user);

            userRepository.save(new User("Rose", "Lake", "rose@lake.com", "rose", passwordEncoder.encode("lake"), true));
            User rose = userRepository.findByUsername("rose");
            rose.setRoles(Arrays.asList(userRole));
            userRepository.save(rose);

            userRepository.save(new User("Brian", "Lake", "brian@lake.com", "brian", passwordEncoder.encode("lake"), true));
            User brian = userRepository.findByUsername("brian");
            brian.setRoles(Arrays.asList(userRole));
            userRepository.save(brian);

            //*******************************************
            // Making Messages ::
            // Message Constructors ::
            // title, date, username, content
            // title, date, username, content, image
            //*******************************************
            messageRepository.save(new Message("Sitting on a Green Chair", "December 2, 2019", "brian",
                    "Reflecting on the world, seeing the blue sky, sitting, like a giant, upon a giant Green Chair. " +
                            "What would you do?",
                    "https://res.cloudinary.com/roselake/image/upload/v1575907086/chairscape.png"));
            messageRepository.save(new Message("Orange", "November 24, 2019", "rose",
                    "What's in a color...",
                    "https://res.cloudinary.com/roselake/image/upload/v1575907086/orangeleaves.png"));
            messageRepository.save(new Message("Sugarloaf Musings", "November 22, 2019", "rose",
                    "Running off to sugarloaf, standing on top of a mountain.",
                    "https://res.cloudinary.com/roselake/image/upload/v1575907086/sugarloaf.png"));
            messageRepository.save(new Message("Dark Sky", "December 7, 2019", "user",
                    "When the sky looks like that, you stop to take a look. This time the photograph took.",
                    "https://res.cloudinary.com/roselake/image/upload/v1575907086/pinkstreaks.png"));
            messageRepository.save(new Message("Red Sky", "March 18, 2020", "user",
                    "architectural cloudscape, hinting at a Kelvin-Helmholtz formation",
                    "https://res.cloudinary.com/roselake/image/upload/v1575907086/redsky.png"));
            messageRepository.save(new Message("The Purple Night Becomes Her", "March 8, 2018", "rose",
                    "the purple night",
                    "https://res.cloudinary.com/roselake/image/upload/v1575907086/purplenight.png"));
            messageRepository.save(new Message("Alaskan Skies", "November 20th, 2019", "user",
                    "who can say who was there and who saw this cold shining",
                    "https://res.cloudinary.com/roselake/image/upload/v1575907086/fullmoon.png"));
            messageRepository.save(new Message("Gypsy Sally's", "November 30th, 2019", "brian",
                    "gypsy rose forever",
                    "https://res.cloudinary.com/roselake/image/upload/v1575907086/gypsysallys.png"));

            //*******************************************
            // Linking Users to their Messages ::
            //*******************************************
            ArrayList<Message> roseMessages = messageRepository.findByUsername("rose");
            rose.setMessages(roseMessages);
            userRepository.save(rose);
            for (Message roseMessage : roseMessages){
                roseMessage.setUser(rose);
                messageRepository.save(roseMessage);
            }

            ArrayList<Message> brianMessages = messageRepository.findByUsername("brian");
            brian.setMessages(brianMessages);
            userRepository.save(brian);
            for (Message brianMessage : brianMessages){
                brianMessage.setUser(brian);
                messageRepository.save(brianMessage);
            }

            ArrayList<Message> userMessages = messageRepository.findByUsername("user");
            user.setMessages(userMessages);
            userRepository.save(user);
            for(Message userMessage : userMessages){
                userMessage.setUser(user);
                messageRepository.save(userMessage);
            }

        }
    }

}
