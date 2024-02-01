package cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Packet;

import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Bot.BotManager;
import cn.serendipityr.EndMinecraftPlusV2.Tools.ConfigUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PacketManager {
    public static int clickVerifies = 0;
    public static String serverShoutCmd = null;
    public static List<Object> npcDetect = new CopyOnWriteArrayList<>();

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
            packetHandler.handleServerPlayerPositionRotationPacket(client, packet, username);
            BotManager.positionList.put(client, packet);
        } else if (packetHandler.checkServerSpawnPlayerPacket(packet)) {
            // 其他玩家位置数据包
            boolean add = true;
            for (String checkName : ConfigUtil.JoinNPCDetect) {
                if (!packetHandler.checkSpawnPlayerName(packet, checkName)) {
                    add = false;
                    break;
                }
            }
            if (add && !npcDetect.contains(packet)) {
                LogUtil.doLog(0, "存在符合条件的NPC: " + Arrays.toString(packetHandler.getSpawnPlayerLocation(packet)), "NPCDetect");
                npcDetect.add(packet);
            }
        } else if (packetHandler.checkServerOpenWindowPacket(packet)) {
            // 收到打开Inventory数据包
            if (ConfigUtil.BotActionDetails) {
                LogUtil.doLog(0, "[" + username + "] 打开可交互窗口: ID - " + packetHandler.getWindowIDFromServerOpenWindowPacket(packet) + " | 名称 - " + packetHandler.getWindowNameFromPacket(packet) + " | 大小 - " + packetHandler.getWindowSlotsFromPacket(packet), "NPCDetect");
            }
        } else if (packetHandler.checkServerWindowItemsPacket(packet)) {
            // 收到Inventory物品数据包
            int windowID = packetHandler.getWindowIDFromWindowItemsPacket(packet);
            Object[] itemStacks = packetHandler.getItemStackFromWindowItemsPacket(packet);
            int windowSize = itemStacks.length;
            if (ConfigUtil.BotActionDetails) {
                LogUtil.doLog(0, "[" + username + "] 收到可交互窗口物品数据: ID - " + windowID + " | 大小 - " + windowSize, "NPCDetect");
            }

            for (int i = 0; i < windowSize; i++) {
                Object item = itemStacks[i];
                if (item == null) {
                    continue;
                }

                String itemName = packetHandler.getItemName(item);
                List<String> itemLore = packetHandler.getItemLore(item);
                if (ConfigUtil.BotActionDetails) {
                    LogUtil.doLog(0, "[" + username + "] 收到可交互窗口物品数据: ID - " + windowID + " | 位置 - " + i + " | 名称: " + itemName + " | Lore: " + itemLore, "NPCDetect");
                }

                boolean interact = false;
                boolean left = true;
                for (String action : ConfigUtil.JoinLobbyItem) {
                    String[] actionParts = action.split(":");
                    if (actionParts[0].equals("RIGHT")) {
                        left = false;
                    }

                    String actionValue = actionParts[1];
                    if ((itemLore != null && itemLore.stream().anyMatch(lore -> lore.contains(actionValue))) || (itemName != null && itemName.contains(actionValue))) {
                        interact = true;
                        break;
                    }
                }

                if (interact) {
                    LogUtil.doLog(0, "[" + username + "] 已发送窗口点击数据: ID - " + windowID + " | 位置 - " + i, "NPCDetect");
                    if (left) {
                        packetHandler.sendLeftClickWindowItemPacket(client, windowID, i, item);
                    } else {
                        packetHandler.sendRightClickWindowItemPacket(client, windowID, i, item);
                    }
                    break;
                }
            }
        } else {
            // packetHandler.handleOtherPacket(packet);
        }
    }

    public static void clickVerifiesHandle(PacketHandler packetHandler, Object client, Object message, String username) {
        if (packetHandler.hasMessageClickEvent(message)) {
            boolean needClick = false;

            String msg = packetHandler.getMessageText(message);
            String value = packetHandler.getClickValue(message);

            for (String clickVerifiesDetect : ConfigUtil.ClickVerifiesDetect) {
                if (msg.contains(clickVerifiesDetect)) {
                    needClick = true;
                    break;
                }
            }

            for (String teleportMsg : ConfigUtil.ServerShoutDetect) {
                if (value.contains(teleportMsg)) {
                    serverShoutCmd = value;
                    needClick = true;
                    break;
                }
            }

            if (needClick) {
                LogUtil.doLog(0, "[服务端返回可交互信息] [" + username + "] " + value, "BotAttack");
                packetHandler.sendChatPacket(client, value);
                clickVerifies++;
            }
        }

        if (packetHandler.hasMessageExtra(message)) {
            packetHandler.handleMessageExtra(packetHandler, message, client, username);
        }
    }
}
