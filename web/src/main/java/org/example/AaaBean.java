package org.example;

import org.example.services.UtilService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ApplicationScoped
public class AaaBean extends BaseBean {

    @Inject
    UtilService utilService;

    public AaaBean() {
        System.out.println("Init Util Bean");
    }

    @PostConstruct
    public void init() {
        System.out.println("UtilBean init method");
    }

    void startup() {
        System.out.println("UtilBean startup method");
    }

    public void openCars () {
        Redirect redirect = new Redirect("/car.jsf");
        redirect.execute();
    }

    public void openCompanies () {
        Redirect redirect = new Redirect("/companies.jsf");
        redirect.execute();
    }
}
