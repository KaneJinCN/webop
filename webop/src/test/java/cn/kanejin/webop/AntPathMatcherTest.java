package cn.kanejin.webop;

import cn.kanejin.webop.support.AntPathMatcher;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kane Jin
 */
public class AntPathMatcherTest {

    @Test
    public void testAntPath() {

        String pattern = "**/hello?.jsp";
        AntPathMatcher matcher = new AntPathMatcher(pattern);

        Assert.assertTrue(matcher.matches("/view/hello1.jsp"));
        Assert.assertTrue(matcher.matches("/view/hello$.jsp"));
        Assert.assertFalse(matcher.matches("/view/hello/world.jsp"));

        Assert.assertTrue(SelectorUtils.matchPath(pattern, "/view/hello1.jsp"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "/view/hello$.jsp"));
        Assert.assertFalse(SelectorUtils.matchPath(pattern, "/view/hello/world.jsp"));


        pattern = "/view/hello?.jsp";
        matcher = new AntPathMatcher(pattern);

        Assert.assertTrue(matcher.matches("/view/hello1.jsp"));
        Assert.assertTrue(matcher.matches("/view/hello$.jsp"));
        Assert.assertFalse(matcher.matches("/view/hello/world.jsp"));

        Assert.assertTrue(SelectorUtils.matchPath(pattern, "/view/hello1.jsp"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "/view/hello$.jsp"));
        Assert.assertFalse(SelectorUtils.matchPath(pattern, "/view/hello/world.jsp"));

        pattern = "com/*.java";
        matcher = new AntPathMatcher(pattern);

        Assert.assertTrue(matcher.matches("com/Hello.java"));
        Assert.assertTrue(matcher.matches("com/World.java"));
        Assert.assertFalse(matcher.matches("com/example/Hello.java"));

        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/Hello.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/World.java"));
        Assert.assertFalse(SelectorUtils.matchPath(pattern, "com/example/Hello.java"));



        pattern = "com/**/Hello.java";
        matcher = new AntPathMatcher(pattern);

        Assert.assertTrue(matcher.matches("com/Hello.java"));
        Assert.assertTrue(matcher.matches("com/example/Hello.java"));
        Assert.assertTrue(matcher.matches("com/example/company/Hello.java"));
        Assert.assertTrue(matcher.matches("com/a/b/c/d/e/f/g/h/i/j/k/Hello.java"));
        Assert.assertFalse(matcher.matches("com/a/b/c/d/e/f/g/h/i/j/k/World.java"));

        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/Hello.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/Hello.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/company/Hello.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/a/b/c/d/e/f/g/h/i/j/k/Hello.java"));
        Assert.assertFalse(SelectorUtils.matchPath(pattern, "com/a/b/c/d/e/f/g/h/i/j/k/World.java"));

        pattern = "com/example/**/*.java";
        matcher = new AntPathMatcher(pattern);

        Assert.assertTrue(matcher.matches("com/example/Hello.java"));
        Assert.assertTrue(matcher.matches("com/example/World.java"));
        Assert.assertTrue(matcher.matches("com/example/company/Hello.java"));
        Assert.assertTrue(matcher.matches("com/example/company/World.java"));
        Assert.assertFalse(matcher.matches("com/a/b/c/d/e/f/g/h/i/j/k/Hello.java"));
        Assert.assertFalse(matcher.matches("com/a/b/c/d/e/f/g/h/i/j/k/World.java"));
        Assert.assertFalse(matcher.matches("com/a/b/c/d/e/f/g/h/i/j/k/Hello.class"));
        Assert.assertFalse(matcher.matches("com/a/b/c/d/e/f/g/h/i/j/k/World.class"));

        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/Hello.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/World.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/company/Hello.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/company/World.java"));
        Assert.assertFalse(SelectorUtils.matchPath(pattern, "com/a/b/c/d/e/f/g/h/i/j/k/Hello.java"));
        Assert.assertFalse(SelectorUtils.matchPath(pattern, "com/a/b/c/d/e/f/g/h/i/j/k/World.java"));
        Assert.assertFalse(SelectorUtils.matchPath(pattern, "com/a/b/c/d/e/f/g/h/i/j/k/Hello.class"));
        Assert.assertFalse(SelectorUtils.matchPath(pattern, "com/a/b/c/d/e/f/g/h/i/j/k/World.class"));

        pattern = "com/example/**";
        matcher = new AntPathMatcher(pattern);

        Assert.assertTrue(matcher.matches("com/example/Hello.java"));
        Assert.assertTrue(matcher.matches("com/example/World.java"));
        Assert.assertTrue(matcher.matches("com/example/company/Hello.java"));
        Assert.assertTrue(matcher.matches("com/example/company/World.java"));
        Assert.assertTrue(matcher.matches("com/example/company/Hello.class"));
        Assert.assertTrue(matcher.matches("com/example/company/World.class"));
        Assert.assertTrue(matcher.matches("com/example/a/b/c/d/e/f/g/h/i/j/k/Hello.java"));
        Assert.assertTrue(matcher.matches("com/example/a/b/c/d/e/f/g/h/i/j/k/Hello.class"));
        Assert.assertFalse(matcher.matches("com/a/b/c/d/e/f/g/h/i/j/k/Hello.class"));

        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/Hello.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/World.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/company/Hello.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/company/World.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/company/Hello.class"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/company/World.class"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/a/b/c/d/e/f/g/h/i/j/k/Hello.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/a/b/c/d/e/f/g/h/i/j/k/Hello.class"));
        Assert.assertFalse(SelectorUtils.matchPath(pattern, "com/a/b/c/d/e/f/g/h/i/j/k/Hello.class"));


        pattern = "com/**/company/**/*.java";
        matcher = new AntPathMatcher(pattern);

        Assert.assertTrue(matcher.matches("com/company/Hello.java"));
        Assert.assertTrue(matcher.matches("com/company/department/Hello.java"));
        Assert.assertTrue(matcher.matches("com/company/department/A/World.java"));
        Assert.assertTrue(matcher.matches("com/example/company/department/A/Hello.java"));
        Assert.assertTrue(matcher.matches("com/example/foo/company/department/A/World.java"));
        Assert.assertFalse(matcher.matches("com/example/foo/department/A/Hello.java"));
        Assert.assertFalse(matcher.matches("com/example/foo/company/department/A/World.class"));

        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/company/Hello.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/company/department/Hello.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/company/department/A/World.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/company/department/A/Hello.java"));
        Assert.assertTrue(SelectorUtils.matchPath(pattern, "com/example/foo/company/department/A/World.java"));
        Assert.assertFalse(SelectorUtils.matchPath(pattern, "com/example/foo/department/A/Hello.java"));
        Assert.assertFalse(SelectorUtils.matchPath(pattern, "com/example/foo/company/department/A/World.class"));

    }

}
