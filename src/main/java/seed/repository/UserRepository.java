package seed.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import seed.domain.User;

import java.awt.print.Pageable;
import java.util.List;

/**
 * Created by Macsnow on 2017/3/1.
 */

public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findById(ObjectId id);
    List<User> findByUsername(String Username, Pageable pageable);
}
