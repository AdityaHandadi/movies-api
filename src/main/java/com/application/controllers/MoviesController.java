package com.application.controllers;

import com.application.exceptions.BadRequestException;
import com.application.exceptions.InternalServerErrorException;
import com.application.model.Movie;
import com.application.query.ElasticQueries;
import com.application.service.QueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "/movies")
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/movies")
public class MoviesController {

    @Autowired
    private QueryService queryService;

    @ApiOperation(value = "Find movies By Name, either find by name OR insert * to unknown parts of the name(wildcard)",
                  response = Movie.class)
    @RequestMapping(value = "/findByName", method = RequestMethod.GET)
    public List<Movie> getAllMovies(@RequestParam(value = "movieName") String movieName,
                                    @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) throws BadRequestException, InternalServerErrorException {
        Map<String, String> params = new HashMap<>();
        params.put("movieName", movieName);
        params.put("size", size.toString());

        if(size < 0 || "".equals(movieName))
            throw new BadRequestException("Given Request is invalid");

        return queryService.fetchMovies(ElasticQueries.FIND_MOVIE_BY_NAME, params);
    }
}
