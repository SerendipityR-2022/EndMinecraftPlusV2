package cn.serendipityr.EndMinecraftPlusV2.AttackManager;

import cn.serendipityr.EndMinecraftPlusV2.EndMinecraftPlusV2;
import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Bot.BotHandler;
import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Bot.BotManager;
import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Packet.PacketHandler;
import cn.serendipityr.EndMinecraftPlusV2.VersionManager.ProtocolLibs;
import cn.serendipityr.EndMinecraftPlusV2.Tools.ConfigUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.DataUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;

public class AttackManager {
    public static Boolean isDoubleAttack = false;

    public static void doAttack() {
        LogUtil.emptyLog();
        DataUtil.loadData();

        switch (ConfigUtil.AttackMethod) {
            case 1:
            case 4:
            case 5:
                // BotDebug & BotAttack
                BotHandler botHandler;
                PacketHandler packetHandler;
                if (ProtocolLibs.currentVersion >= 4 && ProtocolLibs.currentVersion <= 5) {
                    // 1.7.X
                    botHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_7_X.BotHandler();
                    packetHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_7_X.PacketHandler();
                } else if (ProtocolLibs.currentVersion == 47) {
                    // 1.8.X
                    botHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_8_X.BotHandler();
                    packetHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_8_X.PacketHandler();
                } else if (ProtocolLibs.currentVersion <= 338) {
                    // 1.9.X-1.12.1
                    botHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_9_X_1_12_1.BotHandler();
                    packetHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_9_X_1_12_1.PacketHandler();
                } else if (ProtocolLibs.currentVersion <= 340) {
                    // 1.12.2
                    botHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_12_2.BotHandler();
                    packetHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_12_2.PacketHandler();
                } else if (ProtocolLibs.currentVersion <= 404) {
                    // 1.13.X
                    botHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_13_X.BotHandler();
                    packetHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_13_X.PacketHandler();
                } else if (ProtocolLibs.currentVersion <= 498) {
                    // 1.14.X
                    botHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_14_X.BotHandler();
                    packetHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_14_X.PacketHandler();
                } else if (ProtocolLibs.currentVersion <= 578) {
                    // 1.15.X
                    botHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_15_X.BotHandler();
                    packetHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_15_X.PacketHandler();
                } else if (ProtocolLibs.currentVersion <= 754) {
                    // 1.16.X
                    botHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_16_X.BotHandler();
                    packetHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_16_X.PacketHandler();
                } else if (ProtocolLibs.currentVersion <= 756) {
                    // 1.17.X
                    botHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_17_X.BotHandler();
                    packetHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_17_X.PacketHandler();
                } else if (ProtocolLibs.currentVersion <= 758) {
                    // 1.18.X
                    botHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_18_X.BotHandler();
                    packetHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_18_X.PacketHandler();
                } else if (ProtocolLibs.currentVersion <= 759) {
                    // 1.19
                    botHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_19.BotHandler();
                    packetHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_19.PacketHandler();
                } else if (ProtocolLibs.currentVersion <= 760) {
                    // 1.19.1-1.19.2
                    botHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_19_1_1_19_2.BotHandler();
                    packetHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_19_1_1_19_2.PacketHandler();
                } else if (ProtocolLibs.currentVersion <= 763) {
                    // 1.19.3-1.20.1
                    botHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_19_3_1_20_1.BotHandler();
                    packetHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_19_3_1_20_1.PacketHandler();
                } else {
                    // 1.20.1-(Subsequent Versions)
                    botHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_20_X.BotHandler();
                    packetHandler = new cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_20_X.PacketHandler();
                }

                if (ProtocolLibs.currentVersion > 404 && ConfigUtil.ForgeSupport) {
                    LogUtil.doLog(0, "选定的协议库版本无法启用Forge支持。", "ForgeSupport");
                }

                BotManager botManager = new BotManager(botHandler, packetHandler);

                if (ConfigUtil.AttackMethod.equals(5)) {
                    botManager.test();
                } else {
                    botManager.startTask(isDoubleAttack, ProtocolLibs.getProtocolVersion());
                }
                break;
            case 2:
                // MotdAttack
                MotdAttack.doAttack(false);
                break;
            case 3:
                // MotdAttack(P)
                MotdAttack.doAttack(true);
                break;
            default:
                LogUtil.doLog(1, "攻击类型错误，停止运行。", null);
                EndMinecraftPlusV2.Exit();
        }
    }
}
