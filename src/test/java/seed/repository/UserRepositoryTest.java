package seed.repository;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import seed.Application;
import seed.domain.User;

import static org.junit.Assert.*;

/**
 * Created by Macsnow on 2017/3/2.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@SpringBootConfiguration

public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private User user1;
    private Pageable pageable;
    private ObjectId id1;

    @Before
    public void setUp(){
        userRepository.deleteAll();
        pageable = new PageRequest(0, 20);
        user1 = new User("Alan","Alan","",false);
        user1.setEmail("Alan.gmail.com");
        id1 = userRepository.insert(user1).getId();

    }

    @Test
    public void read(){
        assertNotNull(userRepository.findById(id1));
        assertNotNull(userRepository.findByEmail(user1.getEmail()));
        assertNotNull(userRepository.findByOpenId(user1.getOpenId()));
        assertNotNull(userRepository.findByUsername(user1.getUsername(),pageable));
        userRepository.deleteAll();
    }

    @Test
    public void update() {
        user1.setEmail("Aria@gmail.com");
        user1.setOpenId("Aria");
        user1.setUsername("Aria");
        userRepository.save(user1);
        assertEquals(userRepository.findById(user1.getId()).get().getId(), userRepository.findByEmail("Aria@gmail.com").get().getId());
        assertEquals(userRepository.findById(user1.getId()).get().getId(), userRepository.findByOpenId("Aria").get().getId());
        userRepository.deleteAll();

    }
}
