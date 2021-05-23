package ru.nsu.ccfit.kokunina.dto.client.requests;

public class Login {
    private String name;

    public Login() {

    }

    public Login(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
