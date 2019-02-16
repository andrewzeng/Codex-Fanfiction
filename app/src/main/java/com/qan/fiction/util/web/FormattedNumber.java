package com.qan.fiction.util.web;

public class FormattedNumber {
    public static int parseInt(String number) {
        if (number.contains("K") || number.contains("k"))
            return (int) (Double.parseDouble(number.substring(0, number.length() - 1)) * 1000);
        else
            return Integer.parseInt(number);
    }
}
