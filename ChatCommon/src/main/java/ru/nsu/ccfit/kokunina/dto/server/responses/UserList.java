package ru.nsu.ccfit.kokunina.dto.server.responses;

import ru.nsu.ccfit.kokunina.dto.User;

import java.util.ArrayList;

public class UserList {
    private ArrayList<User> users;

    public UserList() {
    }

    public UserList(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
