package org.example.services;

import org.example.model.BaseEntity;
import org.example.repository.BaseRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public class DataService implements Serializable {

    private BaseRepository repository;


    protected void setRepository(BaseRepository repository) {
        this.repository = repository;
    }

    public BaseRepository getRepository() {
        return repository;
    }

    @Transactional(readOnly=true)
    public <T extends BaseEntity> List<T> findAll(Object[] criterions, int offset, int limit) {
        return this.repository.findAll(criterions, 0, 0);
    }

    @Transactional(readOnly=true)
    public int count(Object[] criterions) {
        return this.repository.count(criterions);
    }
}
