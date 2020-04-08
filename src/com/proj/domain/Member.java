package com.proj.domain;

public class Member {

    private String firstName;

    private String lastName;

    public Member(String firstName, String lastName) {

        super();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Member() {

        super();
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

    @Override
    public String toString() {

        return "Member [firstName=" + firstName + ", lastName=" + lastName + "]";
    }

    
}
