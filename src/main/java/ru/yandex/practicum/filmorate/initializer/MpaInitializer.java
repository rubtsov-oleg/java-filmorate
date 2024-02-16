package ru.yandex.practicum.filmorate.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaInitializerException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaInitializer {
    private final MpaStorage mpaStorage;

    @Value("${mpa.file.path}")
    private String mpaFilePath;

    @PostConstruct
    public void init() {
        List<Mpa> mpaList = mpaStorage.getAll();
        List<String> mpaNames = new ArrayList<>();
        for (Mpa mpa: mpaList) {
            mpaNames.add(mpa.getName());
        }
        try (FileReader reader = new FileReader(mpaFilePath, StandardCharsets.UTF_8)) {
            BufferedReader br = new BufferedReader(reader);

            while (br.ready()) {
                String name = br.readLine();
                if (!mpaNames.contains(name)) {
                    mpaStorage.create(createMpa(name));
                }
            }
        } catch (IOException e) {
            throw new MpaInitializerException("Ошибка при чтении данных из файла", e);
        }
    }

    private Mpa createMpa(String name) {
        Mpa mpa = new Mpa();
        mpa.setName(name);
        return mpa;
    }
}
