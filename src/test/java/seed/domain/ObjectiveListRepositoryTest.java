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

    @Before
    public void setUp() {
        pageable = new PageRequest(1, 2, new Sort(Sort.Direction.DESC, "id"));
        objectiveList1 = new ObjectiveList("tech");
        objectiveList2 = new ObjectiveList("health");
        objectiveList3 = new ObjectiveList("tech");
        objectiveList1.setDescription("objective for tech");
        objectiveList2.setDescription("objective for health");
        objectiveList3.setDescription("another objective for tech");
        id1 = objectiveListRepository.insert(objectiveList1).getId();
    }

    @Test
    public void read() {
        assertNotNull(objectiveListRepository.findById(id1).getId());
        assertTrue(objectiveListRepository.findByTitleIgnoreCase("tech", pageable).size() == 2);
        assertFalse(objectiveListRepository.findByTitleIgnoreCase("tech", pageable).get(0).getId() ==
                      objectiveListRepository.findByTitleIgnoreCase("tech", pageable).get(1).getId());
    }



}
