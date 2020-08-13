package org.example;

import org.example.services.CarService;
import org.example.services.UtilService;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.List;

@Named
@SessionScoped
public class CarBean extends BaseBean {

    private Integer counter = 0;

    @Inject
    CarService carService;

    @Inject
    UtilService utilService;

    public CarBean() {
        System.out.println("Init Car Bean");
    }

    public void init() {
        counter++;
        System.out.println("CarBean init method");
    }

    public List<String> getCarDetails() {
        return carService.getCars();
    }

    public Integer getCarCount() {
        return 5;
    }

    public String page () {
        return "car?faces-redirect = true";
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }
}
