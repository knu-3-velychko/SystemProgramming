package com.Tai;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("It works!");
        Manager manager = new Manager("localhost", 1052, 2, 0, "int", false);
        try {
            manager.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

