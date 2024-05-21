package app.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderControllerTest {
    @Test
    void calculatePrice () {
        int price = 2924;
        double degreeOfCoverage = 0.40;
        int processingFee = 2000;
        int carportPrice = price;
        carportPrice = (int) ((carportPrice * degreeOfCoverage) + price + processingFee);

        System.out.println(carportPrice);
    }





}