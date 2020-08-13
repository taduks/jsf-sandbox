package org.example;

import org.example.model.CompanyEntity;
import org.example.services.CompanyService;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
@SessionScoped
public class CompanyBean extends BaseBean{

    @Inject
    CompanyService companyService;

    public CompanyBean() {
        System.out.println("Init Company Bean");
    }

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
