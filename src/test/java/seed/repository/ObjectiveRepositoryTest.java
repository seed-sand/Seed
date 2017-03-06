package seed.repository;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.After;
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
import seed.domain.Objective;
import seed.repository.ObjectiveListRepository;
import seed.repository.ObjectiveRepository;
import seed.repository.UserRepository;

import java.time.DateTimeException;

import static org.junit.Assert.*;

/**
 * Created by Macsnow on 2017/3/2.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@SpringBootConfiguration


public class ObjectiveRepositoryTest {
    @Autowired
    private ObjectiveRepository objectiveRepository;
    private Objective objective1;
    private Objective objective2;
    private Objective objective3;
    private Objective objective4;
    private Pageable pageable;
    private ObjectId id1;
    private ObjectId id2;
    private ObjectId id3;
    private DateTime id4;

    @Before
    public void setUp() {
        objectiveRepository.deleteAll();
        pageable = new PageRequest(0, 20);
        objective1 = new Objective("Alan");
        objective2 = new Objective("Aria");
        objective3 = new Objective("Kinji");
        objective4 = new Objective("Riko");
        id1 = objectiveRepository.insert(objective1).getId();
        id2 = objectiveRepository.insert(objective2).getUserId();
        id3 = objectiveRepository.insert(objective3).getListIdId();
        id4 = objectiveRepository.insert(objective4).getDeadline();
        objective1.setPriority(1);
        objective1.setStatus(true);


    }
    @Test
    public void read(){
        assertNotNull(objectiveRepository.findById(id1));
        assertNotNull(objectiveRepository.findByUserId(id2,pageable));
        assertNotNull(objectiveRepository.findByListId(id3,pageable));
        assertNotNull(objectiveRepository.findByTitleIgnoreCase(objective1.getTitle(),pageable));
        assertNotNull(objectiveRepository.findByDeadline(id4,pageable));
        assertNotNull(objectiveRepository.findByPriority(objective1.getPriority(),pageable));
        assertNotNull(objectiveRepository.findByStatus(objective1.getStatus(),pageable));
    }

    @Test
    public void update(){
        objective1.setTitle("Aria");
        objective1.setPriority(2);
        objective1.setStatus(false);
        objectiveRepository.save(objective1);
        assertEquals(objectiveRepository.findById(id1).get().getId(),objectiveRepository.findByTitleIgnoreCase("Aria",pageable).get(0).getId());
        assertEquals(objectiveRepository.findById(id1).get().getId(),objectiveRepository.findByPriority(2,pageable).get(0).getId());
        assertEquals(objectiveRepository.findById(id1).get().getId(),objectiveRepository.findByStatus(false,pageable).get(0).getId());
    }

    @After
    public void tearDown() {
        objectiveRepository.deleteAll();
    }
}
