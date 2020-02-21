package com.example.whereru;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Harsh on 15-03-2018.
 */

public class User {

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
    private String currentLocation;

    public HashMap<String, Request> getFriends() {
        return friends;
    }

    public void setFriends(HashMap<String, Request> friends) {
        this.friends = friends;
    }

    private HashMap<String, Request> friends;

    public String getFirstName() {
        return firstName;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
