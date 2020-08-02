package org.example.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarService {


    public List<String> getCars() {
        List<String> cars = new ArrayList<>();

        cars.add(0, "Santro");
        cars.add(1, "Zen");
        cars.add(2, "Alto");
        cars.add(3, "Qualis");
        cars.add(4, "Innova");
        return cars;
    }
}
