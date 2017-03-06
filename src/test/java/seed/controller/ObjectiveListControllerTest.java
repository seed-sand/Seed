package seed.controller;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import seed.Application;
import seed.domain.Objective;
import seed.domain.ObjectiveList;
import seed.domain.User;
import seed.repository.ObjectiveListRepository;
import seed.repository.ObjectiveRepository;
import seed.repository.UserRepository;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by Macsnow on 2017/3/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@SpringBootConfiguration
public class ObjectiveListControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private ObjectiveList objectiveList;

    private User user;

    private Objective objective1, objective2;

    private HashMap<String, Object> sessionAttr;

    @Autowired
    private ObjectiveListRepository objectiveListRepository;

    @Autowired
    private ObjectiveRepository objectiveRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.objectiveListRepository.deleteAll();
        this.objectiveRepository.deleteAll();
        this.userRepository.deleteAll();

        this.user = userRepository.insert(new User("Tom", "Tom@testUser.com", "123456", false));

        objectiveList = new ObjectiveList("tech");
        objectiveList.setUserId(user.getId());
        this.objectiveList = objectiveListRepository.insert(objectiveList);

        this.objective1 = objectiveRepository.insert(new Objective("drink"));
        this.objective2 = objectiveRepository.insert(new Objective("eat"));

        sessionAttr = new HashMap<String, Object>();
        sessionAttr.put("userId", user.getId());
    }

    @Test
    public void create() throws Exception {
        objectiveList.setId(null);
        objectiveList.setUserId(null);
        mockMvc.perform(post("/objectiveList")
                .sessionAttrs(sessionAttr)
                .content(this.json(objectiveList))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void update() throws Exception {
        objectiveList.setTitle("health");
        objectiveList.setDescription("objectives for health");
        List<ObjectId> objectives = Optional.ofNullable(objectiveList.getObjectives()).orElse(new ArrayList<>());
        objectives.add(objective1.getId());
        objectives.add(objective2.getId());
        objectiveList.setObjectives(objectives);
        mockMvc.perform(put("/objectiveList/" + objectiveList.getId())
                .sessionAttrs(sessionAttr)
                .content(this.json(objectiveList))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getProfile() throws Exception {
        mockMvc.perform(get("/objectiveList/" + objectiveList.getId())
                .sessionAttrs(sessionAttr))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void remove() throws Exception {
        mockMvc.perform(delete("/objectiveList/" + objectiveList.getId())
                .sessionAttrs(sessionAttr))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void getObjectiveLists() throws Exception {
        mockMvc.perform(get("/objectiveList")
                .sessionAttrs(sessionAttr))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void pushObjective() throws Exception {
        mockMvc.perform(patch("/objectiveList/" + objectiveList.getId() + "/objective")
                .sessionAttrs(sessionAttr)
                .content(json(objective1))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void popObjective() throws Exception {
        mockMvc.perform(delete("/objectiveList/" + objectiveList.getId() + "/objective")
                .sessionAttrs(sessionAttr)
                .content(json(objective1.getId()))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk());
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @After
    public void tearDown() {
        this.userRepository.deleteAll();
        this.objectiveRepository.deleteAll();
        this.objectiveListRepository.deleteAll();
    }
}
