package cn.serendipityr.EndMinecraftPlusV2.VersionManager;

import cn.serendipityr.EndMinecraftPlusV2.Tools.ConfigUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.OtherUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class ProtocolLibs {
    public static int currentVersion;
    static boolean newVersion = false;

    public static void loadProtocolLib() {
        if (ConfigUtil.AttackMethod.equals(2) || ConfigUtil.AttackMethod.equals(3)) {
            return;
        }

        LogUtil.doLog(0, "==========================================================", "ProtocolLib");
        choseProtocolVer(scanProtocolLibs());

        currentVersion = getProtocolVersion();
    }

    public static int getProtocolVersion() {
        // 根据版本标志确定包前缀。
        String packagePrefix = newVersion ? "com.github.steveice10.mc.protocol" : "org.spacehq.mc.protocol";

        // 尝试从ProtocolConstants或MinecraftConstants类获取协议版本。
        try {
            Class<?> cls = getClass(packagePrefix + ".ProtocolConstants", packagePrefix + ".MinecraftConstants");
            Field field = cls.getDeclaredField("PROTOCOL_VERSION");
            return field.getInt(null); // 静态字段，因此使用null作为对象参数。
        } catch (Exception ignored) {
            // 如果出现任何异常，继续尝试其他方式。
        }

        // 如果从常量获取失败且是新版本，尝试从编解码器类获取。
        if (newVersion) {
            try {
                Class<?> codecClass = Class.forName(packagePrefix + ".codec.MinecraftCodec");
                Field codecField = codecClass.getField("CODEC");
                Object codecObject = codecField.get(null); // 静态字段，因此使用null。

                Field protocolVersionField = codecObject.getClass().getDeclaredField("protocolVersion");
                protocolVersionField.setAccessible(true); // 确保私有字段可访问。
                return (Integer) protocolVersionField.get(codecObject);
            } catch (Exception ignored) {
                // 如果出现任何异常，继续尝试其他方式。
            }
        }

        // 如果所有尝试都失败，则返回默认值。
        return -1;
    }

    // 辅助方法，尝试按名称加载类，如果首选名称失败，则尝试第二个名称。
    private static Class<?> getClass(String primaryName, String secondaryName) throws ClassNotFoundException {
        try {
            return Class.forName(primaryName);
        } catch (ClassNotFoundException e) {
            return Class.forName(secondaryName);
        }
    }

    public static List<File> scanProtocolLibs() {
        try {
            Class.forName("javassist.CtClass");
        } catch (ClassNotFoundException e) {
            OtherUtils.loadLibrary(new File("libs", "javassist-3.22.0-CR2.jar"));
        }

        File libDir = new File("libs");
        if (!libDir.exists() && libDir.mkdir()) {
            LogUtil.doLog(0, "未找到[/libs]文件夹，请确认依赖库被正确放置。", "ProtocolLib");
        }

        List<File> versionLibs = new ArrayList<>();
        for (File file : Objects.requireNonNull(libDir.listFiles())) {
            if ((file.getName().startsWith("MC-") || file.getName().startsWith("MCP-")) && file.getName().endsWith(".jar"))
                versionLibs.add(file);
        }

        Collections.sort(versionLibs);
        return versionLibs;
    }

    public static void choseProtocolVer(List<File> versionLibs) {
        // 使用自定义比较器对versionLibs列表进行排序
        versionLibs.sort((f1, f2) -> {
            String v1 = f1.getName().replaceAll("[^\\d.]", "");
            String v2 = f2.getName().replaceAll("[^\\d.]", "");
            return compareVersionStrings(v1, v2);
        });

        // 显示排序后的版本库
        for (int i = 0; i < versionLibs.size(); i++) {
            File file = versionLibs.get(i);
            String filename = file.getName();
            String version = filename.substring(filename.indexOf("-") + 1, filename.lastIndexOf("."));
            LogUtil.doLog(0, "(" + (i + 1) + ") " + version, "ProtocolLib");
        }

        LogUtil.doLog(-1, "请选择一个Minecraft协议库版本: ", "ProtocolLib");

        try {
            Scanner scanner = new Scanner(System.in);
            int sel = Integer.parseInt(scanner.nextLine());
            File versionLib = versionLibs.get(sel - 1);

            if (versionLib.getName().contains("MCP")) {
                newVersion = true;
            }

            OtherUtils.loadLibrary(versionLib);
            LogUtil.doLog(0, "当前协议库版本: " + getProtocolVersion(), "ProtocolLib");
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

            choseProtocolVer(scanProtocolLibs());
        }
    }

    // 版本号比较方法
    public static int compareVersionStrings(String v1, String v2) {
        String[] v1Components = v1.split("\\.");
        String[] v2Components = v2.split("\\.");

        int length = Math.max(v1Components.length, v2Components.length);
        for (int i = 0; i < length; i++) {
            int v1Part = i < v1Components.length ? Integer.parseInt(v1Components[i]) : 0;
            int v2Part = i < v2Components.length ? Integer.parseInt(v2Components[i]) : 0;
            if (v1Part < v2Part) {
                return -1;
            } else if (v1Part > v2Part) {
                return 1;
            }
        }
        return 0;
    }
}
