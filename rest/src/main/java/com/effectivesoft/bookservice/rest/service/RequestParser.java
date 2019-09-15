package com.effectivesoft.bookservice.rest.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RequestParser {

    private static final Map<String, String> SORTING_COLUMNS = new HashMap<>() {{
        put("name", "name");
        put("title", "title");
        put("author", "author");
        put("authors", "author");
        put("isbn", "isbn");
        put("isbn13", "isbn13");
        put("rating", "average_rating");
        put("publisher", "publisher");
        put("binding", "binding");
        put("pages", "pages_number");
        put("year", "publication_year");
        put("original", "original_publication_year");
    }};

    public String[] parseSortParam(String[] sort) {
        String[] sortingColumns = null;
        String sortingColumn;
        if (sort != null) {
            sortingColumns = new String[sort.length];
            for (int i = 0; i < sort.length; i++) {
                sortingColumn = parse(sort[i]);
                if (sortingColumn != null) {
                    sortingColumns[i] = sortingColumn;
                }
            }
        }
        return sortingColumns;
    }
    
    private String parse(String param) {
        String column = param.substring(0, param.indexOf(' '));
        if (SORTING_COLUMNS.containsKey(column)) {
            String direction = param.substring(param.indexOf(' ') + 1);
            if (direction.equals("ascending")) {
                return SORTING_COLUMNS.get(column) + " ASC";
            }
            if (direction.equals("descending")) {
                return SORTING_COLUMNS.get(column) + " DESC";
            }
        } else {
            return null;
        }
        return null;
    }


}
