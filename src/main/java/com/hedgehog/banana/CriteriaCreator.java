package com.hedgehog.banana;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.*;


/**
 * Created by Jon on 9/11/2021.
 */
public class CriteriaCreator {

    private static SearchCriteria searchCriterium;

    public static Criteria getCriteria(SearchCriteria searchCriteria) {
        searchCriterium = searchCriteria;
        Comparator<Map.Entry<SearchOperation, Object>> searchOperationMapComparator = (entry1, entry2) -> {
            return entry1.getKey().compareTo(entry2.getKey());
        };

        List<Map.Entry<SearchOperation, Object>> operations = searchCriteria.getOperationValueEntries();
        Collections.sort(operations, searchOperationMapComparator);
        Iterator<Map.Entry<SearchOperation, Object>> iterator = operations.iterator();
        List<String> seenParams = new ArrayList<>();
        Map.Entry<SearchOperation, Object> entry = iterator.next();
        Criteria returnCriteria = getSearchCriteria(entry);

        addSeenParam(seenParams, entry);

        while (iterator.hasNext()) {
            entry = iterator.next();
            if (seenParams.contains(searchCriterium.getKey() + entry.getKey().toString())
                    || (seenParams.contains(searchCriterium.getKey()) && entry.getKey().equals(SearchOperation.NULL))) {
                returnCriteria = returnCriteria.orOperator(returnCriteria, getSearchCriteria(entry));
            } else {
                returnCriteria = returnCriteria.andOperator(returnCriteria, getSearchCriteria(entry));
                addSeenParam(seenParams, entry);
            }
        }
        return returnCriteria;
    }

    /**
     * Keeps track of all seen parameters, including the specific operations
     * performed (i.e. like, equals, greater, etc)
     * @param seenParams current list of seen parameters
     * @param entry the entry to add to the list
     */
    private static void addSeenParam(List<String> seenParams, Map.Entry<SearchOperation, Object> entry) {
        seenParams.add(searchCriterium.getKey() + entry.getKey().toString());
        seenParams.add(searchCriterium.getKey());
    }

    /**
     * Gets the individual search criteria based on the key and operation
     * @param operationValueEntry operation and value with which to build
     * @param clazz the class of object on which to search
     * @return criteria for operation and value
     */
    private static Criteria getSearchCriteria(Map.Entry<SearchOperation, Object> operationValueEntry) {
        final String value = operationValueEntry.getValue().toString();
        switch (operationValueEntry.getKey()) {
            case LIKE:
                // Using "i" for case insensitivity - this is just default and may not be desired
                // Find way of indicating whether this is desired then pass correct option to regex
                return Criteria.where(searchCriterium.getKey()).regex(".*".concat(value).concat(".*"), "i");
            case STARTS:
                return Criteria.where(searchCriterium.getKey()).regex("^".concat(value), "i");
            case ENDS:
                return Criteria.where(searchCriterium.getKey()).regex(value.concat("$"), "i");
            case EQUALS:
                return Criteria.where(searchCriterium.getKey()).is(value);
            case NOT_EQUAL:
                return Criteria.where(searchCriterium.getKey()).ne(value);
            case LESS_THAN:
                return Criteria.where(searchCriterium.getKey()).lt(value);
            case GREATER_THAN:
                return Criteria.where(searchCriterium.getKey()).gt(value);
            case NULL:
                return Criteria.where(searchCriterium.getKey()).is(null);
            case NOT_NULL:
                return Criteria.where(searchCriterium.getKey()).not().is(null);
            default:
                return null;
        }
    }

}
