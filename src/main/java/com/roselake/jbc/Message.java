package com.roselake.jbc;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @Size(min=2, max=50)
    private String title;

    @NotNull
    @Size(min=6, max=30)
    private String date;

    @NotNull
    private String username;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Size(min=3)
    @Column(columnDefinition = "VARCHAR(4096)")
    private String content;

    @NotNull
    private String image;

    public Message() {
        this.image = "";
    }

    // constructor for DataLoader :: WITH IMAGE
    public Message(@NotNull @Size(min = 2, max = 50) String title,
                   @NotNull @Size(min = 6, max = 30) String date,
                   @NotNull String username,
                   @NotNull @Size(min = 3) String content,
                   @NotNull String image) {
        this.title = title;
        this.date = date;
        this.username = username;
        this.content = content;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

