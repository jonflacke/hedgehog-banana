package com.hedgehog.banana;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServiceConstants {
    public static final List<String> NON_FILTER_ACTIONS =
            Collections.unmodifiableList(Arrays.asList("sort", "include", "count", "start"));
    public static final List<String> FILTER_ACTIONS = Collections.unmodifiableList(Arrays.asList("equals", "before", "less", "after", "greater", "like", "starts","ends", "not", "null"));
    public static final List<String> NON_PREDICATE_TERMS =Collections.unmodifiableList(Arrays.asList("least", "greatest", "min", "max"));
    public static final List<SearchOperation> VALID_BOOLEAN_OPERATORS =Collections.unmodifiableList(Arrays.asList(SearchOperation.EQUALS, SearchOperation.NOT_EQUAL, SearchOperation.NULL, SearchOperation.NOT_NULL));
    public static final List<SearchOperation> VALID_STRING_OPERATORS = Collections.unmodifiableList(Arrays.asList(SearchOperation.EQUALS, SearchOperation.NOT_EQUAL, SearchOperation.NULL, SearchOperation.NOT_NULL, SearchOperation.ENDS, SearchOperation.STARTS, SearchOperation.LIKE));
    public static final List<SearchOperation> VALID_ENUM_OPERATORS = Collections.unmodifiableList(Arrays.asList(SearchOperation.EQUALS, SearchOperation.NOT_EQUAL, SearchOperation.NULL, SearchOperation.NOT_NULL, SearchOperation.ENDS, SearchOperation.STARTS, SearchOperation.LIKE));
    public static final List<SearchOperation> VALID_NUMERIC_OPERATORS = Collections.unmodifiableList(Arrays.asList(SearchOperation.EQUALS, SearchOperation.NOT_EQUAL, SearchOperation.NULL, SearchOperation.NOT_NULL, SearchOperation.ENDS, SearchOperation.STARTS, SearchOperation.LIKE, SearchOperation.GREATER_THAN, SearchOperation.LESS_THAN, SearchOperation.GREATEST, SearchOperation.LEAST));
    public static final List<SearchOperation> VALID_DATE_OPERATORS = Collections.unmodifiableList(Arrays.asList(SearchOperation.EQUALS, SearchOperation.NOT_EQUAL, SearchOperation.NULL, SearchOperation.NOT_NULL, SearchOperation.GREATER_THAN, SearchOperation.LESS_THAN, SearchOperation.GREATEST, SearchOperation.LEAST));
    public static final List<SearchOperation> VALID_CHARACTER_OPERATORS =  Collections.unmodifiableList(Arrays.asList(SearchOperation.EQUALS, SearchOperation.NOT_EQUAL, SearchOperation.NULL, SearchOperation.NOT_NULL));
}
