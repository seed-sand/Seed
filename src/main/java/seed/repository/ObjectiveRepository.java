package seed.repository;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
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
    List<Objective> findByTitleIgnoreCase(String title);
    List<Objective> findByDeadline(DateTime deadline);
    List<Objective> findByPriority(int priority);
    List<Objective> findByScope(String scope);
    List<Objective> findByStatus(boolean status);

}
