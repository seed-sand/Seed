package seed.repository;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import seed.domain.ObjectiveList;

import java.util.List;

/**
 * Created by Froggy
 * 2017-03-01.
 */
public interface ObjectiveListRepository extends MongoRepository<ObjectiveList,ObjectId>{
    ObjectiveList findById(ObjectId id);
    List<ObjectiveList> findByUserId(ObjectiveList userId, Pageable pageable);
    List<ObjectiveList> findByTitleIgnoreCase(String title, Pageable pageable);
}
