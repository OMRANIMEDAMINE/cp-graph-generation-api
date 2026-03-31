package edu.polytech.cpmolgenapi;

public class TestCase  {
    String name;
    int[] degrees;
    String description;

    TestCase(String name, int[] degrees, String description) {
        this.name = name;
        this.degrees = degrees;
        this.description = description;
    }

    TestCase(String name, int[] degrees) {
        this(name, degrees, "");
    }


}
