package seed.controller;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.PUBLIC_MEMBER;
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
import seed.domain.Comment;
import seed.domain.Event;
import seed.domain.Objective;
import seed.domain.User;
import seed.repository.EventRepository;
import seed.repository.ObjectiveRepository;
import seed.repository.UserRepository;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by Froggy on 2017/3/6.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@SpringBootConfiguration
public class ObjectiveControllerTest {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private User user;

    private Objective objective;

    private  Event event1;

    private  Event event2;

    private HashMap<String, Object> sessionAttr;

    @Autowired
    private ObjectiveRepository objectiveRepository;

    @Autowired
    private EventRepository eventRepository;

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

        this.objectiveRepository.deleteAll();
        this.userRepository.deleteAll();

        this.user = userRepository.insert(new User("Tom", "Tom@testUser.com", "123456", false));

        objective = new Objective("tech");
        Event event1 = new Event("drink", new DateTime(2019, 3, 4, 5, 6, 7), true);
        Event event2 = new Event("eat", new DateTime(2019, 3, 4, 6, 6, 7), true);
        objective.setUserId(user.getId());
        objective.setDescription("objectives for tech");
        objective.setDeadline(new DateTime(2019, 2, 1, 8, 2, 0));
        objective.setPriority(3);
        objective.setStatus(true);
        event1.setObjectiveId(objective.getId());
        event2.setObjectiveId(objective.getId());
        List<ObjectId> events = new ArrayList<>();
        events.add(event1.getId());
        events.add(event2.getId());
        objective.setEvents(events);
        this.objective = objectiveRepository.insert(objective);
        this.event1 = eventRepository.insert(event1);
        this.event2 = eventRepository.insert(event2);

        //维持登录态
        sessionAttr = new HashMap<String, Object>();
        sessionAttr.put("userId", user.getId());
    }

    @Test
    public void create() throws Exception {
        objective.setId(null);
        objective.setUserId(null);
        mockMvc.perform(post("/objective")
                .sessionAttrs(sessionAttr)
                .content(this.json(objective))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void remove() throws Exception {
        mockMvc.perform(delete("/objective/" + objective.getId())
                .sessionAttrs(sessionAttr))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void modify() throws Exception {
        objective.setTitle("running");
        objective.setDescription("objectives for health");
        objective.setDeadline(new DateTime(2017, 4, 5, 0, 0, 0));
        objective.setPriority(1);
        objective.setStatus(true);

        mockMvc.perform(put("/objective/" + objective.getId())
                .sessionAttrs(sessionAttr)
                .content(this.json(objective))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getProfile() throws Exception {
        mockMvc.perform(get("/objective/" + objective.getId())
                .sessionAttrs(sessionAttr))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    public void share() throws Exception {
        mockMvc.perform(get("/objective/" + objective.getId() +"/assignment")
                .sessionAttrs(sessionAttr))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void join() throws Exception {
        mockMvc.perform(patch("/objective/" + objective.getId() + "/assignment")
                .sessionAttrs(sessionAttr)
                .content(this.json(objective))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void leave() throws Exception {
        mockMvc.perform(delete("/objective/" + objective.getId() + "/assignment")
                .sessionAttrs(sessionAttr)
                .content(this.json(objective.getId()))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void search() throws Exception {
        mockMvc.perform(get("/objective/search")
                .sessionAttrs(sessionAttr)
                .param("page", "0")
                .param("size", "20")
                .param("sort", "ASC")
                .param("key", "title")
                .param("value", "tech"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void invalidSearch() throws Exception {
        mockMvc.perform(get("/objective/search")
                .sessionAttrs(sessionAttr)
                .param("page", "0")
                .param("size", "20")
                .param("sort", "ASC")
                .param("key", "Seeeeeeed")
                .param("value", "deeeeeees"))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    public void comment() throws Exception {
        Comment comment = new Comment(user.getId(), "comment for my own.");
        mockMvc.perform(post("/objective/" + objective.getId() + "/comment")
                .sessionAttrs(sessionAttr)
                .content(this.json(comment))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void getComments() throws Exception{
        Comment comment1 = new Comment(user.getId(),"The first");
        Comment comment2 = new Comment(user.getId(),"The second");
        comment1.setObjectiveId(objective.getId());
        comment2.setObjectiveId(objective.getId());
        mockMvc.perform(get("/objective/" + objective.getId() + "comment")
                .sessionAttrs(sessionAttr)
                .param("page","0")
                .param("size","7")
                .param("sort","ASC"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteComments() throws Exception{
        Comment comment = new Comment(user.getId(), "GOOD.");
        comment.setObjectiveId(objective.getId());
        mockMvc.perform(delete("/objective/" + objective.getId() + "/comment/" + comment.getId())
                .sessionAttrs(sessionAttr))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void event() throws Exception {
        mockMvc.perform(post("/objective/" + objective.getId() + "/event")
                .sessionAttrs(sessionAttr)
                .content(this.json(event1))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void getEvents() throws Exception{
        mockMvc.perform(get("/objective/" + objective.getId() + "/event")
                .sessionAttrs(sessionAttr)
                .param("page","0")
                .param("size","7")
                .param("sort","ASC"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getEvent() throws Exception{
        mockMvc.perform(get("/objective/" + objective.getId() + "/event/" + event1.getId())
                .sessionAttrs(sessionAttr))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void updateEvent() throws Exception{
        event1.setEvent("Running");
        event1.setEndTime(new DateTime("2017-05-26T11:28:50Z"));
        event1.setStatus(true);

        mockMvc.perform(put("/objective/" + objective.getId() + "/event/" + event1.getId())
                .sessionAttrs(sessionAttr)
                .content(this.json(event1))
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteEvent() throws Exception{
        mockMvc.perform(delete("/objective/" + objective.getId() + "/event/" + event1.getId())
                .sessionAttrs(sessionAttr))
                .andDo(print())
                .andExpect(status().isNoContent());
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
    }

}
