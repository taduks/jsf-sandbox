package org.example;

import org.example.services.CarService;

import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.List;

@Named
@SessionScoped
public class CarBean {

    @Inject
    CarService carService;

    public List<String> getCarDetails() {
        return carService.getCars();
    }

    public Integer getCarCount() {
        return 5;
    }
}
