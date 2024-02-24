# java-filmorate

# Схема базы данных:
![Иллюстрация к проекту](https://github.com/rubtsov-oleg/java-filmorate/blob/add-database/schema.png)

# Основные SQL-запросы:

Таблицы `films` и `film_genres`:
```
INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?);

INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?);

UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? where id = ?;

DELETE FROM film_genres WHERE film_id = ?;

DELETE FROM films where id = ?;

SELECT f.id, f.name, f.description, f.release_date, f.duration, m.id as mpa_id, m.name as mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.id WHERE f.id = ?;

SELECT g.id, g.name FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?;

SELECT f.id, f.name, f.description, f.release_date, f.duration, m.id as mpa_id, m.name as mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.id;
```

Таблица `friends`:
```
INSERT INTO friends (user_id, friend_id, friend_status) VALUES (?, ?, ?);

UPDATE friends SET friend_status = ? where id = ?;

DELETE FROM friends where id = ?;

SELECT * FROM friends f WHERE (f.user_id = ?) OR (f.friend_id = ? AND f.friend_status = 'CONFIRMED');

SELECT * FROM friends WHERE user_id = ? and friend_id = ?;
```

Таблица `genres`:
```
INSERT INTO genres (name) VALUES (?);

SELECT id, name FROM genres WHERE id = ?;

SELECT id, name FROM genres;
```

Таблица `likes`:
```
INSERT INTO likes (user_id, film_id) VALUES (?, ?);

DELETE FROM likes where id = ?;

SELECT * FROM likes WHERE film_id = ?;

SELECT * FROM likes WHERE film_id = ? and user_id = ?;
```

Таблица `mpa`:
```
INSERT INTO mpa (name) VALUES (?);

SELECT id, name FROM mpa WHERE id = ?;

SELECT id, name FROM mpa;
```

Таблица `users`:
```
SELECT id, email, login, name, birthday  FROM users WHERE id = ?;

SELECT id, email, login, name, birthday FROM users;

DELETE FROM users where id = ?;

INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?);

UPDATE users SET email = ?, login = ?, name = ?, birthday = ? where id = ?;
```