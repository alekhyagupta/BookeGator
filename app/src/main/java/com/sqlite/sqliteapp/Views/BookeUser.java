package com.sqlite.sqliteapp.Views;

/**
 * Created by Shilpa Goel on 11/7/2015.
 */

public class BookeUser {
    private String username;
    private String emailAddress;
    private String phoneNumber;
    private String address;

    BookeUser(String username, String emailAddress, String phoneNumber, String address) {
        setUsername(username);
        setEmailAddress(emailAddress);
        setPhoneNumber(phoneNumber);
        setAddress(address);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
