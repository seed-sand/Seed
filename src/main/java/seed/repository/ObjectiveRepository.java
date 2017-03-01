package seed.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import seed.domain.Objective;
import seed.domain.User;

import java.util.List;

/**
 * Created by Froggy
 * 2017-03-01.
 */
public interface ObjectiveRepository extends MongoRepository<Objective,ObjectId> {
    Objective findById(ObjectId id);
    List<Objective> findByUserId(ObjectId userId);
    List<Objective> findByGroupId(ObjectId groupId);
    List<Objective> findByTitleIgnoreCase(ObjectId title);
    List<Objective> findByDeadline(ObjectId deadline);
    List<Objective> findByPriority(ObjectId priority);
    List<Objective> findByScope(ObjectId scope);
    List<Objective> findByStatus(ObjectId status);

}
