package cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_17_X;

import cn.serendipityr.EndMinecraftPlusV2.AdvanceModule.ACProtocol.AnotherStarAntiCheat;
import cn.serendipityr.EndMinecraftPlusV2.AdvanceModule.ACProtocol.AntiCheat3;
import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Bot.BotManager;
import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Packet.PacketManager;
import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.UniverseMethods;
import cn.serendipityr.EndMinecraftPlusV2.Tools.ConfigUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.OtherUtils;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.player.HandPreference;
import com.github.steveice10.mc.protocol.data.game.entity.player.InteractAction;
import com.github.steveice10.mc.protocol.data.game.setting.ChatVisibility;
import com.github.steveice10.mc.protocol.data.game.setting.SkinPart;
import com.github.steveice10.mc.protocol.data.game.window.ClickItemParam;
import com.github.steveice10.mc.protocol.data.game.window.WindowAction;
import com.github.steveice10.mc.protocol.packet.ingame.client.*;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.*;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.*;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.StringTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;

import java.util.*;
import java.util.stream.Collectors;

public class PacketHandler implements cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Packet.PacketHandler {
    private double selfX = 0;
    private double selfY = 0;
    private double selfZ = 0;

    @Override
    public boolean checkServerPluginMessagePacket(Object packet) {
        return packet instanceof ServerPluginMessagePacket;
    }

    @Override
    public void handleServerPluginMessagePacket(Object client, Object recvPacket, String username) {
        ServerPluginMessagePacket packet = (ServerPluginMessagePacket) recvPacket;
        TcpClientSession session = (TcpClientSession) client;

        switch (packet.getChannel()) {
            case "AntiCheat3.4.3":
                AntiCheat3 ac3 = new AntiCheat3();
                String code = ac3.uncompress(packet.getData());
                byte[] checkData = ac3.getCheckData("AntiCheat3.jar", code, new String[]{"44f6bc86a41fa0555784c255e3174260"});
                sendClientPluginMessagePacket(session, "AntiCheat3.4.3", checkData);
                break;
            case "anotherstaranticheat":
                AnotherStarAntiCheat asac = new AnotherStarAntiCheat();
                String salt = asac.decodeSPacket(packet.getData());
                byte[] data = asac.encodeCPacket(new String[]{"4863f8708f0c24517bb5d108d45f3e15"}, salt);
                sendClientPluginMessagePacket(session, "anotherstaranticheat", data);
                break;
            case "VexView":
                if (new String(packet.getData()).equals("GET:Verification")) {
                    sendClientPluginMessagePacket(session, "VexView", "Verification:1.8.10".getBytes());
                }
                break;
            case "MAC|Check":
                if (ConfigUtil.RandomMAC) {
                    byte[] MACAddress = UniverseMethods.getRandomMAC();
                    LogUtil.doLog(0, "[" + username + "] 已发送随机MACAddress数据。(" + Arrays.toString(MACAddress) + ")", "MACCheck");
                    sendClientPluginMessagePacket(session, packet.getChannel(), MACAddress);
                }
                break;
            default:
                // 收到插件消息
        }
    }

    @Override
    public boolean checkServerJoinGamePacket(Object packet) {
        return packet instanceof ServerJoinGamePacket;
    }

    @Override
    public void handleServerJoinGamePacket(Object client, Object recvPacket, String username) {
        TcpClientSession session = (TcpClientSession) client;
        session.setFlag("join", true);
        LogUtil.doLog(0, "[假人加入服务器] [" + username + "]", "BotAttack");

        sendClientSettingPacket(session, "zh_CN");
        sendClientChangeHeldItemPacket(session, 1);
    }

    @Override
    public boolean checkServerPlayerPositionRotationPacket(Object packet) {
        return packet instanceof ServerPlayerPositionRotationPacket;
    }

    @Override
    public void handleServerPlayerPositionRotationPacket(Object client, Object recvPacket, String username) {
        TcpClientSession session = (TcpClientSession) client;
        ServerPlayerPositionRotationPacket positionRotationPacket = (ServerPlayerPositionRotationPacket) recvPacket;
        selfX = positionRotationPacket.getX();
        selfY = positionRotationPacket.getY();
        selfZ = positionRotationPacket.getZ();
        if (ConfigUtil.PacketHandlerMove) {
            sendClientPlayerMovementPacket(session, true);
            ClientTeleportConfirmPacket teleportConfirmPacket = new ClientTeleportConfirmPacket(positionRotationPacket.getTeleportId());
            session.send(teleportConfirmPacket);
            sendClientPlayerMovementPacket(session, true);
        }
    }

    @Override
    public boolean checkServerChatPacket(Object packet) {
        return packet instanceof ServerChatPacket;
    }

    @Override
    public void handleServerChatPacket(Object client, Object recvPacket, String username) {
        ServerChatPacket chatPacket = (ServerChatPacket) recvPacket;
        Component message = chatPacket.getMessage();
        if (ConfigUtil.ShowServerMessages && !getMessageText(message).equals("")) {
            LogUtil.doLog(0, "[服务端返回信息] [" + username + "] " + getMessageText(message), "BotAttack");
        }
    }

    @Override
    public boolean checkServerKeepAlivePacket(Object packet) {
        return packet instanceof ServerKeepAlivePacket;
    }

    @Override
    public void handleServerKeepAlivePacket(Object client, Object recvPacket, String username) {
        TcpClientSession session = (TcpClientSession) client;
        ServerKeepAlivePacket serverKeepAlivePacket = (ServerKeepAlivePacket) recvPacket;
        sendClientKeepAlivePacket(session, serverKeepAlivePacket.getPingId());
    }

    @Override
    public boolean checkServerPlayerHealthPacket(Object packet) {
        return packet instanceof ServerPlayerHealthPacket;
    }

    @Override
    public void handleServerPlayerHealthPacket(Object client, Object recvPacket, String username) {
        TcpClientSession session = (TcpClientSession) client;
        ServerPlayerHealthPacket serverPlayerHealthPacket = (ServerPlayerHealthPacket) recvPacket;
        double health = serverPlayerHealthPacket.getHealth();
        if (health <= 0) {
            sendRespawnPacket(session);
            LogUtil.doLog(0, "[" + username + "] " + "假人于服务器中死亡，已重生。", "BotAttack");
        }
    }

    @Override
    public boolean checkServerSpawnPlayerPacket(Object packet) {
        return packet instanceof ServerPlayerListEntryPacket;
    }

    @Override
    public boolean checkSpawnPlayerName(Object packet, String checkName) {
        return false;
    }

    @Override
    public List<String> getSpawnPlayerMetadata(Object packet) {
        return null;
    }

    @Override
    public Double[] getSpawnPlayerLocation(Object packet) {
        ServerSpawnPlayerPacket playerPacket = (ServerSpawnPlayerPacket) packet;
        return new Double[]{playerPacket.getX(), playerPacket.getY(), playerPacket.getZ()};
    }

    @Override
    public int getSpawnPlayerEntityId(Object packet) {
        ServerSpawnPlayerPacket playerPacket = (ServerSpawnPlayerPacket) packet;
        return playerPacket.getEntityId();
    }


    @Override
    public void moveToLocation(Object client, Double[] targetLocation, double moveSpeed) {
        TcpClientSession session = (TcpClientSession) client;

        // 设置初始目标位置
        double targetX = targetLocation[0];
        double targetY = targetLocation[1];
        double targetZ = targetLocation[2];

        if (!BotManager.positionList.containsKey(client)) {
            return;
        }

        boolean movedLastTime = true; // 标记上次是否移动成功
        boolean moveYFirst = true; // 标记是否首先移动Y轴

        // 持续移动直到接近目标位置
        while (true) {
            double previousX = selfX;
            double previousY = selfY;
            double previousZ = selfZ;

            double distanceX = selfX - targetX;
            double distanceY = selfY - targetY;
            double distanceZ = selfZ - targetZ;
            double totalDistance = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);

            // 检查是否已经足够接近目标
            if (totalDistance <= moveSpeed) {
                break;  // 已经接近目标，退出循环
            }

            if ((Math.abs(distanceY) > Math.abs(distanceX) && moveYFirst) || !movedLastTime) {
                // 优先移动Y轴，或者上次移动失败时尝试移动Y轴
                double stepRatioY = Math.min(moveSpeed / Math.abs(distanceY), 1.0);
                selfY -= distanceY * stepRatioY;
                moveYFirst = !moveYFirst; // 下次尝试另一个方向
            } else {
                // 移动X轴和Z轴
                double stepRatioXZ = Math.min(moveSpeed / Math.sqrt(distanceX * distanceX + distanceZ * distanceZ), 1.0);
                selfX -= distanceX * stepRatioXZ;
                selfZ -= distanceZ * stepRatioXZ;
                moveYFirst = !moveYFirst; // 下次尝试另一个方向
            }

            ClientPlayerPositionPacket playerPositionPacket = new ClientPlayerPositionPacket(true, selfX, selfY, selfZ);
            session.send(playerPositionPacket);

            OtherUtils.doSleep(100); // 暂停以等待服务器响应

            // 检查是否成功移动
            movedLastTime = (previousX != selfX || previousY != selfY || previousZ != selfZ);
        }
    }

    @Override
    public boolean checkServerOpenWindowPacket(Object packet) {
        return packet instanceof ServerOpenWindowPacket;
    }

    @Override
    public int getWindowIDFromServerOpenWindowPacket(Object packet) {
        ServerOpenWindowPacket windowPacket = (ServerOpenWindowPacket) packet;
        return windowPacket.getWindowId();
    }

    @Override
    public int getWindowSlotsFromPacket(Object packet) {
        return -1;
    }

    @Override
    public String getWindowNameFromPacket(Object packet) {
        ServerOpenWindowPacket windowPacket = (ServerOpenWindowPacket) packet;
        return windowPacket.getName();
    }

    @Override
    public boolean checkServerWindowItemsPacket(Object packet) {
        return packet instanceof ServerWindowItemsPacket;
    }

    @Override
    public int getWindowIDFromWindowItemsPacket(Object packet) {
        ServerWindowItemsPacket windowItemsPacket = (ServerWindowItemsPacket) packet;
        return windowItemsPacket.getWindowId();
    }

    @Override
    public Object[] getItemStackFromWindowItemsPacket(Object packet) {
        ServerWindowItemsPacket windowItemsPacket = (ServerWindowItemsPacket) packet;
        return windowItemsPacket.getItems();
    }

    @Override
    public String getItemName(Object itemStack) {
        if (itemStack == null) {
            return null;
        }
        ItemStack item = (ItemStack) itemStack;
        CompoundTag nbtData = item.getNbt();
        HashMap<?, ?> hashMap = (HashMap<?, ?>) nbtData.get("display").getValue();
        if (hashMap.get("Name") == null) {
            return null;
        }
        return ((StringTag) hashMap.get("Name")).getValue();
    }

    @Override
    public List<String> getItemLore(Object itemStack) {
        if (itemStack == null) {
            return null;
        }
        ItemStack item = (ItemStack) itemStack;
        CompoundTag nbtData = item.getNbt();
        HashMap<?, ?> hashMap = (HashMap<?, ?>) nbtData.get("display").getValue();
        if (hashMap.get("Lore") == null) {
            return null;
        }
        List<Tag> itemLore = ((ListTag) hashMap.get("Lore")).getValue();
        List<String> loreList = new ArrayList<>();
        for (Tag tag:itemLore) {
            loreList.add((String) tag.getValue());
        }
        return loreList;
    }

    @Override
    public void sendPlayerInteractEntityPacket(Object client, int entityId, float[] location) {
        TcpClientSession session = (TcpClientSession) client;
        ClientPlayerInteractEntityPacket interactEntityPacket = new ClientPlayerInteractEntityPacket(entityId, InteractAction.INTERACT, false);
        session.send(interactEntityPacket);
    }

    @Override
    public void sendLeftClickWindowItemPacket(Object client, int windowId, int slot, Object itemStack) {
        TcpClientSession session = (TcpClientSession) client;
        ItemStack item = (ItemStack) itemStack;
        ClientWindowActionPacket windowActionPacket = new ClientWindowActionPacket(windowId, 6, slot, WindowAction.CLICK_ITEM, ClickItemParam.LEFT_CLICK, item, new HashMap<>());
        session.send(windowActionPacket);
    }

    @Override
    public void sendRightClickWindowItemPacket(Object client,int windowId, int slot, Object itemStack) {
        TcpClientSession session = (TcpClientSession) client;
        ItemStack item = (ItemStack) itemStack;
        ClientWindowActionPacket windowActionPacket = new ClientWindowActionPacket(windowId, 6, slot, WindowAction.CLICK_ITEM, ClickItemParam.RIGHT_CLICK, item, new HashMap<>());
        session.send(windowActionPacket);
    }

    @Override
    public void handleOtherPacket(Object packet) {
    }

    @Override
    public void sendChatPacket(Object client, String text) {
        TcpClientSession session = (TcpClientSession) client;
        ClientChatPacket chatPacket = new ClientChatPacket(text);
        session.send(chatPacket);
    }

    @Override
    public void sendTabCompletePacket(Object client, String cmd) {
        TcpClientSession session = (TcpClientSession) client;
        ClientTabCompletePacket clientTabCompletePacket = new ClientTabCompletePacket(1, cmd);
        session.send(clientTabCompletePacket);
    }

    @Override
    public void sendPositionPacketFromPacket(Object client, Object recvPacket, boolean random) {
        TcpClientSession session = (TcpClientSession) client;
        ServerPlayerPositionRotationPacket packet = (ServerPlayerPositionRotationPacket) recvPacket;
        if (packet == null) {
            return;
        }
        double x = packet.getX() + (random ? OtherUtils.getRandomInt(-10, 10) : 0);
        double y = packet.getY() + (random ? OtherUtils.getRandomInt(-10, 10) : 0);
        double z = packet.getZ() + (random ? OtherUtils.getRandomInt(-10, 10) : 0);
        float yaw = packet.getYaw() + (random ? OtherUtils.getRandomFloat(0.00, 1.00) : 0);
        float pitch = packet.getPitch() + (random ? OtherUtils.getRandomFloat(0.00, 1.00) : 0);
        sendPositionRotationPacket(session, x, y, z, yaw, pitch);
    }

    @Override
    public void sendCrashBookPacket(Object client) {
    }

    @Override
    public Object getMessageFromPacket(Object packet) {
        ServerChatPacket chatPacket = (ServerChatPacket) packet;
        return chatPacket.getMessage();
    }

    @Override
    public boolean hasMessageClickEvent(Object message) {
        Component msg = (Component) message;
        return msg.style().clickEvent() != null;
    }

    @Override
    public String getMessageText(Object message) {
        Component msg = (Component) message;
        return convertComponentToPlainText(msg);
    }

    public static String convertComponentToPlainText(Component component) {
        StringBuilder sb = new StringBuilder();
        if (component instanceof TextComponent) {
            sb.append(((TextComponent) component).content());
        } else if (component instanceof TranslatableComponent) {
            TranslatableComponent translatable = (TranslatableComponent) component;
            List<Component> args = translatable.args();
            // 将参数转换为纯文本
            List<String> argsText = args.stream()
                    .map(PacketHandler::convertComponentToPlainText)
                    .collect(Collectors.toList());
            // 附加子参数
            if (argsText.size() >= 2) {
                sb.append("[").append(argsText.get(0)).append("] ").append(argsText.get(1));
                // 如果有更多的参数，你可能需要根据实际情况处理
            }
        }
        // 处理子组件
        List<Component> children = component.children();
        for (Component child : children) {
            sb.append(convertComponentToPlainText(child));
        }
        return sb.toString();
    }

    @Override
    public void handleMessageExtra(cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Packet.PacketHandler packetHandler, Object message, Object client, String username) {
        Component msg = (Component) message;
        for (Component extra : msg.children()) {
            PacketManager.clickVerifiesHandle(packetHandler, client, extra, username);
        }
    }

    @Override
    public String getClickValue(Object message) {
        Component msg = (Component) message;
        return Objects.requireNonNull(msg.style().clickEvent()).value();
    }

    @Override
    public boolean hasMessageExtra(Object message) {
        Component msg = (Component) message;
        return !msg.children().isEmpty();
    }

    private void sendClientPluginMessagePacket(Session session, String channel, byte[] data) {
        ClientPluginMessagePacket clientPluginMessagePacket = new ClientPluginMessagePacket(channel, data);
        session.send(clientPluginMessagePacket);
    }

    private void sendClientSettingPacket(Session session, String locale) {
        ClientSettingsPacket clientSettingsPacket = new ClientSettingsPacket(locale, 8, ChatVisibility.FULL, true, Arrays.asList(SkinPart.values()), HandPreference.LEFT_HAND, true);
        session.send(clientSettingsPacket);
    }

    private void sendClientChangeHeldItemPacket(Session session, int slot) {
        ClientPlayerChangeHeldItemPacket clientPlayerChangeHeldItemPacket = new ClientPlayerChangeHeldItemPacket(slot);
        session.send(clientPlayerChangeHeldItemPacket);
    }

    private void sendPositionRotationPacket(Session session, double x, double y, double z, float yaw, float pitch) {
        ClientPlayerPositionRotationPacket clientPlayerPositionRotationPacket = new ClientPlayerPositionRotationPacket(true, x, y, z, yaw, pitch);
        session.send(clientPlayerPositionRotationPacket);
    }

    private void sendClientPlayerMovementPacket(Session session, boolean onGround) {
        ClientPlayerMovementPacket clientPlayerMovementPacket = new ClientPlayerMovementPacket(onGround);
        session.send(clientPlayerMovementPacket);
    }

    private void sendClientKeepAlivePacket(Session session, long pingId) {
        ClientKeepAlivePacket packet = new ClientKeepAlivePacket(pingId);
        session.send(packet);
    }

    private void sendRespawnPacket(Session session) {
        ClientRequestPacket clientRequestPacket = new ClientRequestPacket(ClientRequest.RESPAWN);
        session.send(clientRequestPacket);
    }
}
