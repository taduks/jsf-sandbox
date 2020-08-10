package org.example;

import org.example.services.UtilService;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

@Startup
@Named("aaaBean")
@ApplicationScoped
public class AaaBean {

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
}
