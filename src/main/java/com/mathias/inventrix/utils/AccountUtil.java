package com.mathias.inventrix.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AccountUtil {

 public  String generateRandomId() {
     Random rand = new Random();

     int firstNumber = rand.nextInt(9) + 1;
     int secondNumber = rand.nextInt(9) + 1;
     char randomChar = (char) ('A' + rand.nextInt(26));

     return "" + firstNumber + secondNumber + randomChar;
 }
}
