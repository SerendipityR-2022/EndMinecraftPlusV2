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
    public static Boolean ShowServerMessages;
    public static Boolean ServerCrasher;
    public static Integer ServerCrasherMode;
    public static Long ServerCrasherPacketDelay;

    public void loadConfig() {
        try {
            configFile = new File("config.yml");

            if (!configFile.exists()) {
                LogUtil.doLog(1, "????????????????????????! ??????????????????", null);
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
            ShowServerMessages = config.getBoolean("BotSettings.ShowServerMessages");
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
                    LogUtil.doLog(1, "CustomScreenShot????????????CatAntiCheat????????????????????????", null);
                    CatAntiCheat = false;
                }

                if (CACLoadedClass.isEmpty()) {
                    LogUtil.doLog(1, "LoadedClass?????????CatAntiCheat????????????????????????", null);
                    CatAntiCheat = false;
                }

                if (Objects.requireNonNull(CACLoadedMods.listFiles()).length <= 5) {
                    LogUtil.doLog(1, "LoadedMods????????????(<=5)???CatAntiCheat????????????????????????", null);
                    CatAntiCheat = false;
                }

                if (!ForgeSupport) {
                    LogUtil.doLog(1, "ForgeSupport????????????CatAntiCheat????????????????????????", null);
                    CatAntiCheat = false;
                } else {
                    ForgeModList.put("catanticheat", "1.2.6");
                }
            }

            RandomMAC = config.getBoolean("AdvancedSettings.MACChecker.RandomMAC");
            ServerCrasher = config.getBoolean("AdvancedSettings.ServerCrasher.Enable");
            ServerCrasherMode = config.getInt("AdvancedSettings.ServerCrasher.Mode");
            ServerCrasherPacketDelay = config.getLong("AdvancedSettings.ServerCrasher.PacketDelay");

            checkSRV();

            LogUtil.doLog(0, "==============================================================", "CFGUtil");
            LogUtil.doLog(0, "???????????????: " + AttackAddress, "CFGUtil");
            LogUtil.doLog(0, "???????????????: " + AttackPort, "CFGUtil");
            LogUtil.doLog(0, "????????????: " + getAttackMethod(AttackMethod), "CFGUtil");
            LogUtil.doLog(0, "????????????: " + AttackTime + "???", "CFGUtil");
            LogUtil.doLog(0, "????????????: " + timeToSeconds(ConnectDelay) + "???", "CFGUtil");
            LogUtil.doLog(0, "???????????????: " + MaxConnections + "???", "CFGUtil");
            LogUtil.doLog(0, "Forge??????: " + booleanToStr(ForgeSupport), "CFGUtil");
            LogUtil.doLog(0, "????????????Tab??????: " + booleanToStr(TabAttack), "CFGUtil");
            LogUtil.doLog(0, "AntiAttack??????: " + booleanToStr(AntiAttackMode), "CFGUtil");
            LogUtil.doLog(0, "CatAntiCheat??????: " + booleanToStr(CatAntiCheat), "CFGUtil");
            LogUtil.doLog(0, "????????????: " + getProxyFrom(ProxyGetType), "CFGUtil");
            LogUtil.doLog(0, "??????API: " + ProxyAPIs.size() + "???", "CFGUtil");
            LogUtil.doLog(0, "??????????????????: " + ProxyUpdateTime + "???", "CFGUtil");
            LogUtil.doLog(0, "==============================================================", "CFGUtil");
            LogUtil.emptyLog();
        } catch (Exception e) {
            LogUtil.emptyLog();
            LogUtil.doLog(1, "????????????????????????! ????????????: " + e, null);
            LogUtil.doLog(-1, "??????????????????????????????????????????????????????????????????????????? [y/n]:", "CFGUtil");
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

                LogUtil.doLog(0, "??????????????????????????????: " + currentCharset + " | ???????????????: " + getFileCharset(configFile) , "CFGUtil");
            }

            loadConfig();
        }

        if (!EndMinecraftPlusV2.CfgVer.equals(CfgVer)) {
            LogUtil.doLog(1, "????????????????????????! ?????????????????????????????????????????????????????????????????????", null);
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
                return charset; // ??????????????? ANSI
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; // ??????????????? Unicode
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; // ??????????????? Unicode big endian
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; // ??????????????? UTF-8
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
            LogUtil.doLog(1, "?????????????????????????????????! ????????????: " + e, null);
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
        return type ? "??????" : "??????";
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
                LogUtil.doLog(0,"??????: " + AttackAddress, "CheckSRV");
                LogUtil.doLog(0,"?????????: " + re[3], "CheckSRV");
                LogUtil.doLog(0,"?????????: " + re[2], "CheckSRV");
                LogUtil.doLog(-1,"????????????????????????SRV??????????????????????????????SRV???????????????[y/n]: ", "CheckSRV");
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
