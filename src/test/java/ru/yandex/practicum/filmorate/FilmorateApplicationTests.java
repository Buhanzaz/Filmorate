package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.db.DirectorDbStorage;
import ru.yandex.practicum.filmorate.repository.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.repository.db.RatingMpaDbStorage;
import ru.yandex.practicum.filmorate.repository.db.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class FilmorateApplicationTests {
	private final FilmDbStorage filmDbStorage;
	private final RatingMpaDbStorage ratingMpaDbStorage;
	private final UserDbStorage userDbStorage;
	private final DirectorDbStorage directorDbStorage;
	private Film film;
	private Film secondFilm;
	private Film thirdFilm;
	private Director director1;
	private Director director2;
	private User user;
	private User secondUser;

	@BeforeEach
	void setUp() {
		director1 = Director.builder().name("Tarantino").build();
		director2 = Director.builder().name("Cameron").build();

		directorDbStorage.createDirector(director1);
		directorDbStorage.createDirector(director2);

		film = Film.builder()
				.id(1)
				.name("Test")
				.description("test description")
				.releaseDate(LocalDate.now().minusYears(10))
				.duration(180)
				.genres(new TreeSet<>())
				.mpa(ratingMpaDbStorage.getRatingMpaById(1))
				.directors(Set.of(director1))
				.build();

		secondFilm = Film.builder()
				.id(2)
				.name("Test 2")
				.description("test description 2")
				.releaseDate(LocalDate.now().minusYears(20))
				.duration(180)
				.genres(new TreeSet<>()).mpa(ratingMpaDbStorage.getRatingMpaById(1))
				.directors(Set.of(director1, director2))
				.build();

		thirdFilm = Film.builder()
				.id(3)
				.name("Test 3")
				.description("test description 3")
				.releaseDate(LocalDate.now().minusYears(30))
				.duration(180)
				.genres(new TreeSet<>()).mpa(ratingMpaDbStorage.getRatingMpaById(1))
				.directors(Set.of(director2))
				.build();

		user = User.builder()
				.id(1)
				.name("Eugene")
				.login("Buhanzaz")
				.email("Buhanza21@yandex.ru")
				.birthday(LocalDate.now().minusYears(30))
				.build();

		secondUser = User.builder()
				.id(2)
				.name("Eugene")
				.login("Buhanzaz")
				.email("Buhanza21@gmail.ru")
				.birthday(LocalDate.now().minusYears(30))
				.build();
	}

	@Test
	void getAllFilms_shouldConfirmThatTwoFilmsWasAddedAtList() {
		filmDbStorage.create(film);
		filmDbStorage.create(secondFilm);
		Collection<Film> films = filmDbStorage.getAllFilm();

		assertThat(films).hasSize(2);
	}

	@Test
	void createFilm_shouldConfirmThatFilmIdExists() {
		filmDbStorage.create(film);
		Film filmOptional = filmDbStorage.getById(1);

		assertEquals(filmOptional.getId(), 1);
	}

	@Test
	void getFilmById_shouldConfirmThatFilmIdExists() {
		filmDbStorage.create(film);

		assertEquals(filmDbStorage.getById(1).getId(), film.getId());
	}

	@Test
	public void getAllUsers_shouldConfirmThatTwoUsersWasAddedAtList() {
		userDbStorage.create(user);
		userDbStorage.create(secondUser);
		Collection<User> users = userDbStorage.getAll();

		assertThat(users).contains(user);
		assertThat(users).contains(secondUser);
	}

	@Test
	public void createUser_shouldConfirmThatUserIdExists() {
		userDbStorage.create(user);
		User userOptional = userDbStorage.getById(1);

		assertEquals(userOptional.getId(), 1);
	}

	@Test
	public void getUserById_shouldConfirmThatUserNameExists() {
		userDbStorage.create(user);
		User userOptional = userDbStorage.getById(1);

		assertEquals(userOptional.getName(), "Eugene");
	}

	@Test
	public void deleteUserById_ShouldConfirmThatUsernameHasBeenDeleted() {
		userDbStorage.create(user);
		userDbStorage.delete(user.getId());
		NotFoundException exception = assertThrows(NotFoundException.class, this::execute);

		assertEquals(exception.getMessage(), "User with ID=1 not found!");
	}

	@Test

	public void deleteFilmById_ShouldConfirmThatUsernameHasBeenDeleted() {
		filmDbStorage.create(film);
		filmDbStorage.delete(film.getId());
		NotFoundException exception = assertThrows(NotFoundException.class, () -> filmDbStorage.getById(film.getId()));

		assertEquals(exception.getMessage(), "Movie with ID = 1 not found!");
	}

	private void execute() {
		userDbStorage.getById(user.getId());
	}

	@Test
	public void getAllFilm_shouldReturnFilmsWithDirectors() {
		filmDbStorage.create(film);
		filmDbStorage.create(secondFilm);
		filmDbStorage.create(thirdFilm);

		List<Film> filmsFromDb = new ArrayList<>(filmDbStorage.getAllFilm());
		assertAll("Проверка, что у фильмов из списка есть режиссеры",
				() -> assertEquals(Set.of(director1), filmsFromDb.get(0).getDirectors(),
						"Режиссеры первого фильма не совпадают с ожидаемыми"),
				() -> assertEquals(Set.of(director1, director2), filmsFromDb.get(1).getDirectors(),
						"Режиссеры второго фильма не совпадают с ожидаемыми"),
				() -> assertEquals(Set.of(director2), filmsFromDb.get(2).getDirectors(),
						"Режиссеры третьего фильма не совпадают с ожидаемыми"));
	}

	@Test
	public void create_shouldAddDirectorWhenCreateNewFilm() {
		filmDbStorage.create(film);
		Film filmFromDb = filmDbStorage.getById(1);
		assertEquals(Set.of(director1), filmFromDb.getDirectors(), "Режиссеры фильмов не совпадают");
	}

	@Test
	public void update_shouldUpdateDirectorWhenUpdateFilm() {
		filmDbStorage.create(film);
		film.setDirectors(Set.of(director2));
		filmDbStorage.update(film);
		Film filmFromDb = filmDbStorage.getById(1);
		assertEquals(Set.of(director2), filmFromDb.getDirectors(), "Режиссер фильма не совпадает с ожидаемым");
	}

	@Test
	public void shouldReturnDirectorsFilmsSortedByYear() {
		filmDbStorage.create(film);
		filmDbStorage.create(secondFilm);
		filmDbStorage.create(thirdFilm);
		List<Film> films = filmDbStorage.getDirectorFilmsSortedByYear(1);
		assertAll("Проверка фильмов режиссера, отсортированных по году",
				() -> assertEquals(2, films.size(), "Неверный размер списка фильмов"),
				() -> assertEquals(secondFilm, films.get(0), "Фильм не совпадает с ожидаемым"),
				() -> assertEquals(film, films.get(1), "Фильм не совпадает с ожидаемым"));
	}

	@Test
	public void shouldReturnDirectorsFilmsSortedByLikes() {
		filmDbStorage.create(film);
		filmDbStorage.create(secondFilm);
		filmDbStorage.create(thirdFilm);
		userDbStorage.create(user);
		userDbStorage.create(secondUser);
		filmDbStorage.addLike(1, 1);
		filmDbStorage.addLike(2, 1);
		filmDbStorage.addLike(2, 2);
		filmDbStorage.addLike(3, 1);
		film.addLike(1);
		secondFilm.addLike(1);
		secondFilm.addLike(2);
		thirdFilm.addLike(1);

		List<Film> films = filmDbStorage.getDirectorFilmsSortedByLikes(1);
		assertAll("Проверка фильмов режиссера, отсортированных по количеству лайков",
				() -> assertEquals(2, films.size(), "Неверный размер списка фильмов"),
				() -> assertEquals(secondFilm, films.get(0), "Фильм не совпадает с ожидаемым"),
				() -> assertEquals(film, films.get(1), "Фильм не совпадает с ожидаемым"));
	}
}