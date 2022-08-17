package cn.serendipityr.EndMinecraftPlusV2.Tools;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtherUtils {
    public static void doSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Matcher matches(String str, String regex) {
        Pattern mPattern = Pattern.compile(regex);
        return mPattern.matcher(str);
    }

    public static String getRandomString(int minLength, int maxLength) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        int length = random.nextInt(maxLength) % (maxLength - minLength + 1) + minLength;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(62);
            stringBuilder.append(str.charAt(number));
        }
        return stringBuilder.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getCo(String date, T def) {
        if (date.equals("")) {
            return def;
        }
        return (T) date;
    }

    public static int getCo(String date, int def) {
        if (date.equals("")) {
            return def;
        }
        return Integer.parseInt(date);
    }

    public static void loadLibrary(File file) {
        try {
            URLClassLoader cl = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(cl, file.toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
