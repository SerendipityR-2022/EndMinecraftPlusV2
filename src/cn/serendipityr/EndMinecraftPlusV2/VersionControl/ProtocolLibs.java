package cn.serendipityr.EndMinecraftPlusV2.VersionControl;

import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.OtherUtils;
import cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ForgeProtocol.MCForge;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundChatPacket;

import java.io.File;
import java.util.*;

public class ProtocolLibs {
    public static boolean highVersion = false;
    public static boolean adaptAfter578 = false;
    public static boolean adaptAfter754 = false;
    public static boolean adaptAfter758 = false;
    public static boolean adaptAfter759 = false;
    public static boolean adaptAfter760 = false;
    public static void loadProtocolLib() {
        LogUtil.doLog(0, "==========================================================", "ProtocolLib");
        choseProtocolVer(scanProtocolLibs(), scanSupportLibs());

        int currentVersion = MCForge.getProtocolVersion();

        if (currentVersion > 498 || currentVersion == -1) {
            adaptAfter578 = true;
        }

        if (currentVersion > 578 || currentVersion == -1) {
            adaptAfter754 = true;
        }

        if (currentVersion == -1) {
            adaptAfter758 = true;
        }

        try {
            Class.forName("com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket");
            adaptAfter759 = true;
        } catch (ClassNotFoundException ignored) {}
    }

    public static List<File> scanProtocolLibs() {
        try {
            Class.forName("javassist.CtClass");
        } catch (ClassNotFoundException e) {
            OtherUtils.loadLibrary(new File("libs", "javassist-3.22.0-CR2.jar"));
        }

        File libDir = new File("libs");
        if (!libDir.exists()) {
            libDir.mkdir();
        }

        List<File> versionLibs = new ArrayList<>();
        for (File file: Objects.requireNonNull(libDir.listFiles())) {
            if ((file.getName().startsWith("MC-") || file.getName().startsWith("MCP-")) && file.getName().endsWith(".jar"))
                versionLibs.add(file);
        }

        Collections.sort(versionLibs);
        return versionLibs;
    }

    public static List<File> scanSupportLibs() {
        File libDir = new File("libs");
        if (!libDir.exists()) {
            libDir.mkdir();
        }

        List<File> supportLibs = new ArrayList<>();
        for (File file: Objects.requireNonNull(libDir.listFiles())) {
            if (file.getName().endsWith(".jar"))
                supportLibs.add(file);
        }

        return supportLibs;
    }

    public static void choseProtocolVer(List<File> versionLibs, List<File> supportLibs) {
        for (int i = 0; i < versionLibs.size(); i++) {
            String filename = versionLibs.get(i).getName();
            StringBuilder info = new StringBuilder();

            if (filename.contains("MC-")) {
                info.append("(").append(i + 1).append(")").append(" ").append(filename, "MC-".length(), filename.length() - ".jar".length());
            } else if (filename.contains("MCP-")) {
                info.append("(").append(i + 1).append(")").append(" ").append(filename, "MCP-".length(), filename.length() - ".jar".length());
            }

            LogUtil.doLog(0, info.toString(), "ProtocolLib");
        }

        LogUtil.doLog(-1, "请选择一个Minecraft协议库版本: ", "ProtocolLib");

        try {
            Scanner scanner = new Scanner(System.in);
            int sel = Integer.parseInt(scanner.nextLine());
            File versionLib = versionLibs.get(sel - 1);

            if (versionLib.getName().contains("MCP")) {
                highVersion = true;
            }

            if (versionLib.getName().contains("1.19.1")) {
                adaptAfter760 = true;
            }

            OtherUtils.loadLibrary(versionLib);
        } catch (Exception e) {
            LogUtil.emptyLog();
            LogUtil.doLog(1, "加载Minecraft协议库时发生错误! 详细信息:" + e, null);
            LogUtil.doLog(0, "=========================错误排除=========================", "ProtocolLib");
            LogUtil.doLog(0, " 1.检查[/libs]文件夹内依赖库是否完整", "ProtocolLib");
            LogUtil.doLog(0, " 2.检查对应依赖库是否存在", "ProtocolLib");
            LogUtil.doLog(0, "   (如[1.8]需要[MC-1.8.jar])", "ProtocolLib");
            LogUtil.doLog(0, " 3.请输入正确的协议库序号(如10)", "ProtocolLib");
            LogUtil.doLog(0, "==========================================================", "ProtocolLib");
            LogUtil.emptyLog();

            choseProtocolVer(scanProtocolLibs(), scanSupportLibs());
        }
    }
}
