package seed.repository;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import seed.domain.Event;
import seed.domain.Objective;

import java.util.List;
import java.util.Optional;

/**
 * Created by Macsnow on 2017/3/9.
 */
public interface EventRepository extends MongoRepository<Event, ObjectId> {
    Optional<Event> findById(ObjectId id);
    Optional<Event> findByIdAndObjectiveId(ObjectId id, ObjectId objectiveId);
    List<Event> findByObjectiveId(ObjectId objectiveId, Pageable pageable);
    List<Event> findByObjectiveIdAndEndTime(ObjectId objectiveId, DateTime dateTime, Pageable pageable);
    List<Event> findByObjectiveIdAndStatus(ObjectId objectiveId, boolean status, Pageable pageable);
}
