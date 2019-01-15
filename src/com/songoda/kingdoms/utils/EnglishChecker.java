package com.songoda.kingdoms.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class EnglishChecker {
    public static void main(String[] ar) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String input = "";
        try {
            while (!(input = br.readLine()).equals("exit")) {
                System.out.println(isEnglish(input));
                System.out.println(isValidName(input));
                System.out.println(isVailedLore(input));
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean isEnglish(String str) {
        return str.matches("[a-zA-Z0-9_]+");
    }

    public static boolean isValidName(String str) {
        return pattern.matcher(str).matches();
    }

    public static boolean isVailedLore(String str) {
        return str.matches("[\\w ]+");
    }

    private static Pattern pattern = Pattern.compile(
            "# Match a valid Windows filename (unspecified file system).          \n"
                    + "^                                # Anchor to start of string.        \n"
                    + "(?!                              # Assert filename is not: CON, PRN, \n"
                    + "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n"
                    + "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n"
                    + "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n"
                    + "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n"
                    + "  (?:\\.[^.]*)?                  # followed by optional extension    \n"
                    + "  $                              # and end of string                 \n"
                    + ")                                # End negative lookahead assertion. \n"
                    + "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n"
                    + "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n"
                    + "$                                # Anchor to end of string.            ",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);
}
