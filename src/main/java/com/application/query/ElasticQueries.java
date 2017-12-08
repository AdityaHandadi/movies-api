package com.application.query;

public class ElasticQueries {

    public static final String FIND_MOVIE_BY_NAME =
            "{\n" +
            "  \"size\": \"$size\",\n" +
            "  \"query\": {\n" +
            "    \"filtered\": {\n" +
            "      \"query\": {\n" +
            "        \"match_all\": {}\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"bool\": {\n" +
            "          \"should\": [\n" +
            "            {\"query\": {\"wildcard\": {\"titleName\": {\"value\": \"$movieName\"}}}},\n" +
            "            {\"query\": { \"match\": {\"titleName\": \"$movieName\"}}}\n" +
            "          ]\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    public static final String FIND_ALL_CASTS =
            "{\n" +
            "  \"query\": {\n" +
            "   \t\"match\" : {\n" +
            "      \"titleId\": \"$titleId\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

    public static final String FIND_CAST_DETAILS =
            "{\n" +
            "    \"query\" : {\n" +
            "        \"constant_score\" : {\n" +
            "            \"filter\" : {\n" +
            "                \"terms\" : { \n" +
            "                    \"nameId\" : [\"$listOfNameIds\"]\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";

}
