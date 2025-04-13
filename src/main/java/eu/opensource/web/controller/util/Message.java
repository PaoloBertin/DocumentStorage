package eu.opensource.web.controller.util;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Message {

    private String type;

    private String message;

    public Message(String type, String message) {

        this.type    = type;
        this.message = message;
    }
}
