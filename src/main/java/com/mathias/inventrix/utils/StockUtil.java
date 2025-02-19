package com.mathias.inventrix.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class StockUtil {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random RANDOM = new Random();

    public static String generateStockUnitNo() {
        StringBuilder unitNo = new StringBuilder();

        // Generate two random uppercase letters
        unitNo.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        unitNo.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));

        // Generate three random numbers
        unitNo.append(RANDOM.nextInt(10));
        unitNo.append(RANDOM.nextInt(10));
        unitNo.append(RANDOM.nextInt(10));

        return unitNo.toString();
    }
}
