package com.application.service;


import com.application.query.ElasticQueries;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class QueryServiceTest {


    /*@Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationMetadataRepository organizationMetadataRepository;

    @Mock
    private DomainRepository domainRepository;

    @InjectMocks
    private OrganizationService organizationService = new OrganizationService();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }*/

    @Test
    public void testGetMetadataByOrganizationName() {
        QueryService queryService = new QueryService();
        Map<String, String> params = new HashMap<>();
        params.put("size", "10");
        params.put("movieName", "Inception");

        String expectedQuery =
                "{\n" +
                "  \"size\": \"10\",\n" +
                "  \"query\": {\n" +
                "    \"filtered\": {\n" +
                "      \"query\": {\n" +
                "        \"match_all\": {}\n" +
                "      },\n" +
                "      \"filter\": {\n" +
                "        \"bool\": {\n" +
                "          \"should\": [\n" +
                "            {\"query\": {\"wildcard\": {\"titleName\": {\"value\": \"Inception\"}}}},\n" +
                "            {\"query\": { \"match\": {\"titleName\": \"Inception\"}}}\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        String actualQuery = queryService.fetchPopulatedQuery(ElasticQueries.FIND_MOVIE_BY_NAME, params);

        assertEquals(actualQuery, expectedQuery);
    }
}
