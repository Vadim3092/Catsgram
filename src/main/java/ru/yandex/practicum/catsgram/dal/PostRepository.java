package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.PostRowMapper;
import ru.yandex.practicum.catsgram.model.Post;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository extends BaseRepository<Post> {

    private static final String FIND_ALL = "SELECT * FROM posts";
    private static final String FIND_BY_ID = "SELECT * FROM posts WHERE id = ?";
    private static final String FIND_BY_AUTHOR = "SELECT * FROM posts WHERE author_id = ?";
    private static final String INSERT_POST =
            "INSERT INTO posts(author_id, description, post_date) VALUES (?, ?, ?)";
    private static final String UPDATE_POST =
            "UPDATE posts SET description = ? WHERE id = ?";
    private static final String DELETE_POST = "DELETE FROM posts WHERE id = ?";

    public PostRepository(JdbcTemplate jdbc, PostRowMapper mapper) {
        super(jdbc, mapper);
    }

    public List<Post> findAll() {
        return findMany(FIND_ALL);
    }

    public Optional<Post> findById(long id) {
        return findOne(FIND_BY_ID, id);
    }

    public List<Post> findByAuthorId(long authorId) {
        return findMany(FIND_BY_AUTHOR, authorId);
    }

    public Post save(Post post) {
        long id = insert(INSERT_POST,
                post.getAuthorId(),
                post.getDescription(),
                Timestamp.from(post.getPostDate())
        );
        post.setId(id);
        return post;
    }

    public Post update(Post post) {
        update(UPDATE_POST, post.getDescription(), post.getId());
        return post;
    }

    public boolean delete(long id) {
        return delete(DELETE_POST, id);
    }
}