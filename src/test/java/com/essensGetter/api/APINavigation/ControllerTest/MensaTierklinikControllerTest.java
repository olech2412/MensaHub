package com.essensGetter.api.APINavigation.ControllerTest;

import com.essensGetter.api.JPA.entities.meals.Meals_Mensa_Tierklinik;
import com.essensGetter.api.JPA.services.meals.Meals_Mensa_TierklinikService;
import com.essensGetter.api.JPA.services.mensen.Mensa_TierklinikService;
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
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MensaTierklinikControllerTest {

    @Autowired
    private MockMvc mockMvc;

    Integer randomRating = ((int) (Math.random() * (5)));

    String mensaName = "mensa_tierklinik";

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
    Meals_Mensa_TierklinikService mealsMensaTierklinikService;

    @Autowired
    Mensa_TierklinikService mensaTierklinikService;

    @Test
    public void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(mealsMensaTierklinikService).isNotNull();
        assertThat(mensaTierklinikService).isNotNull();
    }

    @Test
    public void controllerShouldReturnMealData() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/" + mensaName + "/getMeals/from/2001-01-01/to/2001-03-03?code=8PLUv50emD7jBakyy9U4").contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItems()))
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void controllerShouldBeAccessedOnlyWithAuthCode() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/" + mensaName + "/")).andDo(print()).andExpect(status().is(401));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/" + mensaName + "/getMeals/from/2001-01-01/to/2001-02-03")).andDo(print()).andExpect(status().is(401));
    }

    @Test
    public void controllerShouldBeAccessedOnlyWithValidAuthCode() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/" + mensaName + "/?code=" + RandomString.make(20))).andDo(print()).andExpect(status().is(401));
    }

    @Test
    public void controllerShouldProvideMensaName() throws Exception {
        String content = this.mockMvc.perform(MockMvcRequestBuilders.get("/" + mensaName + "/?code=8PLUv50emD7jBakyy9U4")).andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(content, "[{\"name\":\"" + mensaTierklinikService.getMensa().getName() + "\"}]");
    }

    @Test
    public void controllerShouldProvideMealsForSpecificTimeRange() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/" + mensaName + "/getMeals/from/2001-01-01/to/2001-03-03?code=8PLUv50emD7jBakyy9U4")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].servingDate", is("2001-01-01")))
                .andExpect(jsonPath("$[1].servingDate", is("2001-02-02")))
                .andExpect(jsonPath("$[2].servingDate", is("2001-03-03")));
    }

    @Test
    public void controllerShouldProvideMealsForSpeicificDate() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/" + mensaName + "/servingDate/2001-02-02?code=8PLUv50emD7jBakyy9U4")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].servingDate", is("2001-02-02")));
    }

    @Test
    public void controllerShouldProvideMealsForSpecificCategory() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/" + mensaName + "/category/Pastateller?code=8PLUv50emD7jBakyy9U4")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItems()))
                .andExpect(jsonPath("$", hasSize(mealsMensaTierklinikService.findAllByCategory("Pastateller").size())));
    }

    @Test
    public void controllerShouldProvideMealsForSpecificCategoryAndServingDate() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/" + mensaName + "/category/Testkategorie3/servingDate/2001-03-03?code=8PLUv50emD7jBakyy9U4")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItems()))
                .andExpect(jsonPath("$", hasSize(mealsMensaTierklinikService.findAllByCategoryAndServingDate("Testkategorie3", LocalDate.parse("2001-03-03")).size())));
    }

    @Test
    public void controllerShouldProvideMealsWhereRatingIsLessThen() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/" + mensaName + "/byRatingLessThen/" + randomRating +"?code=8PLUv50emD7jBakyy9U4")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItems()))
                .andExpect(jsonPath("$", hasSize(mealsMensaTierklinikService.findAllByRatingLessThanEqual(Double.valueOf(randomRating)).size())));
    }

    @Test
    public void controllerShouldProvideMealsWhereRatingIsHigherThen() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/" + mensaName + "/byRatingHigherThen/" + randomRating +"?code=8PLUv50emD7jBakyy9U4")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItems()))
                .andExpect(jsonPath("$", hasSize(mealsMensaTierklinikService.findAllByRatingGreaterThanEqual(Double.valueOf(randomRating)).size())));
    }

    @Test
    public void controllerShouldReceivePostData() throws Exception {
        Meals_Mensa_Tierklinik testMealBeforePost = (Meals_Mensa_Tierklinik) mealsMensaTierklinikService.findByNameAndServingDateAndId("Testname", LocalDate.parse("2001-01-01"), 1L).get(0);
        this.mockMvc.perform(post("/" + mensaName + "/sendRating?code=8PLUv50emD7jBakyy9U4").contentType(MediaType.APPLICATION_JSON).content(jsonData))
                .andDo(print())
                .andExpect(status()
                        .isOk()).andReturn();
        Meals_Mensa_Tierklinik testMealAfterPost = (Meals_Mensa_Tierklinik) mealsMensaTierklinikService.findByNameAndServingDateAndId("Testname", LocalDate.parse("2001-01-01"), 1L).get(0);

        Assertions.assertTrue(testMealBeforePost.getVotes() < testMealAfterPost.getVotes());
        Assertions.assertEquals(1, testMealAfterPost.getVotes() - testMealBeforePost.getVotes());
        Assertions.assertEquals(testMealAfterPost.getStarsTotal(), testMealBeforePost.getStarsTotal() + randomRating);

        Double calculatedRating = Double.valueOf(testMealAfterPost.getStarsTotal()) / Double.valueOf(testMealAfterPost.getVotes());
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.FLOOR);
        Assertions.assertEquals(Double.parseDouble(df.format(calculatedRating).replaceFirst(",", ".")), testMealAfterPost.getRating());
    }
}
