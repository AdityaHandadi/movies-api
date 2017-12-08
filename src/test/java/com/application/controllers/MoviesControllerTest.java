package com.application.controllers;

import com.application.TestConfig;
import com.application.model.Movie;
import com.application.query.ElasticQueries;
import com.application.service.QueryService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@Ignore
public class MoviesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QueryService queryService;

    @InjectMocks
    private MoviesController moviesController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(moviesController)
                .build();
    }

    @After
    public void after() {
        Mockito.reset(queryService);
    }

    @Test
    public void getMoviesGivenValidName() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("movieName", "Inception");
        params.put("size", "2");

        Movie movie = new Movie("tt000111", "Inception", 2010);
        List<Movie> movieList = Arrays.asList(movie);

        Mockito.when(queryService.fetchMovies(ElasticQueries.FIND_MOVIE_BY_NAME, params)).thenReturn(movieList);

        mockMvc.perform(MockMvcRequestBuilders.get("/movies?movieName=Inception&size=2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(
               "{\n" +
                        "    \"titleId\": \"tt000111\",\n" +
                        "    \"titleName\": \"Inception\",\n" +
                        "    \"year\": 2010\n" +
                        "  }")));

    }
}
