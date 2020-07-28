package org.example;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;

import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class CarBean {

    public List<String> getCarDetails() {

        List<String> cars = new ArrayList<>();

        cars.add(0, "Santro");
        cars.add(1, "Zen");
        cars.add(2, "Alto");
        cars.add(3, "Qualis");
        cars.add(4, "Innova");

        return cars;
    }

    public Integer getCarCount() {
        return 5;
    }
}
