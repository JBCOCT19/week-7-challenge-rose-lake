package com.roselake.jbc;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface MessageRepository extends CrudRepository<Message, Long> {

    // Custom Search Query (for search functionality)
    ArrayList<Message> findByContentContainsIgnoreCaseOrTitleContainsIgnoreCase(String content, String title);

    // Query for fetching out all messages associated with "username"
    ArrayList<Message> findByUsername(String username);


    // QUERY BUILDING NOTES:
    // findByContentContaining -- "tor" will pull up "Victor", etc.
    // findByContentContains -- "tor" will pull up only the actual word "tor".

}
