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

    public static String getRandomString_Ili(int minLength, int maxLength) {
        String str = "Ili";
        Random random = new Random();
        int length = random.nextInt(maxLength) % (maxLength - minLength + 1) + minLength;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(3);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomString_Abc(int minLength, int maxLength) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        int length = random.nextInt(maxLength) % (maxLength - minLength + 1) + minLength;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(52);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomString_123(int minLength, int maxLength) {
        String str = "1234567890";
        Random random = new Random();
        int length = random.nextInt(maxLength) % (maxLength - minLength + 1) + minLength;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(10);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static Integer getRandomInt(int min, int max) {
        return (int)(Math.random()*(max-min+1)+min);
    }

    public static float getRandomFloat(double min, double max) {
        return (float) (Math.random()*(max-min)+min);
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
