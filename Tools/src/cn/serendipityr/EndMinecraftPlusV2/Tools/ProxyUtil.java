package cn.serendipityr.EndMinecraftPlusV2.Tools;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;

public class ProxyUtil {
    public static Date runTime;
    public static List<String> proxies = new CopyOnWriteArrayList<>();
    public static List<Proxy> workingProxiesList = new CopyOnWriteArrayList<>();

    public static void prepareProxy() {
        if (!ConfigUtil.AttackMethod.equals(2)) {
            LogUtil.doLog(0, "正在获取代理...", "ProxyUtil");
            ProxyUtil.getProxies();
            ProxyUtil.runUpdateProxiesTask(ConfigUtil.ProxyUpdateTime);
        }
    }

    public static void getProxies() {
        String getMethod;

        switch (ConfigUtil.ProxyGetType) {
            case 2:
                getMethod = "通过本地文件获取";
                getProxiesFromFile(true);
                break;
            case 3:
                getMethod = "通过API+本地文件获取";
                getProxiesFromFile(true);
                getProxiesFromAPIs(false);
                break;
            case 1:
            default:
                getMethod = "通过API获取";
                getProxiesFromAPIs(true);
        }

        LogUtil.doLog(0, "获取代理完成! (" + getMethod + " | 数量: " + proxies.size() + "个)", "ProxyUtil");
        LogUtil.emptyLog();
    }

    public static void getProxiesFromAPIs(boolean replace) {
        if (replace) {
            proxies.clear();
        }

        for (String url : ConfigUtil.ProxyAPIs) {
            String ips = HTTPUtil.sendGet(url);
            Matcher matcher = OtherUtils.matches(ips, "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\:\\d{1,5}");
            while (matcher.find()) {
                String ip = matcher.group();

                if (!proxies.contains(ip)) {
                    proxies.add(ip);
                }
            }
        }
    }


    public static void getProxiesFromFile(boolean replace) {
        try {
            if (!ConfigUtil.ProxyFile.exists()) {
                LogUtil.doLog(1, "无法从文件读取代理! 文件不存在。", null);
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(ConfigUtil.ProxyFile));
            String tempString;

            if (replace) {
                proxies.clear();
            }

            while ((tempString = reader.readLine()) != null) {
                if (!proxies.contains(tempString)) {
                    proxies.add(tempString);
                }
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

                switch (ConfigUtil.ProxyGetType) {
                    case 1:
                        getProxiesFromAPIs(true);
                        LogUtil.doLog(0, "代理更新完毕! (通过API获取 | 数量: " + proxies.size() + "个)", "ProxyUtil");
                        break;
                    case 2:
                        getProxiesFromFile(true);
                        LogUtil.doLog(0, "代理更新完毕! (通过本地文件获取 | 数量: " + proxies.size() + "个)", "ProxyUtil");
                        break;
                    case 3:
                        getProxiesFromFile(true);
                        getProxiesFromAPIs(false);
                        LogUtil.doLog(0, "代理更新完毕! (通过API+本地文件获取 | 数量: " + proxies.size() + "个)", "ProxyUtil");
                        break;
                }
            }
        }).start();
    }

    public static void saveWorkingProxy(Proxy proxy) {
        if (runTime == null) {
            runTime = new Date();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        File workingProxies = new File("working-proxies_" + simpleDateFormat.format(runTime) + ".txt");
        InetSocketAddress inetSocketAddress = (InetSocketAddress) proxy.address();

        if (!workingProxiesList.contains(proxy)) {
            try {
                FileWriter fileWriter = new FileWriter(workingProxies, true);
                String proxyAddress = (inetSocketAddress.getAddress() + ":" + inetSocketAddress.getPort() + "\n").replace("/", "");
                fileWriter.write(proxyAddress);
                fileWriter.close();
                workingProxiesList.add(proxy);
            } catch (IOException e) {
                LogUtil.doLog(1, "保存有效代理失败! IO异常: " + e.getMessage(), null);
            }
        }
    }
}
