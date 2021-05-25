package ru.nsu.ccfit.kokunina.dto.client.requests;

public class UserListRequest {
    private int sessionId;

    public UserListRequest() {
    }

    public UserListRequest(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
}
