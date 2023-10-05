package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.repository.db.RatingMpaDbStorage;
import ru.yandex.practicum.filmorate.repository.db.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class FilmorateApplicationTests {
	private final FilmDbStorage filmDbStorage;
	private final RatingMpaDbStorage ratingMpaDbStorage;
	private final UserDbStorage userDbStorage;
	private Film film;
	private Film secondFilm;
	private User user;
	private User secondUser;

	@BeforeEach
	void setUp() {

		film = Film.builder()
				.id(1)
				.name("Test")
				.description("test description")
				.releaseDate(LocalDate.now().minusYears(40))
				.duration(180)
				.genres(new HashSet<>())
				.mpa(ratingMpaDbStorage.getRatingMpaById(1))
				.build();

		secondFilm = Film.builder()
				.id(1)
				.name("Test 2")
				.description("test description 2")
				.releaseDate(LocalDate.now().minusYears(40))
				.duration(180)
				.genres(new HashSet<>()).mpa(ratingMpaDbStorage.getRatingMpaById(1))
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

		assertEquals(filmDbStorage.getById(1).getId(),film.getId());
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
		NotFoundException exception = assertThrows(NotFoundException.class, () -> {
			userDbStorage.getById(user.getId());
		});

		assertEquals(exception.getMessage(), "User with ID=1 not found!");
	}

	@Test
	public void deleteFilmById_ShouldConfirmThatUsernameHasBeenDeleted() {
		filmDbStorage.create(film);
		filmDbStorage.delete(film.getId());
		NotFoundException exception = assertThrows(NotFoundException.class, () -> {
			filmDbStorage.getById(film.getId());
		});

		assertEquals(exception.getMessage(), "Movie with ID = 1 not found!");
	}
}
