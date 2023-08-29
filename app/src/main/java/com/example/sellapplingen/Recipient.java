package com.example.sellapplingen;

import java.io.Serializable;

public class Recipient implements Serializable {


    private String firstName;
    private String lastName;
    private Address address;

    Recipient(String firstName, String lastName, Address address){

        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
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

    public void setAddress(Address address){
        this.address = address;
    }
    public Address getAddress(){
        return address;
    }
}
