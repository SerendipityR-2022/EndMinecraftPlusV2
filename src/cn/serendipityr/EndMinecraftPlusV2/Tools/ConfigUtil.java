package cn.serendipityr.EndMinecraftPlusV2.Tools;

import cc.summermc.bukkitYaml.file.YamlConfiguration;
import cn.serendipityr.EndMinecraftPlusV2.EndMinecraftPlusV2;

import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class ConfigUtil {
    public static File configFile;
    public static YamlConfiguration config;
    public static String AttackAddress;
    public static Integer AttackPort;
    public static Integer AttackMethod;
    public static Integer AttackTime;
    public static Long ConnectDelay;
    public static Integer MaxConnections;
    public static Boolean TabAttack;
    public static Boolean AntiAttackMode;
    public static String DoubleExploitPlayer;
    public static Boolean ShowFails;
    public static String BotName;
    public static Integer BotCount;
    public static Boolean RegisterAndLogin;
    public static List<String> RegisterCommands;
    public static Integer RejoinCount;
    public static List<String> RejoinDetect;
    public static Long RejoinDelay;
    public static List<String> ClickVerifiesDetect;
    public static List<String> CustomChat;
    public static Boolean ChatSpam;
    public static Long ChatDelay;
    public static Integer ProxyGetType;
    public static Integer ProxyType;
    public static Integer ProxyUpdateTime;
    public static File ProxyFile;
    public static List<String> ProxyAPIs;
    public static Boolean SaveWorkingProxy;

    public void loadConfig() {
        try {
            configFile = new File("config.yml");

            if (!configFile.exists()) {
                LogUtil.doLog(1, "载入配置文件失败! 文件不存在。", null);
                EndMinecraftPlusV2.Exit();
            }

            config = YamlConfiguration.loadConfiguration(configFile);

            AttackAddress = config.getString("AttackSettings.Address");
            AttackPort = config.getInt("AttackSettings.Port");
            AttackMethod = config.getInt("AttackSettings.Method");
            AttackTime = config.getInt("AttackSettings.Time");
            ConnectDelay = config.getLong("AttackSettings.ConnectDelay");
            MaxConnections = config.getInt("AttackSettings.MaxConnections");
            TabAttack = config.getBoolean("AttackSettings.TabAttack");
            AntiAttackMode = config.getBoolean("AttackSettings.AntiAttackMode");
            DoubleExploitPlayer = config.getString("AttackSettings.DoubleExploitPlayer");
            ShowFails = config.getBoolean("AttackSettings.ShowFails");
            BotName = config.getString("BotSettings.BotName");
            BotCount = config.getInt("BotSettings.BotCount");
            RegisterAndLogin = config.getBoolean("BotSettings.Register&Login");
            RegisterCommands = config.getStringList("BotSettings.RegisterCommands");
            RejoinCount = config.getInt("BotSettings.RejoinCount");
            RejoinDetect = config.getStringList("BotSettings.RejoinDetect");
            RejoinDelay = config.getLong("BotSettings.RejoinDelay");
            ClickVerifiesDetect = config.getStringList("BotSettings.ClickVerifiesDetect");
            ChatSpam = config.getBoolean("BotSettings.ChatSpam");
            CustomChat = config.getStringList("BotSettings.CustomChat");
            ChatDelay = config.getLong("BotSettings.ChatDelay");
            ProxyGetType = config.getInt("Proxy.GetType");
            ProxyType = config.getInt("Proxy.ProxyType");
            ProxyUpdateTime = config.getInt("Proxy.UpdateTime");
            ProxyFile = new File(config.getString("Proxy.File"));
            ProxyAPIs = config.getStringList("Proxy.APIs");
            SaveWorkingProxy = config.getBoolean("Proxy.SaveWorkingProxy");

            checkSRV();

            LogUtil.doLog(0, "==============================================================", "CFGUtil");
            LogUtil.doLog(0, "服务器地址: " + AttackAddress, "CFGUtil");
            LogUtil.doLog(0, "服务器端口: " + AttackPort, "CFGUtil");
            LogUtil.doLog(0, "攻击方式: " + getAttackMethod(AttackMethod), "CFGUtil");
            LogUtil.doLog(0, "攻击时间: " + AttackTime + "秒", "CFGUtil");
            LogUtil.doLog(0, "连接间隔: " + timeToSeconds(ConnectDelay) + "秒", "CFGUtil");
            LogUtil.doLog(0, "最大连接数: " + MaxConnections + "个", "CFGUtil");
            LogUtil.doLog(0, "同时进行Tab攻击: " + booleanToStr(TabAttack), "CFGUtil");
            LogUtil.doLog(0, "AntiAttack模式: " + booleanToStr(AntiAttackMode), "CFGUtil");
            LogUtil.doLog(0, "代理类型: " + getProxyFrom(ProxyGetType), "CFGUtil");
            LogUtil.doLog(0, "代理API: " + ProxyAPIs.size() + "个", "CFGUtil");
            LogUtil.doLog(0, "代理更新间隔: " + ProxyUpdateTime + "秒", "CFGUtil");
            LogUtil.doLog(0, "==============================================================", "CFGUtil");
            LogUtil.emptyLog();
        } catch (Exception e) {
            LogUtil.emptyLog();
            LogUtil.doLog(1, "载入配置文件失败! 详细信息: " + e, null);
            EndMinecraftPlusV2.Exit();
        }
    }

    public static String getAttackMethod(int type) {
        switch (type) {
            case 1:
                return "BotAttack";
            case 2:
                return "MotdAttack";
            case 3:
                return "MotdAttackP";
            case 4:
                return "DoubleAttack";
            default:
                return "Error";
        }
    }

    public static String getProxyFrom(int type) {
        switch (type) {
            case 1:
                return "API";
            case 2:
                return "File";
            case 3:
                return "API + File";
            default:
                return "Error";
        }
    }

    public static String booleanToStr(boolean type) {
        return type ? "开启" : "关闭";
    }

    public static Double timeToSeconds(long time) {
        return (double) time / 1000;
    }

    public static void checkSRV() {
        Hashtable<String, String> hashtable = new Hashtable<>();
        hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        hashtable.put("java.naming.provider.url", "dns:");

        try {
            Attribute attribute = (new InitialDirContext(hashtable))
                    .getAttributes("_Minecraft._tcp." + AttackAddress,
                            new String[]{"SRV"})
                    .get("srv");
            if (attribute != null) {
                String[] re = attribute.get().toString().split(" ", 4);
                LogUtil.doLog(0, "=============================================================", "CheckSRV");
                LogUtil.doLog(0,"域名: " + AttackAddress, "CheckSRV");
                LogUtil.doLog(0,"源地址: " + re[3], "CheckSRV");
                LogUtil.doLog(0,"源端口: " + re[2], "CheckSRV");
                LogUtil.doLog(-1,"检测到服务器存在SRV记录，是否替换地址为SRV解析记录？[y/n]: ", "CheckSRV");
                Scanner scanner = new Scanner(System.in);
                String cmd = scanner.nextLine();

                if (cmd.equals("y")) {
                    AttackAddress = re[3];
                    AttackPort = Integer.parseInt(re[2]);
                }
            }
        } catch (Exception ignored) {}
    }
}
