package com.Mybuddy.Myb.Payload.Response;

public class MessageResponse {
    private String message;

    // Construtor
    public MessageResponse(String message) {
        this.message = message;
    }

    // Getter
    public String getMessage() {
        return message;
    }

    // Setter
    public void setMessage(String message) {
        this.message = message;
    }
}