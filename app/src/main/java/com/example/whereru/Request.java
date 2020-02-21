package com.example.whereru;

import java.util.Date;

public class Request {

    private String requestUser;
    private boolean status;

    public Request(String requestUser, Boolean status) {
        this.requestUser = requestUser;
        this.status = status;
    }

    public Request(){

    }

    public String getRequestUser() {
        return requestUser;
    }

    public void setRequestUser(String requestUser) {
        this.requestUser = requestUser;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


}