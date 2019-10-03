package com.Tai;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager("localhost", 1052, 2, 0, "int", true);
        try {
            manager.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
