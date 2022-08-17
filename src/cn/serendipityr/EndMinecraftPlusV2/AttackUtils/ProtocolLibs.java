package cn.serendipityr.EndMinecraftPlusV2.AttackUtils;

import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.OtherUtils;

import java.io.File;
import java.util.*;

public class ProtocolLibs {
    public static void loadProtocolLib() {
        LogUtil.doLog(0, "==========================================================", "ProtocolLib");
        List<File> versionLibs = scanProtocolLibs();
        choseProtocolVer(versionLibs);
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
            if (file.getName().startsWith("MC-") && file.getName().endsWith(".jar"))
                versionLibs.add(file);
        }

        Collections.sort(versionLibs);
        return versionLibs;
    }

    public static void choseProtocolVer(List<File> versionLibs) {
        for (int i = 0; i < versionLibs.size(); i++) {
            String filename = versionLibs.get(i).getName();
            StringBuilder info = new StringBuilder();
            info.append("(").append(i + 1).append(")").append(filename, "MC-".length(), filename.length() - ".jar".length());
            LogUtil.doLog(0, info.toString(), "ProtocolLib");
        }

        LogUtil.doLog(-1, "请选择一个Minecraft协议库版本: ", "ProtocolLib");

        try {
            Scanner scanner = new Scanner(System.in);
            int sel = Integer.parseInt(scanner.nextLine());
            File versionLib = versionLibs.get(sel - 1);
            OtherUtils.loadLibrary(versionLib);
        } catch (Exception e) {
            LogUtil.emptyLog();
            LogUtil.doLog(1, "加载Minecraft协议库时发生错误! 详细信息:" + e, null);
            LogUtil.doLog(0, "==========================错误排除==========================", "ProtocolLib");
            LogUtil.doLog(0, "1.检查/libs文件夹内依赖库是否完整", "ProtocolLib");
            LogUtil.doLog(0, "2.检查对应依赖库是否存在(如[1.8]需要[MC-1.8.jar])", "ProtocolLib");
            LogUtil.doLog(0, "3.请输入正确的协议库序号(如10)", "ProtocolLib");
            LogUtil.doLog(0, "==========================================================", "ProtocolLib");
            LogUtil.emptyLog();

            choseProtocolVer(versionLibs);
        }
    }
}
