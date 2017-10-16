package com.yosidozli.meirkidsapp.registration;


import java.io.Serializable;

/**
 * Created by yosid on 05/06/2017.
 */

public class User implements Serializable {


    private long id;
    private long personId;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private boolean approved = false;


    public User() {
    }

    public User(long id, long personId, String firstName, String lastName) {
        this.id = id;
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;


    }
    public User(long id, long personId, String firstName, String lastName, String userName, String password) {
       this(id,personId,firstName,lastName);
       this.userName = userName;
       this.password = password;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
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


    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
