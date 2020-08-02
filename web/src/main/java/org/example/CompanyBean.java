package org.example;

import org.example.model.CompanyEntity;
import org.example.services.CarService;
import org.example.services.CompanyService;

import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
@SessionScoped
public class CompanyBean {

    @Inject
    CompanyService companyService;

    public List<CompanyEntity> getCompanies() {
        return companyService.getCompanies();
    }

    public Integer getCompanyCount() {
        return companyService.getCompanyCount();
    }

    public String page () {
        return "companies?faces-redirect = true";
    }
}
