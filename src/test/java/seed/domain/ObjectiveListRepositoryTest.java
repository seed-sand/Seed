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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import seed.Application;
import seed.repository.ObjectiveListRepository;

import static org.junit.Assert.*;

/**
 * Created by Macsnow on 2017/3/2.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@SpringBootConfiguration
public class ObjectiveListRepositoryTest {

    @Autowired
    private ObjectiveListRepository objectiveListRepository;

    private ObjectiveList objectiveList1;
    private ObjectiveList objectiveList2;
    private ObjectiveList objectiveList3;
    private Pageable pageable;
    private ObjectId id1;
    private ObjectId id2;
    private ObjectId id3;

    @Before
    public void setUp() {
        pageable = new PageRequest(0, 20);
        objectiveList1 = new ObjectiveList("tech");
        objectiveList2 = new ObjectiveList("health");
        objectiveList3 = new ObjectiveList("tech");
        objectiveList1.setDescription("objective for tech");
        objectiveList2.setDescription("objective for health");
        objectiveList3.setDescription("another objective for tech");
        id1 = objectiveListRepository.insert(objectiveList1).getId();
        id2 = objectiveListRepository.insert(objectiveList2).getId();
        id3 = objectiveListRepository.insert(objectiveList3).getId();
    }


    @Test
    public void read() {
        assertNotNull(objectiveListRepository.findById(id1));
        System.out.print(objectiveListRepository.findByTitleIgnoreCase("tech", pageable).size());
        assertTrue(objectiveListRepository.findByTitleIgnoreCase("tech", pageable).size() > 1);
        assertFalse(objectiveListRepository.findByTitleIgnoreCase("tech", pageable).get(0).getId() ==
                      objectiveListRepository.findByTitleIgnoreCase("tech", pageable).get(1).getId());
        objectiveListRepository.delete(id1);
        objectiveListRepository.delete(id2);
        objectiveListRepository.delete(id3);
    }

    @Test
    public void update() {
        ObjectiveList objectiveList = objectiveListRepository.findById(id1).get();
        objectiveList.setTitle("life");
        objectiveListRepository.save(objectiveList);
        assertEquals("life", objectiveListRepository.findById(id1).get().getTitle());
        objectiveListRepository.delete(id1);
        objectiveListRepository.delete(id2);
        objectiveListRepository.delete(id3);
    }


}
