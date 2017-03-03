package seed.domain;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import seed.Application;
import seed.repository.ObjectiveListRepository;
import seed.repository.UserRepository;

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
    private User user2;
    private User user3;
    private Pageable pageable;
    private ObjectId id1;
    private ObjectId id2;
    private ObjectId id3;

    @Before
    public void setUp(){
        pageable = new PageRequest(0, 20);
        user1 = new User("Alan","Alan","",true);
        user1.setEmail("Alan@gmail.com");
        user2 = new User();
        user3 = new User();
        id1 = userRepository.insert(user1).getId();
        id2 = userRepository.insert(user2).getId();
        id3 = userRepository.insert(user3).getId();

    }

    @Test
    public void read(){
        assertNotNull(userRepository.findById(id1));
        assertNotNull(userRepository.findByEmail(user1.getEmail()));
        assertNotNull(userRepository.findByOpenId(user1.getOpenId()));
        assertNotNull(userRepository.findByUsername(user1.getUsername(),pageable));
        userRepository.delete(id1);
        userRepository.delete(id2);
        userRepository.delete(id3);



    }

    @Test
    public void update() {
        user1.setId(id2);
        assertEquals(id2,userRepository.findById(user1.getId()));
        user1.setEmail("Aria@gmail.com");
        assertEquals("Aria@gmail.com", userRepository.findByEmail(user1.getEmail()));
        user1.setOpenId("Aria");
        assertEquals("Aria", userRepository.findByOpenId(user1.getOpenId()));
        user1.setUsername("Aria");
        assertEquals("Aria", userRepository.findByUsername(user1.getUsername(),pageable));
        userRepository.delete(id1);
        userRepository.delete(id2);
        userRepository.delete(id3);

    }







}
