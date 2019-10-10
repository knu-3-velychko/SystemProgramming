package com.Tai;

import org.junit.jupiter.api.Test;
import spos.lab1.demo.DoubleOps;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {

    @Test
    void case0() throws IOException, InterruptedException {
        Manager manager = new Manager("localhost", 1050, 2, 0, "double", true);
        manager.start();
        HashMap<String, Double> results = manager.getResults();
        assertEquals(results.size(), 2);
        assertEquals(results.get("f"), DoubleOps.funcF(0));
        assertEquals(results.get("g"), DoubleOps.funcG(0));
        assertTrue(manager.getConjunction());
    }

    @Test
    void case1() throws IOException, InterruptedException {
        Manager manager = new Manager("localhost", 1051, 2, 1, "double", true);
        manager.start();
        HashMap<String, Double> results = manager.getResults();
        assertEquals(results.size(), 2);
        assertEquals(results.get("f"), DoubleOps.funcF(1));
        assertEquals(results.get("g"), DoubleOps.funcG(1));
        assertTrue(manager.getConjunction());
    }

    @Test
    void case2() throws IOException, InterruptedException {
        Manager manager = new Manager("localhost", 1052, 2, 2, "double", true);
        manager.start();
        HashMap<String, Double> results = manager.getResults();
        assertEquals(results.size(), 1);
        assertEquals(results.get("f"), DoubleOps.funcF(2));
        assertFalse(manager.getConjunction());
    }

    @Test
    void case3() throws IOException, InterruptedException {
        Manager manager = new Manager("localhost", 1053, 2, 3, "double", true);
        manager.start();
        HashMap<String, Double> results = manager.getResults();
        assertEquals(results.size(), 1);
        assertEquals(results.get("g"), DoubleOps.funcG(3));
        assertFalse(manager.getConjunction());
    }

//    @Test
//    void case4() throws IOException, InterruptedException {
//        Manager manager = new Manager("localhost", 1054, 2, 4, "double", true);
//        manager.start();
//        HashMap<String, Double> results = manager.getResults();
//        assertEquals(results.size(), 1);
//        assertEquals(results.get("f"), DoubleOps.funcF(4));
//    }
//
//    @Test
//    void case5() throws IOException, InterruptedException {
//        Manager manager = new Manager("localhost", 1055, 2, 5, "double", true);
//        manager.start();
//        HashMap<String, Double> results = manager.getResults();
//        assertEquals(results.size(), 1);
//        assertEquals(results.get("g"), DoubleOps.funcG(5));
//    }
}