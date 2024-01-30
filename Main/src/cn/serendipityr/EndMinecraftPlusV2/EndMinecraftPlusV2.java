package cn.serendipityr.EndMinecraftPlusV2;

import cn.serendipityr.EndMinecraftPlusV2.Tools.ConfigUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.ProxyUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.SetTitle;
import cn.serendipityr.EndMinecraftPlusV2.AttackManager.AttackManager;
import cn.serendipityr.EndMinecraftPlusV2.VersionManager.ProtocolLibs;

public class EndMinecraftPlusV2 {
    public static String ver = "2.0.1";
    public static Integer CfgVer = 8;

    public static void main(String[] args) {
        System.out.println("=======================-Designed by SerendipityR-=======================");
        System.out.println(" EndMinecraft原作者 - @iuli-moe");
        System.out.println(" Github发布页: https://github.com/SerendipityR-2022/EndMinecraftPlusV2");
        System.out.println(" EndMinecraftPlusV2 (Ver: " + ver + ")" + " is loading......");
        System.out.println("========================================================================");
        try {
            SetTitle.INSTANCE.SetConsoleTitleA("EndMinecraftPlusV2 - Designed by SerendipityR");
        } catch (Throwable e) {
            ConfigUtil.isLinux = true;
        }
        LogUtil.emptyLog();
        prepareConfig();
        prepareProxy();
        startAttack();
    }

    public static void prepareConfig() {
        LogUtil.doLog(0, "正在载入配置文件...", "CFGUtil");
        int result = new ConfigUtil().loadConfig(CfgVer);

        if (result == 0) {
            Exit();
        }
    }

    public static void prepareProxy() {
        if (!ConfigUtil.AttackMethod.equals(2)) {
            LogUtil.doLog(0, "正在获取代理...", "ProxyUtil");
            ProxyUtil.getProxies();
            ProxyUtil.runUpdateProxiesTask(ConfigUtil.ProxyUpdateTime);
        }
    }

    public static void startAttack() {
        LogUtil.doLog(0, "正在载入Minecraft协议库...", "ProtocolLib");
        ProtocolLibs.loadProtocolLib();
        AttackManager.doAttack();
    }

    public static void Exit() {
        LogUtil.doLog(0, "程序退出...", "INFO");
        System.exit(0);
    }
}