package com.hedgehog.banana;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Created by Jon on 1/19/2019.
 */
@NoRepositoryBean
public interface BaseMongoRepository<T, ID extends Serializable> extends MongoRepository<T, ID>, BaseRepository<T, ID> {
    default T findOne(ID id) {
        return (T) findById(id).orElse(null);
    }
}
