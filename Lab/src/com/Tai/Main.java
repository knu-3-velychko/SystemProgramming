package com.Tai;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager("localhost", 1052, 2, 5, "double", true);
        try {
            manager.start();
            System.out.println(manager.getResults());
            System.out.println(manager.getConjunction());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
