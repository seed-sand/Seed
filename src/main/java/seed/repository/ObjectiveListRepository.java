package seed.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import seed.domain.Objective;

import java.awt.print.Pageable;
import java.util.List;

/**
 * Created by Froggy
 * 2017-03-01.
 */
public interface ObjectiveListRepository extends MongoRepository<Objective,ObjectId>{
    Objective findById(ObjectId id);
    List<Objective> findByUserId(Objective userId, Pageable pageable);
    List<Objective> findByTitleIgnoreCase(String title, Pageable pageable);
}
