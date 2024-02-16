package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.model.validators.IsAfter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film implements Cloneable {
    private Integer id;

    @NotBlank(message = "Name cannot be null or whitespace")
    private String name;

    @Size(max = 200, message = "Description must be less than 200 characters")
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @IsAfter(message = "Release Date must be after 1895-12-28", current = "1895-12-28")
    private LocalDate releaseDate;

    @Positive(message = "Duration must be positive")
    private Integer duration;

    private Mpa mpa;

    private Set<Genre> genres = new HashSet<>();

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Error with clonning object");
        }
    }
}
