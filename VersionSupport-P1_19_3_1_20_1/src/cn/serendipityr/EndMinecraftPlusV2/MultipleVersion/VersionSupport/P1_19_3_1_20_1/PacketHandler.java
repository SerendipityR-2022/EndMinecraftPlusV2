package cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_19_3_1_20_1;

import cn.serendipityr.EndMinecraftPlusV2.AdvanceModule.ACProtocol.AnotherStarAntiCheat;
import cn.serendipityr.EndMinecraftPlusV2.AdvanceModule.ACProtocol.AntiCheat3;
import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Bot.BotManager;
import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Packet.PacketManager;
import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.UniverseMethods;
import cn.serendipityr.EndMinecraftPlusV2.Tools.ConfigUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.OtherUtils;
import com.github.steveice10.mc.protocol.data.game.ClientCommand;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.player.HandPreference;
import com.github.steveice10.mc.protocol.data.game.entity.player.InteractAction;
import com.github.steveice10.mc.protocol.data.game.inventory.ClickItemAction;
import com.github.steveice10.mc.protocol.data.game.inventory.ContainerActionType;
import com.github.steveice10.mc.protocol.data.game.setting.ChatVisibility;
import com.github.steveice10.mc.protocol.data.game.setting.SkinPart;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundCustomPayloadPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundSetHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.spawn.ClientboundAddPlayerPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.inventory.ClientboundContainerSetContentPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.inventory.ClientboundOpenScreenPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.*;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.inventory.ServerboundContainerClickPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.level.ServerboundAcceptTeleportationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.*;
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
    @Override
    public boolean checkServerPluginMessagePacket(Object packet) {
        return packet instanceof ClientboundCustomPayloadPacket;
    }

    @Override
    public void handleServerPluginMessagePacket(Object client, Object recvPacket, String username) {
        ClientboundCustomPayloadPacket packet = (ClientboundCustomPayloadPacket) recvPacket;
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
        return packet instanceof ClientboundLoginPacket;
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
        return packet instanceof ClientboundPlayerPositionPacket;
    }

    @Override
    public void handleServerPlayerPositionRotationPacket(Object client, Object recvPacket, String username) {
        TcpClientSession session = (TcpClientSession) client;
        ClientboundPlayerPositionPacket positionRotationPacket = (ClientboundPlayerPositionPacket) recvPacket;
        session.setFlag("location", new double[]{positionRotationPacket.getX(), positionRotationPacket.getY(), positionRotationPacket.getZ(), positionRotationPacket.getYaw(), positionRotationPacket.getPitch()});
        if (ConfigUtil.PacketHandlerMove) {
            sendClientPlayerMovementPacket(session, true);
            ServerboundAcceptTeleportationPacket teleportConfirmPacket = new ServerboundAcceptTeleportationPacket(positionRotationPacket.getTeleportId());
            session.send(teleportConfirmPacket);
            sendClientPlayerMovementPacket(session, true);
        }
    }

    @Override
    public boolean checkServerChatPacket(Object packet) {
        return packet instanceof ClientboundSystemChatPacket;
    }

    @Override
    public void handleServerChatPacket(Object client, Object recvPacket, String username) {
        ClientboundSystemChatPacket chatPacket = (ClientboundSystemChatPacket) recvPacket;
        Component message = chatPacket.getContent();
        if (ConfigUtil.ShowServerMessages && !getMessageText(message).equals("")) {
            LogUtil.doLog(0, "[服务端返回信息] [" + username + "] " + getMessageText(message), "BotAttack");
        }
    }

    @Override
    public boolean checkServerKeepAlivePacket(Object packet) {
        return packet instanceof ClientboundKeepAlivePacket;
    }

    @Override
    public void handleServerKeepAlivePacket(Object client, Object recvPacket, String username) {
        TcpClientSession session = (TcpClientSession) client;
        ClientboundKeepAlivePacket serverKeepAlivePacket = (ClientboundKeepAlivePacket) recvPacket;
        sendClientKeepAlivePacket(session, serverKeepAlivePacket.getPingId());
    }

    @Override
    public boolean checkServerPlayerHealthPacket(Object packet) {
        return packet instanceof ClientboundSetHealthPacket;
    }

    @Override
    public void handleServerPlayerHealthPacket(Object client, Object recvPacket, String username) {
        TcpClientSession session = (TcpClientSession) client;
        ClientboundSetHealthPacket serverPlayerHealthPacket = (ClientboundSetHealthPacket) recvPacket;
        double health = serverPlayerHealthPacket.getHealth();
        if (health <= 0) {
            sendRespawnPacket(session);
            LogUtil.doLog(0, "[" + username + "] " + "假人于服务器中死亡，已重生。", "BotAttack");
        }
    }

    @Override
    public boolean checkServerSpawnPlayerPacket(Object packet) {
        return packet instanceof ClientboundAddPlayerPacket;
    }

    @Override
    public boolean checkSpawnPlayerName(Object packet, String checkName) {
        return false;
    }

    @Override
    public List<Object> getSpawnPlayerMetadata(Object packet) {
        return null;
    }

    @Override
    public Double[] getSpawnPlayerLocation(Object packet) {
        ClientboundAddPlayerPacket playerPacket = (ClientboundAddPlayerPacket) packet;
        return new Double[]{playerPacket.getX(), playerPacket.getY(), playerPacket.getZ()};
    }

    @Override
    public int getSpawnPlayerEntityId(Object packet) {
        ClientboundAddPlayerPacket playerPacket = (ClientboundAddPlayerPacket) packet;
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
            double[] location = session.getFlag("location");
            double selfX = location[0];
            double selfY = location[1];
            double selfZ = location[2];

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

            session.setFlag("location", new double[]{selfX, selfY, selfZ, location[3], location[4]});
            ServerboundMovePlayerPosPacket playerPositionPacket = new ServerboundMovePlayerPosPacket(true, selfX, selfY, selfZ);
            session.send(playerPositionPacket);

            OtherUtils.doSleep(50); // 暂停以等待服务器响应

            // 检查是否成功移动
            movedLastTime = (previousX != selfX || previousY != selfY || previousZ != selfZ);
        }
    }

    @Override
    public boolean checkServerOpenWindowPacket(Object packet) {
        return packet instanceof ClientboundOpenScreenPacket;
    }

    @Override
    public int getWindowIDFromServerOpenWindowPacket(Object packet) {
        ClientboundOpenScreenPacket windowPacket = (ClientboundOpenScreenPacket) packet;
        return windowPacket.getContainerId();
    }

    @Override
    public int getWindowSlotsFromPacket(Object packet) {
        return -1;
    }

    @Override
    public String getWindowNameFromPacket(Object packet) {
        ClientboundOpenScreenPacket windowPacket = (ClientboundOpenScreenPacket) packet;
        return windowPacket.getName();
    }

    @Override
    public boolean checkServerWindowItemsPacket(Object packet) {
        return packet instanceof ClientboundContainerSetContentPacket;
    }

    @Override
    public int getWindowIDFromWindowItemsPacket(Object packet) {
        ClientboundContainerSetContentPacket windowItemsPacket = (ClientboundContainerSetContentPacket) packet;
        return windowItemsPacket.getContainerId();
    }

    @Override
    public Object[] getItemStackFromWindowItemsPacket(Object packet) {
        ClientboundContainerSetContentPacket windowItemsPacket = (ClientboundContainerSetContentPacket) packet;
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
    public double[] getLocationFromPacket(Object packet) {
        ClientboundPlayerPositionPacket positionRotationPacket = (ClientboundPlayerPositionPacket) packet;
        return new double[]{positionRotationPacket.getX(), positionRotationPacket.getY(), positionRotationPacket.getZ(), positionRotationPacket.getYaw(), positionRotationPacket.getPitch()};
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
        for (Tag tag : itemLore) {
            loreList.add((String) tag.getValue());
        }
        return loreList;
    }

    @Override
    public void sendPlayerInteractEntityPacket(Object client, int entityId, float[] location) {
        TcpClientSession session = (TcpClientSession) client;
        ServerboundInteractPacket interactEntityPacket = new ServerboundInteractPacket(entityId, InteractAction.INTERACT, false);
        session.send(interactEntityPacket);
    }

    @Override
    public void sendPlayerPositionPacket(Object client, boolean onGround, double[] location) {
        TcpClientSession session = (TcpClientSession) client;
        ServerboundMovePlayerPosPacket positionPacket = new ServerboundMovePlayerPosPacket(onGround, location[0], location[1], location[2]);
        session.send(positionPacket);
    }

    @Override
    public void sendLeftClickWindowItemPacket(Object client, int windowId, int slot, Object itemStack) {
        TcpClientSession session = (TcpClientSession) client;
        ItemStack item = (ItemStack) itemStack;
        ServerboundContainerClickPacket windowActionPacket = new ServerboundContainerClickPacket(windowId, 6, slot, ContainerActionType.CLICK_ITEM, ClickItemAction.LEFT_CLICK, item, new HashMap<>());
        session.send(windowActionPacket);
    }

    @Override
    public void sendRightClickWindowItemPacket(Object client, int windowId, int slot, Object itemStack) {
        TcpClientSession session = (TcpClientSession) client;
        ItemStack item = (ItemStack) itemStack;
        ServerboundContainerClickPacket windowActionPacket = new ServerboundContainerClickPacket(windowId, 6, slot, ContainerActionType.CLICK_ITEM, ClickItemAction.RIGHT_CLICK, item, new HashMap<>());
        session.send(windowActionPacket);
    }

    @Override
    public void handleOtherPacket(Object packet) {
    }

    @Override
    public void sendChatPacket(Object client, String text) {
        TcpClientSession session = (TcpClientSession) client;
        if (text.startsWith("/")) {
            ServerboundChatCommandPacket commandPacket = new ServerboundChatCommandPacket(text.split("/")[1], System.currentTimeMillis(), 0L, new ArrayList<>(), 0, new BitSet());
            session.send(commandPacket);
        } else {
            ServerboundChatPacket chatPacket = new ServerboundChatPacket(text, System.currentTimeMillis(), 0L, null, 0, new BitSet());
            session.send(chatPacket);
        }
    }

    @Override
    public void sendTabCompletePacket(Object client, String cmd) {
        TcpClientSession session = (TcpClientSession) client;
        ServerboundCommandSuggestionPacket clientTabCompletePacket = new ServerboundCommandSuggestionPacket(1, cmd);
        session.send(clientTabCompletePacket);
    }

    @Override
    public void sendPositionPacketFromPacket(Object client, Object recvPacket, boolean random) {
        TcpClientSession session = (TcpClientSession) client;
        ClientboundPlayerPositionPacket packet = (ClientboundPlayerPositionPacket) recvPacket;
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
        ClientboundSystemChatPacket chatPacket = (ClientboundSystemChatPacket) packet;
        return chatPacket.getContent();
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
        ServerboundCustomPayloadPacket clientPluginMessagePacket = new ServerboundCustomPayloadPacket(channel, data);
        session.send(clientPluginMessagePacket);
    }

    private void sendClientSettingPacket(Session session, String locale) {
        ServerboundClientInformationPacket clientSettingsPacket = new ServerboundClientInformationPacket(locale, 8, ChatVisibility.FULL, true, Arrays.asList(SkinPart.values()), HandPreference.LEFT_HAND, true, true);
        session.send(clientSettingsPacket);
    }

    private void sendClientChangeHeldItemPacket(Session session, int slot) {
        ServerboundSetCarriedItemPacket clientPlayerChangeHeldItemPacket = new ServerboundSetCarriedItemPacket(slot);
        session.send(clientPlayerChangeHeldItemPacket);
    }

    private void sendPositionRotationPacket(Session session, double x, double y, double z, float yaw, float pitch) {
        ServerboundMovePlayerPosRotPacket clientPlayerPositionRotationPacket = new ServerboundMovePlayerPosRotPacket(true, x, y, z, yaw, pitch);
        session.send(clientPlayerPositionRotationPacket);
    }

    private void sendClientPlayerMovementPacket(Session session, boolean onGround) {
        ServerboundMovePlayerStatusOnlyPacket clientPlayerMovementPacket = new ServerboundMovePlayerStatusOnlyPacket(onGround);
        session.send(clientPlayerMovementPacket);
    }

    private void sendClientKeepAlivePacket(Session session, long pingId) {
        ServerboundKeepAlivePacket packet = new ServerboundKeepAlivePacket(pingId);
        session.send(packet);
    }

    private void sendRespawnPacket(Session session) {
        ServerboundClientCommandPacket clientRequestPacket = new ServerboundClientCommandPacket(ClientCommand.RESPAWN);
        session.send(clientRequestPacket);
    }
}
