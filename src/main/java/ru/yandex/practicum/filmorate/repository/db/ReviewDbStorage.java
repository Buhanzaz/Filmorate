package ru.yandex.practicum.filmorate.repository.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.enums.EventType;
import ru.yandex.practicum.filmorate.repository.enums.Operation;
import ru.yandex.practicum.filmorate.repository.interfaces.ReviewStorage;
import ru.yandex.practicum.filmorate.util.LogEventDbStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final LogEventDbStorage logEventDbStorage;

    @Override
    public Review add(Review review) {
        String insert =
                "insert into reviews(film_id, user_id, content, is_positive) values(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(insert, new String[]{"id"});
                    ps.setLong(1, review.getFilmId());
                    ps.setLong(2, review.getUserId());
                    ps.setString(3, review.getContent());
                    ps.setBoolean(4, review.getIsPositive());

                    return ps;
                },
                keyHolder);

        int reviewId = Objects.requireNonNull(keyHolder.getKey()).intValue();

        review.setReviewId(reviewId);
        review.setUseful(0);
        logEventDbStorage.logging(review.getUserId(), EventType.REVIEW, Operation.ADD, reviewId);

        return review;
    }

    @Override
    public int update(Review review) {
        String update = "update reviews " +
                "set content = ?, is_positive = ? " +
                "where id = ?";
        int userId = getUserIdFromReview(review.getReviewId());

        logEventDbStorage.logging(userId, EventType.REVIEW, Operation.UPDATE, review.getReviewId());
        return jdbcTemplate.update(
                update,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
    }

    private int getUserIdFromReview(Integer id) {
        String sqlQuery = "select USER_ID from REVIEWS where ID = ?";

        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);

            rowSet.next();
            return rowSet.getInt("user_id");
        } catch (NotFoundException e) {
            throw new NotFoundException("Reviews with ID=" + id + " not found!");
        }
    }

    @Override
    public int removeById(Integer id) {
        Review review = findById(id);
        logEventDbStorage.logging(review.getUserId(), EventType.REVIEW, Operation.REMOVE, review.getReviewId());
        return jdbcTemplate.update("delete from reviews where id = ?", id);
    }

    @Override
    public List<Review> findAllByFilmId(Integer id, int count) {
        String select = "select r.id, r.film_id, r.user_id, r.content, r.is_positive, nvl(s.summ, 0) summ " +
                "from reviews r " +
                "left join (select review_id, sum(grade) summ " +
                "from reviews_likes rl " +
                "join reviews rr on rl.review_id = rr.id " +
                "and rr.film_id = ? " +
                "group by review_id) s " +
                "on r.id = s.review_id " +
                "where r.film_id = ? " +
                "order by summ desc " +
                "limit ?";

        return jdbcTemplate.query(
                select,
                new Object[]{id, id, count},
                new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER},
                this::makeReview);
    }

    @Override
    public Review findById(Integer id) throws EmptyResultDataAccessException {
        String select = "select r.id, r.film_id, r.user_id, r.content, r.is_positive, nvl(s.summ, 0) summ " +
                "from reviews r " +
                "left join (select review_id, sum(grade) summ " +
                "from reviews_likes " +
                "where review_id = ? " +
                "group by review_id) s " +
                "on r.id = s.review_id " +
                "where r.id = ?";

        return jdbcTemplate.queryForObject(
                select,
                new Object[]{id, id},
                new int[]{Types.INTEGER, Types.INTEGER},
                this::makeReview);
    }

    @Override
    public Review addLike(Integer id, Integer userId, int grade) {
        String insert = "insert into reviews_likes(review_id, user_id, grade) values(?, ?, ?)";

        jdbcTemplate.update(
                insert,
                id,
                userId,
                grade);
        return findById(id);
    }

    @Override
    public int removeLike(Integer id, Integer userId) {
        String delete = "delete from reviews_likes where review_id = ? and user_id = ?";

        return jdbcTemplate.update(
                delete,
                id,
                userId);
    }

    @Override
    public List<Review> findAll() {
        String select = "select r.id, r.film_id, r.user_id, r.content, r.is_positive, nvl(s.summ, 0) summ " +
                "from reviews r " +
                "left join (select review_id, sum(grade) summ " +
                "from reviews_likes " +
                "group by review_id) s " +
                "on r.id = s.review_id " +
                "order by summ desc";

        return jdbcTemplate.query(select, this::makeReview);
    }

    private Review makeReview(ResultSet rs, int num) throws SQLException {
        return new Review(
                rs.getInt("id"),
                rs.getInt("film_id"),
                rs.getInt("user_id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getInt("summ"));
    }
}
