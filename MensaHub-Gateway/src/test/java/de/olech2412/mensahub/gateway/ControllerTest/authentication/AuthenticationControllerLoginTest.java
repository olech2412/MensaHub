package de.olech2412.mensahub.gateway.ControllerTest.authentication;

import de.mensahub.gateway.JPA.repository.API_UserRepository;
import de.mensahub.gateway.security.JWTTokenProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerLoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    API_UserRepository apiUserRepository;

    @Autowired
    JWTTokenProvider jwtTokenProvider;

    String validCredentials = "{" +
            "    \"apiUsername\": \"controllerTestUser\",\n" +
            "    \"password\": \"1345t3456543754\"\n" +
            "}";
    String invalidCredentials = "{" +
            "    \"apiUsername\": \"controllerTestUser1\",\n" +
            "    \"password\": \"1345t3456543754\"\n" +
            "}";

    @Test
    public void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(apiUserRepository).isNotNull();
    }

    @Test
    public void controllerShouldAllowLogin() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(validCredentials))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @Test
    public void afterLoginUserShouldBeAuthorized() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(validCredentials))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andReturn();

        String jwtToken = mvcResult.getResponse().getContentAsString();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/" + "mensa_am_park" + "/getMeals/from/2001-01-01/to/2001-03-03").header(HttpHeaders.AUTHORIZATION,"Bearer " + jwtToken)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].servingDate", is("2001-01-01")))
                .andExpect(jsonPath("$[1].servingDate", is("2001-02-02")))
                .andExpect(jsonPath("$[2].servingDate", is("2001-03-03")));
    }

    @Test
    public void successfulLoginShouldReturnValidJWTToken() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(validCredentials))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andReturn();

        String jwtToken = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(jwtToken.startsWith("eyJhb"));
        Assertions.assertEquals("controllerTestUser", jwtTokenProvider.getUserDataFromToken(jwtToken));
        Assertions.assertTrue(jwtTokenProvider.validateToken(jwtToken));
    }

    @Test
    public void controllerShouldAllowOnlyValidCredentials() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(invalidCredentials))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));
    }

    @Test
    public void afterFailedLoginUserShouldBeUnauthorized() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(invalidCredentials))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();

        String jwtToken = mvcResult.getResponse().getContentAsString();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/" + "mensa_am_park" + "/getMeals/from/2001-01-01/to/2001-03-03").header(HttpHeaders.AUTHORIZATION,"Bearer " + jwtToken)).andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));
    }

}
