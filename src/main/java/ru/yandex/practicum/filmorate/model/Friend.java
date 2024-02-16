package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friend {
    private Integer id;
    private User userId;
    private User friendId;
    private FriendStatus friendStatus;
}
