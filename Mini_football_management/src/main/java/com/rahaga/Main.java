package com.rahaga;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println(System.getenv("JDBC_URL"));
        System.out.println(System.getenv("USERNAME"));
        System.out.println(System.getenv("PASSWORD"));
    }
}
