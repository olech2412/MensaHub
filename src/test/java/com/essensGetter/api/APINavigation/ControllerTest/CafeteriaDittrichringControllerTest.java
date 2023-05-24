package com.essensGetter.api.APINavigation.ControllerTest;

import com.essensGetter.api.JPA.entities.meals.Meals_Cafeteria_Dittrichring;
import com.essensGetter.api.JPA.services.meals.Meals_Cafeteria_DittrichringService;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;

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

    Integer randomRating = ((int) (Math.random() * (5)));

    String jsonData = "{\n" +
            "      \"id\": 1,\n" +
            "      \"name\": \"Testname\",\n" +
            "      \"description\": \"Testbeschreibung\",\n" +
            "      \"price\": \"3,05€/ 4,90€/ 6,70€\",\n" +
            "      \"category\": \"Testkategorie\",\n" +
            "      \"servingDate\": \"2001-01-01\",\n" +
            "      \"responseCode\": 200,\n" +
            "      \"rating\": " + randomRating + ",\n" +
            "      \"votes\": 0,\n" +
            "      \"starsTotal\": 0\n" +
            "    }";

    @Autowired
    Meals_Cafeteria_DittrichringService meals_cafeteria_dittrichringService;

    @Test
    public void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(meals_cafeteria_dittrichringService).isNotNull();
    }

    @Test
    public void controllerShouldReturnMealData() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/getMeals/cafeteria_dittrichring?code=8PLUv50emD7jBakyy9U4").contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItems()));
    }

    @Test
    public void controllerShouldBeAccessedOnlyWithAuthCode() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/getMeals/cafeteria_dittrichring")).andDo(print()).andExpect(status().is(401));
    }

    @Test
    public void controllerShouldBeAccessedOnlyWithValidAuthCode() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/getMeals/cafeteria_dittrichring?code=" + RandomString.make(20))).andDo(print()).andExpect(status().is(401));
    }

    @Test
    public void controllerShouldReceivePostData() throws Exception {
        Meals_Cafeteria_Dittrichring testMealBeforePost = (Meals_Cafeteria_Dittrichring) meals_cafeteria_dittrichringService.findByNameAndServingDateAndId("Testname", LocalDate.parse("2001-01-01"), 1L).get(0);
        this.mockMvc.perform(post("/sendRating/cafeteria_dittrichring?code=8PLUv50emD7jBakyy9U4").contentType(MediaType.APPLICATION_JSON).content(jsonData))
                .andDo(print())
                .andExpect(status()
                .isOk()).andReturn();
        Meals_Cafeteria_Dittrichring testMealAfterPost = (Meals_Cafeteria_Dittrichring) meals_cafeteria_dittrichringService.findByNameAndServingDateAndId("Testname", LocalDate.parse("2001-01-01"), 1L).get(0);

        Assertions.assertTrue(testMealBeforePost.getVotes() < testMealAfterPost.getVotes());
        Assertions.assertEquals(1, testMealAfterPost.getVotes() - testMealBeforePost.getVotes());
        Assertions.assertEquals(testMealAfterPost.getStarsTotal(), (int) (testMealBeforePost.getStarsTotal() + randomRating));

        Double calculatedRating = Double.valueOf(testMealAfterPost.getStarsTotal()) / Double.valueOf(testMealAfterPost.getVotes());
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.FLOOR);
        Assertions.assertEquals(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")), testMealAfterPost.getRating());
    }
}
