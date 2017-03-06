package seed.domain;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
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

    private HashMap<String, Object> sessionAttr;

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

        this.objectiveRepository.deleteAll();
        this.userRepository.deleteAll();

        this.user = userRepository.insert(new User("Tom", "Tom@testUser.com", "123456", false));

        objective = new Objective("test");
        objective.setUserId(user.getId());
        this.objective = objectiveRepository.insert(objective);

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
                .andExpect(status().isCreated());
    }

    @Test
    public void remove() throws Exception {
        mockMvc.perform(delete("/objective/" + objective.getId())
                .sessionAttrs(sessionAttr)
                .contentType(contentType))
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
                .sessionAttrs(sessionAttr)
                .contentType(contentType))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    public void share() throws Exception {
        mockMvc.perform(get("/objective/" + objective.getId() +"/assignment")
                .sessionAttrs(sessionAttr)
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void join() throws Exception {
        mockMvc.perform(patch("/objective/" + objective.getId() + "/assignment")
                .sessionAttrs(sessionAttr)
                .content(json(objective))
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void leave() throws Exception {
        mockMvc.perform(delete("/objective/" + objective.getId() + "/assignment")
                .sessionAttrs(sessionAttr)
                .content(json(objective.getId()))
                .contentType(contentType))
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
    }

}
