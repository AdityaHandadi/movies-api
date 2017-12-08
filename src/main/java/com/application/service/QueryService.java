package com.application.service;

import com.application.esdao.EsDao;
import com.application.exceptions.InternalServerErrorException;
import com.application.model.Cast;
import com.application.model.Mapping;
import com.application.model.Movie;
import com.application.query.ElasticQueries;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QueryService {

    @Autowired
    private EsDao esDao;

    public List<Movie> fetchMovies(String query, Map<String, String> params) throws InternalServerErrorException {
        String populatedQuery = fetchPopulatedQuery(query, params);
        try {
            SearchResponse searchResponseForTitles = esDao.getElasticResponse("titles", populatedQuery);
            List<Movie> movies = parseMovieFrom(searchResponseForTitles, Integer.valueOf(params.get("size")));

            movies.stream().forEach(movie -> {
                movie.setTopBilledCasts(findAndPopulateCasts(movie));
            });

            return movies;
        } catch (Exception e) {
            throw new InternalServerErrorException("Error Occured while querying elastic search");
        }
    }

    /**
     *
     * @param query query that is executed in elasticsearch
     * @param params map of fields that need to be populated for the query
     * @return
     */
    public String fetchPopulatedQuery(String query, Map<String, String> params) {
        Iterator<String> keys = params.keySet().iterator();
        while(keys.hasNext()) {
            String param = keys.next();
            String value = params.get(param);
            query = query.replace("$" + param, value);
        }
        return query;
    }

    /**
     * Method finds and populates top billed Casts given a movie. It queries elastic finds mappings(which casts for this movie)
     * and then finds cast details querying the elastic again.
     * @param movie
     * @return
     */
    private List<Cast> findAndPopulateCasts(Movie movie) {
        List<Cast> castList = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put("titleId", movie.getTitleId());
        try {
            String populatedQuery = fetchPopulatedQuery(ElasticQueries.FIND_ALL_CASTS, params);
            SearchResponse searchResponseForMappings = esDao.getElasticResponse("mappings", populatedQuery);
            Mapping mapping = parseMappingFrom(searchResponseForMappings);

            //a,b,c becomes a","b","c as it could be used in query [" $comaSeprtdCasts "]
            String comaSeprtdCasts = mapping.getListOfNameIds().replace(",", "\",\"");
            params = new HashMap<>();
            params.put("listOfNameIds", comaSeprtdCasts);
            String populatedQueryForCasts = fetchPopulatedQuery(ElasticQueries.FIND_CAST_DETAILS, params);
            SearchResponse searchResponseForCasts = esDao.getElasticResponse("names", populatedQueryForCasts);

            castList = parseCastFrom(searchResponseForCasts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return castList;
    }

    /**
     * Simple parser method to extract Bean from elastic search response
     * @param searchResponse
     * @param size specified by the user for how many results to fetch
     * @return
     */
    private List<Movie> parseMovieFrom(SearchResponse searchResponse, Integer size) {
        List<Movie> movieList = new ArrayList<>();
        Integer numberOfResults = searchResponse.getHits().getHits().length;
        if(numberOfResults > 0) {
            SearchHits searchHits =
                    searchResponse
                    .getHits();

            for(int i = 0; i < size && i < numberOfResults; i++) {
                SearchHit searchHit = searchHits.getAt(i);

                Movie movie = new Movie(
                        searchHit.getSource().get("titleId").toString(),
                        searchHit.getSource().get("titleName").toString(),
                        Integer.valueOf(searchHit.getSource().get("year").toString()),
                        new ArrayList<>()
                );

                movieList.add(movie);
            }
        }
        return movieList;
    }

    /**
     * Simple parser method to extract Bean from elastic search response
     * @param searchResponseForCasts
     * @return
     */
    private List<Cast> parseCastFrom(SearchResponse searchResponseForCasts) {
        List<Cast> castList = new ArrayList<>();
        Integer numberOfResults = searchResponseForCasts.getHits().getHits().length;
        if(numberOfResults > 0) {
            SearchHits searchHits =
                    searchResponseForCasts
                            .getHits();

            for(SearchHit searchHit: searchHits) {
                Cast cast = new Cast(searchHit.getSource().get("nameId").toString(), searchHit.getSource().get("name").toString());
                castList.add(cast);
            }
        }

        return castList;
    }

    /**
     * Simple parser method to extract Bean from elastic search response
     * @param searchResponse
     * @return
     */
    private Mapping parseMappingFrom(SearchResponse searchResponse) {
        Mapping mapping = new Mapping();
        Integer numberOfResults = searchResponse.getHits().getHits().length;
        if(numberOfResults > 0) {
            SearchHits searchHits =
                    searchResponse
                            .getHits();
            SearchHit searchHit = searchHits.getAt(0);

            mapping.setTitleId(searchHit.getSource().get("titleId").toString());
            mapping.setListOfNameIds(searchHit.getSource().get("casts").toString());
        }
        return mapping;
    }
}
