package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.ImageRowMapper;
import ru.yandex.practicum.catsgram.model.Image;

import java.util.List;
import java.util.Optional;

@Repository
public class ImageRepository extends BaseRepository<Image> {

    private static final String FIND_BY_POST_ID = "SELECT * FROM image_storage WHERE post_id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM image_storage WHERE id = ?";
    private static final String INSERT_IMAGE =
            "INSERT INTO image_storage(original_name, file_path, post_id) VALUES (?, ?, ?)";
    private static final String DELETE_IMAGE = "DELETE FROM image_storage WHERE id = ?";

    public ImageRepository(JdbcTemplate jdbc, ImageRowMapper mapper) {
        super(jdbc, mapper);
    }

    public List<Image> findByPostId(long postId) {
        return findMany(FIND_BY_POST_ID, postId);
    }

    public Optional<Image> findById(long id) {
        return findOne(FIND_BY_ID, id);
    }

    public Image save(Image image) {
        long id = insert(INSERT_IMAGE,
                image.getOriginalFileName(),
                image.getFilePath(),
                image.getPostId()
        );
        image.setId(id);
        return image;
    }

    public boolean delete(long id) {
        return delete(DELETE_IMAGE, id);
    }
}
