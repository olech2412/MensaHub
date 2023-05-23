package com.essensGetter.api.ControllerTest;

import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;

@SpringBootTest
@AutoConfigureMockMvc
public class CafeteriaDittrichringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    String jsonData = "{\n" +
            "      \"id\": 1,\n" +
            "      \"name\": \"Testname\",\n" +
            "      \"description\": \"Testbeschreibung\",\n" +
            "      \"price\": \"3,05€/ 4,90€/ 6,70€\",\n" +
            "      \"category\": \"Testkategorie\",\n" +
            "      \"servingDate\": \"2023-05-23\",\n" +
            "      \"responseCode\": 200,\n" +
            "      \"rating\": " + ((int) (Math.random() * (5))) + ",\n" +
            "      \"votes\": 0,\n" +
            "      \"starsTotal\": 0\n" +
            "    }";

    @Test
    public void contextLoads() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    public void controllerShouldReturnMealData() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/mealsForFritz/cafeteria_dittrichring?code=8PLUv50emD7jBakyy9U4").contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItems()));
    }

    @Test
    public void controllerShouldBeAccessedOnlyWithAuthCode() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/mealsForFritz/cafeteria_dittrichring")).andDo(print()).andExpect(status().is(401));
    }

    @Test
    public void controllerShouldBeAccessedOnlyWithValidAuthCode() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/mealsForFritz/cafeteria_dittrichring?code=" + RandomString.make(20))).andDo(print()).andExpect(status().is(401));
    }

    @Test
    public void controllerShouldReceivePostData() throws Exception {
        this.mockMvc.perform(post("/mealsFromFritz/cafeteria_dittrichring?code=8PLUv50emD7jBakyy9U4").contentType(MediaType.APPLICATION_JSON).content(jsonData))
                .andDo(print())
                .andExpect(status()
                .isOk()).andReturn();
    }
}
