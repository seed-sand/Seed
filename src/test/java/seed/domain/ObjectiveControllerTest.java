package seed.domain;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import seed.Application;
import seed.repository.UserRepository;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by kagam on 2017/3/4.
 */
public class ObjectiveControllerTest {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Objective objective;

    @Autowired
    private UserRepository objectiveRepository;

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

//        this.objective = objectiveRepository.insert(new Objective("Tom"));
    }

    @Test
    public void creat() throws Exception {
        this.objectiveRepository.deleteAll();
        objective.setId(null);
        mockMvc.perform(post("/user")
                .content(this.json(objective))
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void login() throws Exception {
        AuthCert authCert = new AuthCert();
        authCert.email = "Tom@testUser.com";
        authCert.password = "123456";
        authCert.useWechat = false;

        mockMvc.perform(post("/user/log-in")
                .content(this.json(authCert))
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void getProfileWithoutLogin() throws Exception {
        mockMvc.perform(get("/user/profile")
                .contentType(contentType))
                .andExpect(status().isUnauthorized());
    }


    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @After
    public void tearDown() {
        this.objectiveRepository.deleteAll();
    }

}
