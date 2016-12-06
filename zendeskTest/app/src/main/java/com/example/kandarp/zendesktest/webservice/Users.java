package com.example.kandarp.zendesktest.webservice;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by Kandarp on 12/5/2016.
 */

public class Users {

    @SerializedName("users")
    List<user> userList;

    public List<user> getUserList(){return userList;}
    public Users(){}

    public class user{
        @SerializedName("name")
        private String name;
        @SerializedName("id")
        private long id;

        public String getName() {
            return name;
        }

        public long getId() {
            return id;
        }
    }
}
