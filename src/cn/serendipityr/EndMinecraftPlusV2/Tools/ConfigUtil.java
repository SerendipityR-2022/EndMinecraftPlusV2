package cn.serendipityr.EndMinecraftPlusV2.Tools;

import cc.summermc.bukkitYaml.file.YamlConfiguration;
import cn.serendipityr.EndMinecraftPlusV2.EndMinecraftPlusV2;

import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.*;

public class ConfigUtil {
    public static File configFile;
    public static YamlConfiguration config;
    public static Integer CfgVer;
    public static String AttackAddress;
    public static Integer AttackPort;
    public static Integer AttackMethod;
    public static Integer AttackTime;
    public static Long ConnectDelay;
    public static Long ConnectTimeout;
    public static Integer MaxConnections;
    public static Boolean TabAttack;
    public static Boolean AntiAttackMode;
    public static String DoubleExploitPlayer;
    public static Boolean ShowFails;
    public static String BotName;
    public static Integer RandomFlag;
    public static Integer RandomMinLength;
    public static Integer RandomMaxLength;
    public static Boolean RandomTeleport;
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
    public static Boolean ForgeSupport;
    public static HashMap<String, String> ForgeModList;
    public static Boolean CatAntiCheat;
    public static File CACCustomScreenShot;
    public static List<String> CACLoadedClass;
    public static File CACLoadedMods;
    public static Boolean RandomMAC;

    public void loadConfig() {
        try {
            configFile = new File("config.yml");

            if (!configFile.exists()) {
                LogUtil.doLog(1, "载入配置文件失败! 文件不存在。", null);
                EndMinecraftPlusV2.Exit();
            }

            config = YamlConfiguration.loadConfiguration(configFile);

            CfgVer = config.getInt("CfgVer");

            AttackAddress = config.getString("AttackSettings.Address");
            AttackPort = config.getInt("AttackSettings.Port");
            AttackMethod = config.getInt("AttackSettings.Method");
            AttackTime = config.getInt("AttackSettings.Time");
            ConnectDelay = config.getLong("AttackSettings.ConnectDelay");
            ConnectTimeout = config.getLong("AttackSettings.ConnectTimeout");
            MaxConnections = config.getInt("AttackSettings.MaxConnections");
            TabAttack = config.getBoolean("AttackSettings.TabAttack");
            AntiAttackMode = config.getBoolean("AttackSettings.AntiAttackMode");
            DoubleExploitPlayer = config.getString("AttackSettings.DoubleExploitPlayer");
            ShowFails = config.getBoolean("AttackSettings.ShowFails");
            BotName = config.getString("BotSettings.BotName");
            BotCount = config.getInt("BotSettings.BotCount");
            RandomTeleport = config.getBoolean("BotSettings.RandomTeleport");
            RandomFlag = config.getInt("BotSettings.RandomFlag");
            RandomMinLength = config.getInt("BotSettings.RandomMinLength");
            RandomMaxLength = config.getInt("BotSettings.RandomMaxLength");
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
            ForgeSupport = config.getBoolean("AdvancedSettings.ForgeSupport");

            if (ForgeSupport) {
                ForgeModList = new HashMap<>();

                for (String modInfo:config.getStringList("AdvancedSettings.ModList")) {
                    String modName = modInfo.split(":")[0];
                    String modVersion = modInfo.split(":")[1];
                    ForgeModList.put(modName, modVersion);
                }
            }

            CatAntiCheat = config.getBoolean("AdvancedSettings.CatAntiCheat.Enable");

            if (CatAntiCheat) {
                CACCustomScreenShot = new File(config.getString("AdvancedSettings.CatAntiCheat.CustomScreenShot"));
                CACLoadedClass = config.getStringList("AdvancedSettings.CatAntiCheat.LoadedClass");
                CACLoadedMods = new File(config.getString("AdvancedSettings.CatAntiCheat.LoadedMods"));

                if (!CACCustomScreenShot.exists()) {
                    LogUtil.doLog(1, "CustomScreenShot不存在，CatAntiCheat相关功能已关闭。", null);
                    CatAntiCheat = false;
                }

                if (CACLoadedClass.isEmpty()) {
                    LogUtil.doLog(1, "LoadedClass为空，CatAntiCheat相关功能已关闭。", null);
                    CatAntiCheat = false;
                }

                if (Objects.requireNonNull(CACLoadedMods.listFiles()).length <= 5) {
                    LogUtil.doLog(1, "LoadedMods数量不足(<=5)，CatAntiCheat相关功能已关闭。", null);
                    CatAntiCheat = false;
                }

                if (!ForgeSupport) {
                    LogUtil.doLog(1, "ForgeSupport未开启，CatAntiCheat相关功能已关闭。", null);
                    CatAntiCheat = false;
                } else {
                    ForgeModList.put("catanticheat", "1.2.6");
                }
            }

            RandomMAC = config.getBoolean("AdvancedSettings.MACChecker.RandomMAC");

            checkSRV();

            LogUtil.doLog(0, "==============================================================", "CFGUtil");
            LogUtil.doLog(0, "服务器地址: " + AttackAddress, "CFGUtil");
            LogUtil.doLog(0, "服务器端口: " + AttackPort, "CFGUtil");
            LogUtil.doLog(0, "攻击方式: " + getAttackMethod(AttackMethod), "CFGUtil");
            LogUtil.doLog(0, "攻击时间: " + AttackTime + "秒", "CFGUtil");
            LogUtil.doLog(0, "连接间隔: " + timeToSeconds(ConnectDelay) + "秒", "CFGUtil");
            LogUtil.doLog(0, "最大连接数: " + MaxConnections + "个", "CFGUtil");
            LogUtil.doLog(0, "Forge支持: " + booleanToStr(ForgeSupport), "CFGUtil");
            LogUtil.doLog(0, "同时进行Tab攻击: " + booleanToStr(TabAttack), "CFGUtil");
            LogUtil.doLog(0, "AntiAttack模式: " + booleanToStr(AntiAttackMode), "CFGUtil");
            LogUtil.doLog(0, "CatAntiCheat绕过: " + booleanToStr(CatAntiCheat), "CFGUtil");
            LogUtil.doLog(0, "代理类型: " + getProxyFrom(ProxyGetType), "CFGUtil");
            LogUtil.doLog(0, "代理API: " + ProxyAPIs.size() + "个", "CFGUtil");
            LogUtil.doLog(0, "代理更新间隔: " + ProxyUpdateTime + "秒", "CFGUtil");
            LogUtil.doLog(0, "==============================================================", "CFGUtil");
            LogUtil.emptyLog();
        } catch (Exception e) {
            LogUtil.emptyLog();
            LogUtil.doLog(1, "载入配置文件失败! 详细信息: " + e, null);
            LogUtil.doLog(-1, "配置可能存在编码问题，是否尝试转换编码以解决问题？ [y/n]:", "CFGUtil");
            Scanner scanner = new Scanner(System.in);
            if (scanner.nextLine().contains("y")) {
                String currentCharset = getFileCharset(configFile);

                File tempConfigFile = new File("config_temp.yml");

                switch (currentCharset) {
                    case "GBK":
                        convertFileCharset(configFile, tempConfigFile, currentCharset, "UTF-8");
                        break;
                    case "UTF-8":
                    default:
                        convertFileCharset(configFile, tempConfigFile, currentCharset, "GBK");
                        break;
                }

                if (configFile.delete()) {
                    tempConfigFile.renameTo(configFile);
                }

                LogUtil.doLog(0, "任务完成。转换前编码: " + currentCharset + " | 转换后编码: " + getFileCharset(configFile) , "CFGUtil");
            }

            loadConfig();
        }

        if (!EndMinecraftPlusV2.CfgVer.equals(CfgVer)) {
            LogUtil.doLog(1, "载入配置文件失败! 配置文件版本不匹配，请前往发布页更新配置文件。", null);
            EndMinecraftPlusV2.Exit();
        }
    }

    public static String getFileCharset(File file) {
        String charset = "GBK";

        byte[] first3Bytes = new byte[3];

        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()));
            bis.mark(100);

            int read = bis.read(first3Bytes, 0, 3);

            if (read == -1) {
                bis.close();
                return charset; // 文件编码为 ANSI
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; // 文件编码为 Unicode
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; // 文件编码为 Unicode big endian
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; // 文件编码为 UTF-8
                checked = true;
            }

            bis.reset();

            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (!(0x80 <= read && read <= 0xBF)) {
                            break;
                        }
                    } else if (0xE0 <= read) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                            }
                        }
                        break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return charset;
    }

    public static void convertFileCharset(File inputFile, File outputFile,String currentCharset ,String targetCharset) {
        try {
            InputStreamReader isr = new InputStreamReader(Files.newInputStream(inputFile.toPath()) ,currentCharset);
            java.io.OutputStreamWriter osw = new OutputStreamWriter(Files.newOutputStream(outputFile.toPath()) ,targetCharset);

            int len;
            while((len = isr.read())!=-1){
                osw.write(len);
            }

            osw.close();
            isr.close();
        } catch (Exception e) {
            LogUtil.doLog(1, "转换文件编码时发生错误! 详细信息: " + e, null);
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
