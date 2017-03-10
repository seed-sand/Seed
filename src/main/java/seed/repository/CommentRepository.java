package seed.repository;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import seed.domain.Comment;

import java.util.List;
import java.util.Optional;

/**
 * Created by Macsnow on 2017/3/9.
 */
public interface CommentRepository extends MongoRepository<Comment, ObjectId>{
    Optional<Comment> findById(ObjectId id);
    List<Comment> findByUserId(ObjectId userId, Pageable pageable);
    List<Comment> findByObjectiveId(ObjectId objectiveId, Pageable pageable);
    List<Comment> findByObjectiveIdAndUserId(ObjectId objectiveId, ObjectId userId, Pageable pageable);
}
