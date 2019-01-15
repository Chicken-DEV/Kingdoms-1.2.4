package com.songoda.kingdoms.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    public static boolean checkForMatch(List<String> matchers, String input) {
        Pattern pattern;
        Matcher matcher;
        int matches = 0;
        for (String match : matchers) {
            pattern = Pattern.compile(match);
            matcher = pattern.matcher(input);
            for (; matcher.find(); matches++);
        }
        return matches > 0;
    }

}
