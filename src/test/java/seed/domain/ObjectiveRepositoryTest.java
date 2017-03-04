package seed.domain;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
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
    private Pageable pageable;
    private ObjectId id1;
    private ObjectId id2;
    private ObjectId id3;
    private ObjectId id4;
    private ObjectId id5;
    private ObjectId id6;
    private DateTime id7;
    private DateTime id8;


    @Before
    public void setUp() {
        objectiveRepository.deleteAll();
        pageable = new PageRequest(0, 20);
        objective1 = new Objective("Alan");
        objective2 = new Objective();
        objective3 = new Objective();
        id1 = objectiveRepository.insert(objective1).getId();
        id2 = objectiveRepository.insert(objective2).getId();
        id3 = objectiveRepository.insert(objective1).getUserId();
        id4 = objectiveRepository.insert(objective2).getUserId();
        id5 = objectiveRepository.insert(objective1).getListIdId();
        id6 = objectiveRepository.insert(objective2).getListIdId();
        id7 = objectiveRepository.insert(objective1).getDeadline();
        id8 = objectiveRepository.insert(objective2).getDeadline();
        objective1.setPriority(1);
        objective1.setStatus(true);


    }
    @Test
    public void read(){
        assertNotNull(objectiveRepository.findById(id2));
//        assertNotNull(objectiveRepository.findByUserId(id3,pageable));
//        assertNotNull(objectiveRepository.findByListId(id5,pageable));
//        assertNotNull(objectiveRepository.findByTitleIgnoreCase(objective1.getTitle(),pageable));
//        assertNotNull(objectiveRepository.findByDeadline(id7,pageable));
//        assertNotNull(objectiveRepository.findByPriority(objective1.getPriority(),pageable));
//        assertNotNull(objectiveRepository.findByStatus(objective1.getStatus(),pageable));
//        objectiveRepository.deleteAll();


    }

//    @Test
//    public void update(){
////        objective1.setId(id2);
////        assertEquals(id2,objectiveRepository.findById(objective1.getId()));
////        objective1.setUserId(id4);
////        assertEquals(id4,objectiveRepository.findByUserId(objective1.getUserId(),pageable));
////        objective1.setListId(id6);
////        assertEquals(id6,objectiveRepository.findByListId(objective1.getListIdId(),pageable));
////        objective1.setTitle("Aria");
////        assertEquals("Aria",objectiveRepository.findByTitleIgnoreCase(objective1.getTitle(),pageable));
////        objective1.setDeadline(id8);
////        assertEquals(id8,objectiveRepository.findByDeadline(objective1.getDeadline(),pageable));
////        objective1.setPriority(2);
////        assertEquals(2,objectiveRepository.findByPriority(objective1.getPriority(),pageable));
////        objective1.setStatus(false);
////        assertEquals(false,objectiveRepository.findByStatus(objective1.getStatus(),pageable));
////        objectiveRepository.deleteAll();
//
//
//
//
//
//
//    }
}
