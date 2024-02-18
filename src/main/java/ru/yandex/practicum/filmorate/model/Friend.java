package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friend implements Cloneable {
    private Integer id;
    private Integer userId;
    private Integer friendId;
    private FriendStatus friendStatus;

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Error with clonning object");
        }
    }
}
