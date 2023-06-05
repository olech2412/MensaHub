package com.essensGetter.api.APINavigation.ControllerTest.authentication;

import com.essensGetter.api.JPA.repository.API_UserRepository;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTestRegisterUserTest {

    @Autowired
    private MockMvc mockMvc;
    String userName = RandomString.make(new Random().nextInt(100 - 2 + 1) + 2);
    String password = RandomString.make(new Random().nextInt(255 - 8 + 1) + 8);
    String description = RandomString.make(new Random().nextInt(255 - 10 + 1) + 10);
    String email = "test.email@test.de";
    String enabledByAdmin = "false";
    String verified_email = "false";
    String registerPath = "/auth/register";
    String invalidUsername = "k";
    String invalidPassword = RandomString.make(new Random().nextInt(7 - 1 + 1) + 1);
    String invalidDescription = RandomString.make(new Random().nextInt(9 - 1 + 1) + 1);
    String invalidEmail = RandomString.make(new Random().nextInt(255 - 1 + 1) + 1);

    String jsonDataRegisterValid = "{\n" +
            "      \"apiUsername\": \"" + userName + "\",\n" +
            "      \"password\": \"" + password + "\",\n" +
            "      \"description\": \"" + description + "\",\n" +
            "      \"email\": \"" + email + "\",\n" +
            "      \"enabledByAdmin\":" + enabledByAdmin + ",\n" +
            "      \"verified_email\":" + verified_email + "\n" +
            "    }";

    String jsonDataRegisterInvalidUsername = "{\n" +
            "      \"apiUsername\": \"" + invalidUsername + "\",\n" +
            "      \"password\": \"" + password + "\",\n" +
            "      \"description\": \"" + description + "\",\n" +
            "      \"email\": \"" + email + "\",\n" +
            "      \"enabledByAdmin\":" + enabledByAdmin + ",\n" +
            "      \"verified_email\":" + verified_email + "\n" +
            "    }";

    String jsonDataRegisterInvalidPassword = "{\n" +
            "      \"apiUsername\": \"" + userName + "\",\n" +
            "      \"password\": \"" + invalidPassword + "\",\n" +
            "      \"description\": \"" + description + "\",\n" +
            "      \"email\": \"" + email + "\",\n" +
            "      \"enabledByAdmin\":" + enabledByAdmin + ",\n" +
            "      \"verified_email\":" + verified_email + "\n" +
            "    }";

    String jsonDataRegisterInvalidDescription = "{\n" +
            "      \"apiUsername\": \"" + userName + "\",\n" +
            "      \"password\": \"" + password + "\",\n" +
            "      \"description\": \"" + invalidDescription + "\",\n" +
            "      \"email\": \"" + email + "\",\n" +
            "      \"enabledByAdmin\":" + enabledByAdmin + ",\n" +
            "      \"verified_email\":" + verified_email + "\n" +
            "    }";

    String jsonDataRegisterInvalidEmail = "{\n" +
            "      \"apiUsername\": \"" + userName + "\",\n" +
            "      \"password\": \"" + password + "\",\n" +
            "      \"description\": \"" + description + "\",\n" +
            "      \"email\": \"" + invalidEmail + "\",\n" +
            "      \"enabledByAdmin\":" + enabledByAdmin + ",\n" +
            "      \"verified_email\":" + verified_email + "\n" +
            "    }";

    @Autowired
    API_UserRepository apiUserRepository;

    @Test
    public void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(apiUserRepository).isNotNull();
    }

    @Test
    @Timeout(2)
    public void controllerShouldRegisterUserForAPI() throws Exception {
        this.mockMvc.perform(post(registerPath).contentType(MediaType.APPLICATION_JSON).content(jsonDataRegisterValid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.apiUsername").value(userName))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.creationDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.blockingReason").value(nullValue()))
                .andExpect(jsonPath("$", aMapWithSize(8)))
                .andReturn();

        Assertions.assertTrue(apiUserRepository.findAPI_UserByApiUsername(userName).isPresent());
        apiUserRepository.delete(apiUserRepository.findAPI_UserByApiUsername(userName).get());
        Assertions.assertFalse(apiUserRepository.findAPI_UserByApiUsername(userName).isPresent());
    }

    @Test
    @Timeout(2)
    public void controllerShouldntRegisterInvalidUsername() throws Exception {
        this.mockMvc.perform(post(registerPath).contentType(MediaType.APPLICATION_JSON).content(jsonDataRegisterInvalidUsername))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        Assertions.assertFalse(apiUserRepository.findAPI_UserByApiUsername(jsonDataRegisterInvalidUsername).isPresent());
    }

    @Test
    @Timeout(2)
    public void controllerShouldntRegisterInvalidPassword() throws Exception {
        this.mockMvc.perform(post(registerPath).contentType(MediaType.APPLICATION_JSON).content(jsonDataRegisterInvalidPassword))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        Assertions.assertFalse(apiUserRepository.findAPI_UserByApiUsername(jsonDataRegisterInvalidUsername).isPresent());
    }

    @Test
    @Timeout(2)
    public void controllerShouldntRegisterInvalidDescription() throws Exception {
        this.mockMvc.perform(post(registerPath).contentType(MediaType.APPLICATION_JSON).content(jsonDataRegisterInvalidDescription))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        Assertions.assertFalse(apiUserRepository.findAPI_UserByApiUsername(jsonDataRegisterInvalidUsername).isPresent());
    }

    @Test
    @Timeout(2)
    public void controllerShouldntRegisterInvalidEmail() throws Exception {
        this.mockMvc.perform(post(registerPath).contentType(MediaType.APPLICATION_JSON).content(jsonDataRegisterInvalidEmail))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        Assertions.assertFalse(apiUserRepository.findAPI_UserByApiUsername(jsonDataRegisterInvalidUsername).isPresent());
    }

    @Test
    @Timeout(2)
    public void controllerShouldntRegisterWithoutParameter() throws Exception {
        String jsonDataWithoutRandomParameter = jsonDataWithoutRandomParameter();
        this.mockMvc.perform(post(registerPath).contentType(MediaType.APPLICATION_JSON).content(jsonDataWithoutRandomParameter))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        Assertions.assertFalse(apiUserRepository.findAPI_UserByApiUsername(jsonDataRegisterInvalidUsername).isPresent());
    }

    @Test
    @Timeout(2)
    public void controllerShouldDecryptPassword() throws Exception {
        this.mockMvc.perform(post(registerPath).contentType(MediaType.APPLICATION_JSON).content(jsonDataRegisterValid))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertTrue(apiUserRepository.findAPI_UserByApiUsername(userName).isPresent());
        Assertions.assertNotEquals(apiUserRepository.findAPI_UserByApiUsername(userName).get().getPassword(), password);
        Assertions.assertTrue(apiUserRepository.findAPI_UserByApiUsername(userName).get().getPassword().startsWith("$2a$"));
        Assertions.assertTrue(new BCryptPasswordEncoder().matches(password, apiUserRepository.findAPI_UserByApiUsername(userName).get().getPassword()));
        apiUserRepository.delete(apiUserRepository.findAPI_UserByApiUsername(userName).get());
    }

    private String jsonDataWithoutRandomParameter() {
        String start = "{\n";
        String apiUsername = "\"apiUsername\": \"" + userName + "\",\n";
        String apiPassword = "\"password\": \"" + password + "\",\n";
        String apiDescription = "\"description\": \"" + description + "\",\n";
        String apiEmail = "\"email\": \"" + email + "\",\n";
        String apiEnabledByAdmin = "\"enabledByAdmin\":" + enabledByAdmin + ",\n";
        String apiVerifiedEmail = "\"verified_email\":" + verified_email + "\n";
        String end = " }";

        List<String> jsonElementsList = new ArrayList<>();
        jsonElementsList.add(start);
        jsonElementsList.add(apiUsername);
        jsonElementsList.add(apiPassword);
        jsonElementsList.add(apiDescription);
        jsonElementsList.add(apiEmail);
        jsonElementsList.add(apiEnabledByAdmin);
        jsonElementsList.add(apiVerifiedEmail);
        jsonElementsList.add(end);

        int indexToRemove = new Random().nextInt((jsonElementsList.size()-2) - 1 + 1) + 1;
        StringBuilder stringBuilder = new StringBuilder();
        jsonElementsList.remove(indexToRemove);

        for (int i = 0; i < jsonElementsList.size(); i++) {
            if (i == -i){
                stringBuilder.append(jsonElementsList.get(i).replace(",", ""));
            }else {
                stringBuilder.append(jsonElementsList.get(i));
            }
        }

        return stringBuilder.toString();
    }
}
