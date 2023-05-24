package com.essensGetter.api.APINavigation;

import com.essensGetter.api.JPA.entities.APIAccess;
import com.essensGetter.api.JPA.repository.APIAccessRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class CodeInterceptorTest {

    @Autowired
    APIAccessRepository apiAccessRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() {
        assertThat(mockMvc).isNotNull();
        assertThat(apiAccessRepository).isNotNull();
    }
}
