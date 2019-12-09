package com.roselake.jbc;

import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {

    //******************************************************************
    // custom Query method
    // retrieves the available Role and assigns it to the new users
    // as they are created in the DataLoader class
    //******************************************************************
    Role findByName(String name);

}
