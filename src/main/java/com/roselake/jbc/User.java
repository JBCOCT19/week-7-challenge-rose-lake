package com.roselake.jbc;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "User_Data")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @Size(min = 2)
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Size(min = 2)
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Size(min = 5)
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Size(min = 2)
    @Column(name = "username")
    private String username;

    @NotNull
    @Size(min = 2)
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "image_url")
    private String image;

    // @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // it's possible that the CascadeType.ALL is only necessary in the MANY side of the relationship,
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Message> messages;

    //**********************
    // BONUS : to implement later
    // only storing the IDs of the other users
    @Column(name = "people_i_follow")
    private ArrayList<Long> peopleIFollow;
    @Column(name = "people_following_me")
    private ArrayList<Long> peopleFollowingMe;
    //**********************

    //***********************
    // SYSTEM
    // the user themselves never edits this stuff ... only an admin could
    @Column(name = "enabled")
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;
    //***********************


    // default constructor with "" and blank array list for simpler processing!
    public User() {
        this.image = "";
        this.messages = new ArrayList<>();
    }

    // constructor for user in the DataLoader :: NO image
    public User(@NotNull @Size(min = 2) String firstName,
                @NotNull @Size(min = 2) String lastName,
                @NotNull @Size(min = 5) String email,
                @NotNull @Size(min = 2) String username,
                @NotNull @Size(min = 2) String password,
                boolean enabled) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        // for easier processing
        this.image = "";
        this.messages = new ArrayList<>();
    }

    // constructor for use in the DataLoader :: YES image
    public User(@NotNull @Size(min = 2) String firstName,
                @NotNull @Size(min = 2) String lastName,
                @NotNull @Size(min = 5) String email,
                @NotNull @Size(min = 2) String username,
                @NotNull @Size(min = 2) String password,
                @NotNull String image,
                boolean enabled) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.image = image;
        this.enabled = enabled;
        // for easier processing
        this.messages = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public ArrayList<Long> getPeopleIFollow() {
        return peopleIFollow;
    }

    public void setPeopleIFollow(ArrayList<Long> peopleIFollow) {
        this.peopleIFollow = peopleIFollow;
    }

    public ArrayList<Long> getPeopleFollowingMe() {
        return peopleFollowingMe;
    }

    public void setPeopleFollowingMe(ArrayList<Long> peopleFollowingMe) {
        this.peopleFollowingMe = peopleFollowingMe;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }
}

//    //**********************************************************************************
//    // ** HAVE MOVED ENCRYPTION into USER-SERVICE class **
//    // moving this into the UserService class!
//    // remember to do it manually in the DataLoader, as well... and anywhere else... !
//    // Security Layer
//    //**********************************************************************************
//    // this is similar to the two-step process we use in the SecurityConfiguration class
//    public void setPassword(String password) {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        this.password = passwordEncoder.encode(password);
//    }
//    //**********************************************************************************

