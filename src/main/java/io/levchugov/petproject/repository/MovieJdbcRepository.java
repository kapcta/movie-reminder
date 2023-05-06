package io.levchugov.petproject.repository;

import io.levchugov.petproject.model.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MovieJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Integer findById(String id) {
        var query = "select count(*) from movies where movies.id = :id";
        return jdbcTemplate.queryForObject(
                query,
                new MapSqlParameterSource("id", id),
                Integer.class
        );
    }

    public void save(Movie movie) {
        var saveQuery =
                "insert into movies (id, title, image, description) " +
                        "values (:id, :title, :image, :description)";
        var namedParameters = new MapSqlParameterSource(
                Map.of(
                        "id", movie.id(),
                        "title", movie.title(),
                        "image", movie.image(),
                        "description", movie.description()
                )
        );
        jdbcTemplate.update(saveQuery, namedParameters);
    }

    public Integer count(Long chatId, String movieId) {
        String select =
                "select count(*) from chat_movie_list " +
                        "where chat_movie_list.chat_id = :chat_id " +
                        "and chat_movie_list.movie_id = :movie_id";

        var namedParameters = new MapSqlParameterSource(
                Map.of(
                        "chat_id", chatId,
                        "movie_id", movieId
                )
        );
        return jdbcTemplate.queryForObject(select, namedParameters, Integer.class);
    }

    public void saveMovieToWatchList(Long chatId, String movieId) {
        String saveQuery =
                "insert into chat_movie_list (chat_id, movie_id)" +
                        " values (:chat_id, :movie_id)";

        var namedParameters = new MapSqlParameterSource(
                Map.of(
                        "chat_id", chatId,
                        "movie_id", movieId
                )
        );

        jdbcTemplate.update(saveQuery, namedParameters);
    }

    public List<Movie> findUsersListToWatchByChatId(Long chatId) {
        String query = "select * from movies " +
                "left join chat_movie_list on movies.id = chat_movie_list.movie_id" +
                " where chat_movie_list.chat_id = :chat_id" +
                " and chat_movie_list.recently_picked = false";

        return jdbcTemplate.query(
                query,
                new MapSqlParameterSource("chat_id", chatId),
                (rs, rowNum) ->
                        new Movie(
                                rs.getString("id"),
                                rs.getString("title"),
                                rs.getString("image"),
                                rs.getString("description")
                        ));

    }

    public void markMoviePicked(Long chatId, String movieId) {
        String query = "update chat_movie_list " +
                "set recently_picked = true " +
                "where chat_movie_list.chat_id = :chat_id " +
                "and chat_movie_list.movie_id = :movie_id";

        var namedParameters = new MapSqlParameterSource(
                Map.of(
                        "chat_id", chatId,
                        "movie_id", movieId
                )
        );

        jdbcTemplate.update(query, namedParameters);
    }

    public void markAllMovieNotPicked(Long chatId) {
        String query = "update chat_movie_list" +
                " set recently_picked = false" +
                " where chat_movie_list.chat_id = :chat_id";

        jdbcTemplate.update(
                query,
                new MapSqlParameterSource("chat_id", chatId)
        );
    }
}