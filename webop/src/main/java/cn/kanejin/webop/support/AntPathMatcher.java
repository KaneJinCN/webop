package cn.kanejin.webop.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * 解析匹配Ant-style的通配符路径
 *
 * <p>Ant-style通配符规则:<br>
 * <ul>
 * <li>? 匹配一个字符</li>
 * <li>* 匹配零或多个字符</li>
 * <li>** 在路径中匹配零或多个目录</li>
 * </ul>
 *
 * <p>示例:<br>
 * <pre>
 *     view/hello?.jsp - 匹配"view/hello1.jsp"、"view/hello2.jsp"、"view/hello9.jsp"
 *     com/*.java - 匹配com目录下所有的.java文件
 *     com/&#42;&#42;/Hello.java - 匹配com目录及子目录下所有的Hello.java文件
 *     com/example/&#42;&#42;/*.java - 匹配com/example目录及子目录下所有的.java文件
 *     com/example/** - 匹配com/example目录及子目录下所有的文件
 * </pre>
 *
 * @author Kane Jin
 */
public class AntPathMatcher {
    private static final Logger log = LoggerFactory.getLogger(AntPathMatcher.class);

    private String regexPattern;

    public AntPathMatcher(String pattern) {
        this.regexPattern = toRegex(pattern);
    }

    public boolean matches(String path) {
        return Pattern.matches(regexPattern, path);
    }

    public static boolean matches(String pattern, String path) {
        if (isPattern(pattern)) {
            return Pattern.matches(toRegex(pattern), path);
        } else {
            return pattern.equals(path);
        }
    }

    /**
     * 判断模板是否包含通配符
     *
     * @param antPattern
     * @return
     */
    private static boolean isPattern(String antPattern) {
        return antPattern.contains("?") || antPattern.contains("*");
    }

    /**
     * 把Ant-style的通配符转换成正则表达式
     *
     * @param antPattern
     * @return
     */
    private static String toRegex(String antPattern) {

        String regexPattern = antPattern.replaceAll("\\\\", "/");
        regexPattern = regexPattern.replaceAll("\\.", "\\\\.");
        regexPattern = regexPattern.replaceAll("\\?", "[^/]");

        regexPattern = replaceSingleStar(regexPattern);

        regexPattern = regexPattern.replaceAll("\\*\\*/", "([^/]+/)*");
        regexPattern = regexPattern.replaceAll("/\\*\\*", "/.*");

        return regexPattern;
    }

    /**
     * 用递归把模板中单独的'*'更换成"[^/]+"
     *
     * @param pattern 模板
     * @return
     */
    private static String replaceSingleStar(String pattern) {
        int index = indexOfSingleStar(pattern);
        if (index < 0) {
            return pattern;
        }

        String prefix = (index == 0) ? "" : pattern.substring(0, index);
        String suffix = (index == pattern.length() - 1) ? "" : pattern.substring(index + 1);

        return replaceSingleStar(prefix + "[^/]+" + suffix);
    }

    /**
     * 获取模板中单独的'*'的下标
     *
     * @param pattern 模板
     * @return
     */
    private static int indexOfSingleStar(String pattern) {

        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) == '*') {
                // 看看左侧是否还是*
                if (i - 1 >= 0) {
                    if (pattern.charAt(i - 1) == '*') {
                        continue;
                    }
                }

                // 看看右侧是否还是*
                if (i + 1 < pattern.length()) {
                    if (pattern.charAt(i + 1) == '*') {
                        continue;
                    }
                }

                return i;
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        String pattern = "view/hello?.jsp";

        System.out.println(AntPathMatcher.matches(pattern, "view/hello1.jsp"));
        System.out.println(AntPathMatcher.matches(pattern, "view/hello$.jsp"));
        System.out.println(AntPathMatcher.matches(pattern, "view/hello/world.jsp"));

        pattern = "com/*.java";
        System.out.println(AntPathMatcher.matches(pattern, "com/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/World.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/Hello.java"));

        pattern = "com/**/Hello.java";
        System.out.println(AntPathMatcher.matches(pattern, "com/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/company/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/a/b/c/d/e/f/g/h/i/j/k/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/a/b/c/d/e/f/g/h/i/j/k/World.java"));


        pattern = "com/example/**/*.java";
        System.out.println(AntPathMatcher.matches(pattern, "com/example/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/World.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/company/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/company/World.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/a/b/c/d/e/f/g/h/i/j/k/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/a/b/c/d/e/f/g/h/i/j/k/World.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/a/b/c/d/e/f/g/h/i/j/k/Hello.class"));
        System.out.println(AntPathMatcher.matches(pattern, "com/a/b/c/d/e/f/g/h/i/j/k/World.class"));


        pattern = "com/example/**";
        System.out.println(AntPathMatcher.matches(pattern, "com/example/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/World.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/company/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/company/World.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/company/Hello.class"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/company/World.class"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/a/b/c/d/e/f/g/h/i/j/k/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/a/b/c/d/e/f/g/h/i/j/k/Hello.class"));
        System.out.println(AntPathMatcher.matches(pattern, "com/a/b/c/d/e/f/g/h/i/j/k/Hello.class"));

        pattern = "com/**/company/**/*.java";
        System.out.println(AntPathMatcher.matches(pattern, "com/company/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/company/department/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/company/department/A/World.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/company/department/A/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/foo/company/department/A/World.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/foo/department/A/Hello.java"));
        System.out.println(AntPathMatcher.matches(pattern, "com/example/foo/company/department/A/World.class"));
    }
}
