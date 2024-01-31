package cn.serendipityr.EndMinecraftPlusV2.Tools;

import cc.summermc.bukkitYaml.file.YamlConfiguration;

import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class ConfigUtil {
    public static File configFile;
    public static YamlConfiguration config;
    public static Boolean isLinux = false;
    public static Integer CfgVer;
    public static String AttackAddress;
    public static Integer AttackPort;
    public static Integer AttackMethod;
    public static Integer AttackTime;
    public static Long ConnectDelay;
    public static Integer ConnectTimeout;
    public static Integer MaxConnections;
    public static Boolean AntiAttackMode;
    public static String DoubleExploitPlayer;
    public static String DebugPlayer;
    public static Boolean DebugPlayerActivities;
    public static List<String> DebugPlayerActions;
    public static Boolean ShowFails;
    public static String BotName;
    public static Integer RandomFlag;
    public static Integer RandomMinLength;
    public static Integer RandomMaxLength;
    public static Boolean KeepAlive;
    public static Boolean PacketHandlerMove;
    public static Integer BotCount;
    public static List<String> RegisterCommands;
    public static Boolean GetMotdBeforeRejoin;
    public static Integer GetMotdCount;
    public static Long GetMotdDelay;
    public static Integer RejoinCount;
    public static List<String> RejoinDetect;
    public static Boolean EmptyMsgRejoin;
    public static List<String> ServerShoutDetect;
    public static Long RejoinDelay;
    public static List<String> ClickVerifiesDetect;
    public static List<String> CustomChat;
    public static List<String> BotActions;
    public static Boolean BotActionDetails;
    public static Integer ProxyGetType;
    public static Integer ProxyType;
    public static Integer ProxyUpdateTime;
    public static File ProxyFile;
    public static List<String> ProxyAPIs;
    public static Boolean SaveWorkingProxy;
    public static Boolean ForgeSupport;
    public static Boolean ReadFromFiles;
    public static HashMap<String, String> ForgeModList;
    public static Boolean CatAntiCheat;
    public static File CACCustomScreenShot;
    public static List<String> CACLoadedClass;
    public static File CACLoadedMods;
    public static Boolean RandomMAC;
    public static Boolean ShowServerMessages;
    public static Integer ServerCrasherMode;
    public static Long ServerCrasherPacketDelay;

    public int loadConfig(int cfgVer) {
        try {
            configFile = new File("config.yml");

            if (!configFile.exists()) {
                LogUtil.doLog(1, "载入配置文件失败! 文件不存在。", null);
                return 0;
            }

            config = YamlConfiguration.loadConfiguration(configFile);

            CfgVer = config.getInt("CfgVer");

            AttackAddress = config.getString("AttackSettings.Address");
            AttackPort = config.getInt("AttackSettings.Port");
            AttackMethod = config.getInt("AttackSettings.Method");
            AttackTime = config.getInt("AttackSettings.Time");
            ConnectDelay = config.getLong("AttackSettings.ConnectDelay");
            ConnectTimeout = config.getInt("AttackSettings.ConnectTimeout");
            MaxConnections = config.getInt("AttackSettings.MaxConnections");
            AntiAttackMode = config.getBoolean("AttackSettings.AntiAttackMode");
            DoubleExploitPlayer = config.getString("AttackSettings.DoubleExploitPlayer");
            DebugPlayer = config.getString("AttackSettings.DebugPlayer");
            DebugPlayerActivities = config.getBoolean("AttackSettings.DebugPlayerActivities.Enable");
            DebugPlayerActions = config.getStringList("AttackSettings.DebugPlayerActivities.Actions");
            ShowFails = config.getBoolean("AttackSettings.ShowFails");
            BotName = config.getString("BotSettings.BotName");
            BotCount = config.getInt("BotSettings.BotCount");
            RandomFlag = config.getInt("BotSettings.RandomFlag");
            RandomMinLength = config.getInt("BotSettings.RandomMinLength");
            RandomMaxLength = config.getInt("BotSettings.RandomMaxLength");
            KeepAlive = config.getBoolean("BotSettings.PacketHandler.KeepAlive");
            PacketHandlerMove = config.getBoolean("BotSettings.PacketHandler.Move");
            RegisterCommands = config.getStringList("BotSettings.RegisterCommands");
            GetMotdBeforeRejoin = config.getBoolean("BotSettings.GetMotdBeforeRejoin");
            GetMotdCount = config.getInt("BotSettings.GetMotdCount");
            GetMotdDelay = config.getLong("BotSettings.GetMotdDelay");
            RejoinCount = config.getInt("BotSettings.RejoinCount");
            RejoinDetect = config.getStringList("BotSettings.RejoinDetect");
            EmptyMsgRejoin = config.getBoolean("BotSettings.EmptyMsgRejoin");
            ServerShoutDetect = config.getStringList("BotSettings.ServerShoutDetect");
            RejoinDelay = config.getLong("BotSettings.RejoinDelay");
            ClickVerifiesDetect = config.getStringList("BotSettings.ClickVerifiesDetect");
            CustomChat = config.getStringList("BotSettings.CustomChat");
            BotActionDetails = config.getBoolean("BotSettings.DetailMsg");
            ShowServerMessages = config.getBoolean("BotSettings.ShowServerMessages");
            BotActions = config.getStringList("BotSettings.Actions");
            ProxyGetType = config.getInt("Proxy.GetType");
            ProxyType = config.getInt("Proxy.ProxyType");
            ProxyUpdateTime = config.getInt("Proxy.UpdateTime");
            ProxyFile = new File(config.getString("Proxy.File"));
            ProxyAPIs = config.getStringList("Proxy.APIs");
            SaveWorkingProxy = config.getBoolean("Proxy.SaveWorkingProxy");
            ForgeSupport = config.getBoolean("AdvancedSettings.ForgeSupport.Enable");
            ReadFromFiles = config.getBoolean("AdvancedSettings.ForgeSupport.ReadFromFiles");

            if (ForgeSupport) {
                ForgeModList = new HashMap<>();
                if (ReadFromFiles) {
                    ForgeModList.putAll(readModInfo("mods"));
                }
                for (String modInfo:config.getStringList("AdvancedSettings.ForgeSupport.ModList")) {
                    String modName = modInfo.split(":")[0];
                    String modVersion = modInfo.split(":")[1];
                    ForgeModList.put(modName, modVersion);
                }
                LogUtil.doLog(0, "当前载入的Mods: ", "ForgeSupport");
                LogUtil.doLog(0, ForgeModList.toString(), "ForgeSupport");
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
            ServerCrasherMode = config.getInt("AdvancedSettings.ServerCrasher.Mode");
            ServerCrasherPacketDelay = config.getLong("AdvancedSettings.ServerCrasher.PacketDelay");

            checkSRV();

            LogUtil.doLog(0, "==============================================================", "CFGUtil");
            LogUtil.doLog(0, "服务器地址: " + AttackAddress, "CFGUtil");
            LogUtil.doLog(0, "服务器端口: " + AttackPort, "CFGUtil");
            LogUtil.doLog(0, "攻击方式: " + getAttackMethod(AttackMethod), "CFGUtil");
            LogUtil.doLog(0, "攻击时间: " + AttackTime + "秒", "CFGUtil");
            LogUtil.doLog(0, "连接间隔: " + timeToSeconds(ConnectDelay) + "秒", "CFGUtil");
            LogUtil.doLog(0, "最大连接数: " + MaxConnections + "个", "CFGUtil");
            LogUtil.doLog(0, "Forge支持: " + booleanToStr(ForgeSupport), "CFGUtil");
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
                        if (0 == convertFileCharset(configFile, tempConfigFile, currentCharset, "UTF-8")) {
                            return 0;
                        }
                        break;
                    case "UTF-8":
                    default:
                        if (0 == convertFileCharset(configFile, tempConfigFile, currentCharset, "GBK")) {
                            return 0;
                        }
                        break;
                }

                if (!configFile.delete() || !tempConfigFile.renameTo(configFile)) {
                    LogUtil.doLog(1, "尝试进行配置文件转码时出现错误。", null);
                    return 0;
                }

                LogUtil.doLog(0, "任务完成。转换前编码: " + currentCharset + " | 转换后编码: " + getFileCharset(configFile) , "CFGUtil");
            }

            loadConfig(cfgVer);
        }

        if (!(cfgVer == CfgVer)) {
            LogUtil.doLog(1, "载入配置文件失败! 配置文件版本不匹配，请前往发布页更新配置文件。", null);
            return 0;
        }

        return 1;
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

    public static int convertFileCharset(File inputFile, File outputFile,String currentCharset ,String targetCharset) {
        try {
            InputStreamReader isr = new InputStreamReader(Files.newInputStream(inputFile.toPath()) ,currentCharset);
            OutputStreamWriter osw = new OutputStreamWriter(Files.newOutputStream(outputFile.toPath()) ,targetCharset);

            int len;
            while((len = isr.read())!=-1){
                osw.write(len);
            }

            osw.close();
            isr.close();
        } catch (Exception e) {
            LogUtil.doLog(1, "转换文件编码时发生错误! 详细信息: " + e, null);
            return 0;
        }

        return 1;
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
            case 5:
                return "BotDebug";
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

    public static HashMap<String, String> readModInfo(String directoryPath) {
        HashMap<String, String> modInfoMap = new HashMap<>();

        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".jar")) {
                    try (JarFile jarFile = new JarFile(filePath.toFile())) {
                        JarEntry entry = jarFile.getJarEntry("mcmod.info");
                        if (entry != null) {
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(entry)))) {
                                String line;
                                StringBuilder jsonString = new StringBuilder();
                                while ((line = reader.readLine()) != null) {
                                    jsonString.append(line);
                                }
                                extractModInfo(jsonString.toString(), modInfoMap);
                            }
                        }
                    } catch (IOException e) {
                        LogUtil.doLog(1, "[ForgeSupport] 读取modInfo时发生错误: " + e, null);
                    }
                }
            });
        } catch (IOException e) {
            LogUtil.doLog(1, "[ForgeSupport] 读取modInfo时发生错误: " + e, null);
        }
        return modInfoMap;
    }

    private static void extractModInfo(String jsonContent, HashMap<String, String> modInfoMap) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            String script = "Java.asJSONCompatible(" + jsonContent + ")";
            Object result = engine.eval(script);
            if (result instanceof java.util.List) {
                java.util.List<?> resultList = (java.util.List<?>) result;
                for (Object obj : resultList) {
                    if (obj instanceof java.util.Map) {
                        java.util.Map<?, ?> map = (java.util.Map<?, ?>) obj;
                        Object modid = map.get("modid");
                        Object version = map.get("version");
                        if (modid != null && version != null) {
                            modInfoMap.put(modid.toString(), version.toString());
                        }
                    }
                }
            }
        } catch (ScriptException e) {
            LogUtil.doLog(1, "[ForgeSupport] 读取modInfo时发生错误: " + e, null);
        }
    }

}
