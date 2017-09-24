package cn.kanejin.webop.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kane Jin
 */
class PathVarURI {
    private static final Logger log = LoggerFactory.getLogger(PathVarURI.class);


    private static final Pattern VARIABLE_PATTERN = Pattern.compile("(\\{[^/]+})");

    public final String uri;
    public final String regex;
    public final String[] names;

    public PathVarURI(String pattern) {
        this.uri = pattern;
        this.regex = toRegex(pattern);
        this.names = extractVariableNames(pattern);
    }

    public boolean matches(String uri) {
        return Pattern.matches(regex, uri);
    }

    public Map<String, String> extractPathVariables(String uri) {

        Matcher uriMatcher = Pattern.compile(regex).matcher(uri);

        Map<String, String> pathVars = new HashMap<>(names.length);

        if (uriMatcher.find()) {
            for (String name : names) {
                try {
                    pathVars.put(name, uriMatcher.group(name));
                } catch (Exception e) {
                    log.warn("No Path Variable [" + name + "] match found", e);
                }
            }
        }

        return pathVars;
    }

    public static boolean isPathVarURI(String uri) {
        return VARIABLE_PATTERN.matcher(uri).find();
    }

    private static String toRegex(String pattern) {
        String regex = pattern;

        Matcher matcher = VARIABLE_PATTERN.matcher(pattern);
        while(matcher.find()) {
            String s = matcher.group();

            String r;
            if (s.contains(":")) {
                String[] kp = s.substring(1, s.length() - 1).trim().split("\\s*:\\s*");

                if (kp[1].equalsIgnoreCase("string")) {
                    r = "(?<" + kp[0] + ">[^/]+)";

                } else if (kp[1].equalsIgnoreCase("integer")) {
                    r = "(?<" + kp[0] + ">\\d+)";
                } else if (kp[1].equalsIgnoreCase("decimal")) {
                    r = "(?<" + kp[0] + ">\\d+(\\.\\d+)?)";
                } else {
                    r = "(?<" + kp[0] + ">" + kp[1] + ")";
                }
            } else {
                r = "(?<" + s.substring(1, s.length() - 1).trim() + ">[^/]+)";
            }

            regex = regex.replace(s, r);
        }

        return regex;
    }

    private static String[] extractVariableNames(String pattern) {

        Matcher patternMatcher = VARIABLE_PATTERN.matcher(pattern);

        List<String> names = new ArrayList<>(3);

        while(patternMatcher.find()) {
            String s = patternMatcher.group();

            String name = s.substring(1, s.length() - 1);

            if (s.contains(":")) {
                name = name.substring(0, name.indexOf(":"));
            }

            names.add(name);
        }

        return names.toArray(new String[0]);
    }
}
