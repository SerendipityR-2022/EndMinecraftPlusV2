package cn.serendipityr.EndMinecraftPlusV2.Tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class ProxyUtil {
    public static final List<String> proxies = new ArrayList<>();

    public static void getProxies() {
        String getMethod;

        switch (ConfigUtil.ProxyGetType) {
            case 2:
                getMethod = "通过本地文件获取";
                getProxiesFromFile();
                break;
            case 3:
                getMethod = "通过API+本地文件获取";
                getProxiesFromFile();
                getProxiesFromAPIs();
                break;
            case 1:
            default:
                getMethod = "通过API获取";
                getProxiesFromAPIs();
        }

        LogUtil.doLog(0, "获取代理完成! (" + getMethod + " | 数量: " + proxies.size() + "个)", "ProxyUtil");
        LogUtil.emptyLog();
    }

    public static void getProxiesFromAPIs() {
        for (String url:ConfigUtil.ProxyAPIs) {
            String ips = HTTPUtil.sendGet(url);

            Matcher matcher = OtherUtils.matches(ips, "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d{1,5}");
            while (matcher.find()) {
                String ip = matcher.group();
                proxies.add(ip);
            }
        }
    }

    public static void getProxiesFromFile() {
        try {
            if (!ConfigUtil.ProxyFile.exists()) {
                LogUtil.doLog(1, "无法从文件读取代理! 文件不存在。", null);
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(ConfigUtil.ProxyFile));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                proxies.add(tempString);
            }

            reader.close();
        } catch (IOException e) {
            LogUtil.doLog(1, "无法从文件读取代理! IO异常: " + e, null);
        }
    }

    public static void runUpdateProxiesTask(int time) {
        new Thread(() -> {
            while (true) {
                OtherUtils.doSleep(time * 1000L);
                synchronized (proxies) {
                    getProxiesFromAPIs();
                }
            }
        }).start();
    }
}
