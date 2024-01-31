package cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Packet;

import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Bot.BotManager;
import cn.serendipityr.EndMinecraftPlusV2.Tools.ConfigUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;

public class PacketManager {
    public static int clickVerifies = 0;

    public static void handlePacket(PacketHandler packetHandler, Object client, Object packet, String username) {
        if (packetHandler.checkServerPluginMessagePacket(packet)) {
            // 插件消息包
            packetHandler.handleServerPluginMessagePacket(client, packet, username);
        } else if (packetHandler.checkServerJoinGamePacket(packet)) {
            // 游戏设置包
            if (BotManager.doubleAttack) {
                BotManager.botHandler.disconnect(client);
                return;
            }
            packetHandler.handleServerJoinGamePacket(client, packet, username);
        } else if (packetHandler.checkServerKeepAlivePacket(packet)) {
            // 心跳包
            if (ConfigUtil.KeepAlive) {
                packetHandler.handleServerKeepAlivePacket(client, packet, username);
            }
        } else if (packetHandler.checkServerChatPacket(packet)) {
            // 聊天信息包
            packetHandler.handleServerChatPacket(client, packet, username);
            Object message = packetHandler.getMessageFromPacket(packet);
            // 处理点击信息
            clickVerifiesHandle(packetHandler, client, message, username);
        } else if (packetHandler.checkServerPlayerHealthPacket(packet)) {
            // 血量数据包
            packetHandler.handleServerPlayerHealthPacket(client, packet, username);
        } else if (packetHandler.checkServerPlayerPositionRotationPacket(packet)) {
            // 移动数据包
            if (ConfigUtil.PacketHandlerMove) {
                packetHandler.handleServerPlayerPositionRotationPacket(client, packet, username);
            }
            BotManager.positionList.put(client, packet);
        } else {
            // packetHandler.handleOtherPacket(packet);
        }
    }

    public static void clickVerifiesHandle(PacketHandler packetHandler, Object client, Object message, String username) {
        boolean needClick = false;

        if (packetHandler.hasMessageClickEvent(message)) {
            for (String clickVerifiesDetect : ConfigUtil.ClickVerifiesDetect) {
                if (packetHandler.getMessageText(message).contains(clickVerifiesDetect)) {
                    needClick = true;
                    break;
                }
            }
        }

        if (needClick) {
            String value = packetHandler.getClickValue(message);
            LogUtil.doLog(0, "[服务端返回验证信息] [" + username + "] " + value, "BotAttack");
            packetHandler.sendChatPacket(client, value);
            clickVerifies++;
        }

        if (packetHandler.hasMessageExtra(message)) {
            packetHandler.handleMessageExtra(packetHandler, message, client, username);
        }
    }
}
