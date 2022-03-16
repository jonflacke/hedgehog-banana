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
        Criteria returnCriteria = getInitialSearchCriteria(entry);

        addSeenParam(seenParams, entry);

        while (iterator.hasNext()) {
            entry = iterator.next();
            // TODO These must be grouped by field and then joined via an array of query
            //    Criteria criteria = new Criteria();
            //    Criteria[] andExpressions = metaData.entrySet().stream().
            //            map(kv -> Criteria.where("data." + kv.getKey()).is(kv.getValue()))
            //            .toArray(Criteria[]::new);
            //    Query andQuery = new Query();
            //    Criteria andCriteria = new Criteria();
            //    andQuery.addCriteria(andCriteria.andOperator(andExpressions));
            //    GridFSDBFile gridFSDBFile = gridFsOperations.findOne(andQuery);
            if (seenParams.contains(searchCriterium.getKey() + entry.getKey().toString())
                    || (seenParams.contains(searchCriterium.getKey()) && entry.getKey().equals(SearchOperation.NULL))) {
                attachAdditionalCriteria(returnCriteria, entry);
            } else {
                attachAdditionalCriteria(returnCriteria, entry);
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

    private static void attachAdditionalCriteria(Criteria criteria, Map.Entry<SearchOperation, Object> operationValueEntry) {
        final String value = operationValueEntry.getValue().toString();
        switch (operationValueEntry.getKey()) {
            case LIKE:
                // Using "i" for case insensitivity - this is just default and may not be desired
                // Find way of indicating whether this is desired then pass correct option to regex
                criteria.and(searchCriterium.getKey()).regex(".*".concat(value).concat(".*"), "i");
                break;
            case STARTS:
                criteria.and(searchCriterium.getKey()).regex("^".concat(value), "i");
                break;
            case ENDS:
                criteria.and(searchCriterium.getKey()).regex(value.concat("$"), "i");
                break;
            case EQUALS:
                criteria.and(searchCriterium.getKey()).is(value);
                break;
            case NOT_EQUAL:
                criteria.and(searchCriterium.getKey()).ne(value);
                break;
            case LESS_THAN:
                criteria.and(searchCriterium.getKey()).lt(value);
                break;
            case GREATER_THAN:
                criteria.and(searchCriterium.getKey()).gt(value);
                break;
            case NULL:
                criteria.and(searchCriterium.getKey()).is(null);
                break;
            case NOT_NULL:
                criteria.and(searchCriterium.getKey()).ne(null);
                break;
            default:
                // Do nothing
                break;
        }
    }

    private static Criteria getInitialSearchCriteria(Map.Entry<SearchOperation, Object> operationValueEntry) {
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
                return Criteria.where(searchCriterium.getKey()).ne(null);
            default:
                return null;
        }
    }

    /**
     * Gets the individual search criteria based on the key and operation
     * @param operationValueEntry operation and value with which to build
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
