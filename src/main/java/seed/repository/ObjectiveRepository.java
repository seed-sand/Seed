package seed.repository;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import seed.domain.Objective;

import java.util.List;
import java.util.Optional;

/**
 * Created by Froggy
 * 2017-03-01.
 */
public interface ObjectiveRepository extends MongoRepository<Objective,ObjectId> {
    Optional<Objective> findById(ObjectId id);
    List<Objective> findByUserId(ObjectId userId, Pageable pageable);
    List<Objective> findByListId(ObjectId listId, Pageable pageable);
    List<Objective> findByTitleIgnoreCase(String title, Pageable pageable);
    List<Objective> findByDeadline(DateTime deadline, Pageable pageable);
    List<Objective> findByPriority(int priority, Pageable pageable);
    List<Objective> findByStatus(boolean status, Pageable pageable);

}
