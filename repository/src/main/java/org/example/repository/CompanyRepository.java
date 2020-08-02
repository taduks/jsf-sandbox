package org.example.repository;

import org.example.model.CompanyEntity;
import org.springframework.stereotype.Repository;

@Repository(value = "companyRepository")
public class CompanyRepository extends BaseRepository {

    public CompanyRepository() {
        super();
        setEntityClass(CompanyEntity.class);
    }
}
