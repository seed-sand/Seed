package seed.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import seed.domain.User;

import java.awt.print.Pageable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by Macsnow on 2017/3/1.
 */

public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findById(ObjectId id);
    Optional<User> findByEmail(String email);
    Optional<User> findByOpenId(String OpenId);
    Collection<User> findByUsername(String Username, Pageable pageable);
}
