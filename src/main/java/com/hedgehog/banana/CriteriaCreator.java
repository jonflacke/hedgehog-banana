package com.hedgehog.banana;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.*;


/**
 * Created by Jon on 9/11/2021.
 */
public class CriteriaCreator {

    private static SearchCriteria searchCriterium;

    public static Criteria getCriteria(SearchCriteria searchCriteria, Class clazz) {
        searchCriterium = searchCriteria;
        Comparator<Map.Entry<SearchOperation, Object>> searchOperationMapComparator = (entry1, entry2) -> {
            return entry1.getKey().compareTo(entry2.getKey());
        };

        List<Map.Entry<SearchOperation, Object>> operations = searchCriteria.getOperationValueEntries();
        Collections.sort(operations, searchOperationMapComparator);
        Iterator<Map.Entry<SearchOperation, Object>> iterator = operations.iterator();
        List<String> seenParams = new ArrayList<>();
        Map.Entry<SearchOperation, Object> entry = iterator.next();
        Criteria returnCriteria = getSearchCriteria(entry, clazz);

        addSeenParam(seenParams, entry);

        while (iterator.hasNext()) {
            entry = iterator.next();
            if (seenParams.contains(searchCriterium.getKey() + entry.getKey().toString())
                    || (seenParams.contains(searchCriterium.getKey()) && entry.getKey().equals(SearchOperation.NULL))) {
                returnCriteria = returnCriteria.orOperator(returnCriteria, getSearchCriteria(entry, clazz));
            } else {
                returnCriteria = returnCriteria.andOperator(returnCriteria, getSearchCriteria(entry, clazz));
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
    private static Criteria getSearchCriteria(Map.Entry<SearchOperation, Object> operationValueEntry, Class clazz) {
        if (EntityTraversalUtility.isFieldOnParameterizedSubEntity(clazz, searchCriterium.getKey())) {
            return getParameterizedFieldCriteria(searchCriterium.getKey(), operationValueEntry, clazz);
        } else {
            return getNonParameterizedFieldCriteria(searchCriterium.getKey(), operationValueEntry);
        }
    }

    private static Criteria getParameterizedFieldCriteria(String pathToEntity, Map.Entry<SearchOperation, Object> operationValueEntry, Class clazz) {
        /*
        Get path down to entity
        return a criteria where path to entity in call to this function
        To call this function, a search operation entry is needed
            The search operation should contain everything below found parameterized entity
            Operator of value entry should be that of topmost entry - the only change should be the path
        This should repeat until you hit bottom level entity
        Once you reach the bottom, perform a non-parameterized field criteria call and return that
         */
        if (EntityTraversalUtility.isFieldOnParameterizedSubEntity(clazz, pathToEntity)) {
            ClassFieldPath classAndFieldPathToEntity = EntityTraversalUtility.getParameterizedEntityAndRemainingFieldPath(
                    new ClassFieldPath(clazz, pathToEntity));
            String pathToParameterizedEntity = EntityTraversalUtility.getFieldPathToParameterizedEntity(searchCriterium.getKey(), classAndFieldPathToEntity.getFieldPath());
            // create a criteria with where-in and add as it's "in" a call to this function
            return Criteria.where(pathToParameterizedEntity).in(getParameterizedFieldCriteria(classAndFieldPathToEntity.getFieldPath(), operationValueEntry, classAndFieldPathToEntity.getClazz()));
        } else {
            // Build final query and return it back
            return getNonParameterizedFieldCriteria(pathToEntity, operationValueEntry);
        }
    }

    private static Criteria getNonParameterizedFieldCriteria(String pathToEntity, Map.Entry<SearchOperation, Object> operationValueEntry) {
        final String value = operationValueEntry.getValue().toString();
        switch (operationValueEntry.getKey()) {
            case LIKE:
                // Using "i" for case insensitivity - this is just default and may not be desired
                // Find way of indicating whether this is desired then pass correct option to regex
                return Criteria.where(pathToEntity).regex(".*".concat(value).concat(".*"), "i");
            case STARTS:
                return Criteria.where(pathToEntity).regex("^".concat(value), "i");
            case ENDS:
                return Criteria.where(pathToEntity).regex(value.concat("$"), "i");
            case EQUALS:
                return Criteria.where(pathToEntity).is(value);
            case NOT_EQUAL:
                return Criteria.where(pathToEntity).ne(value);
            case LESS_THAN:
                return Criteria.where(pathToEntity).lt(value);
            case GREATER_THAN:
                return Criteria.where(pathToEntity).gt(value);
            case NULL:
                return Criteria.where(pathToEntity).is(null);
            case NOT_NULL:
                return Criteria.where(pathToEntity).not().is(null);
            default:
                return null;
        }
    }
}
