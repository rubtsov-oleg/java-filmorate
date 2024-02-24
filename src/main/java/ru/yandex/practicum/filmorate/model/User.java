package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User implements Cloneable {
    private Integer id;

    @Email(message = "Email must be valid!")
    private String email;

    @NotBlank(message = "Login cannot be null or whitespace")
    private String login;

    private String name;

    @Past(message = "Birthday must be in the past.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Error with clonning object");
        }
    }
}
