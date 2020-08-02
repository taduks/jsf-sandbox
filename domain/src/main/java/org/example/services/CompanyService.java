package org.example.services;

import org.example.model.CompanyEntity;
import org.example.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyService extends DataService {

    @Autowired
    public void setRepository(CompanyRepository repository) {
        super.setRepository(repository);
    }

    public List<CompanyEntity> getCompanies () {
        List<CompanyEntity> companies = findAll(new Object[]{}, 0, 0);
        return companies;
    }

    public Integer getCompanyCount() {
        return count(new Object[] {});
    }
}
