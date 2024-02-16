package ru.yandex.practicum.filmorate.storage.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.core.IdIterator;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class InMemoryMpaStorage implements MpaStorage {
    private final HashMap<Integer, Mpa> mpaHashMap = new HashMap<>();

    public Mpa create(Mpa mpa) {
        mpa.setId(IdIterator.getMpaId());
        mpaHashMap.put(mpa.getId(), mpa);
        return mpa;
    }

    public Mpa getById(Integer mpaId) {
        if (!mpaHashMap.containsKey(mpaId)) {
            throw new NoSuchElementException("MPA with ID " + mpaId + " not found");
        }
        return mpaHashMap.get(mpaId);
    }

    public List<Mpa> getAll() {
        return new ArrayList<>(mpaHashMap.values());
    }
}
