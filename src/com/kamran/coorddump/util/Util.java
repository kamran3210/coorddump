package com.kamran.coorddump.util;

import java.util.Arrays;

public class Util {
    public static String[] trim(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }
}
