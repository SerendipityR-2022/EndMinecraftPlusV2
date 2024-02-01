package cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Packet;

import java.util.List;

public interface PacketHandler {
    boolean checkServerPluginMessagePacket(Object packet);

    void handleServerPluginMessagePacket(Object client, Object packet, String username);

    boolean checkServerJoinGamePacket(Object packet);

    void handleServerJoinGamePacket(Object client, Object packet, String username);

    boolean checkServerPlayerPositionRotationPacket(Object packet);

    void handleServerPlayerPositionRotationPacket(Object client, Object packet, String username);

    boolean checkServerChatPacket(Object packet);

    void handleServerChatPacket(Object client, Object packet, String username);

    boolean checkServerKeepAlivePacket(Object packet);

    void handleServerKeepAlivePacket(Object client, Object packet, String username);

    boolean checkServerPlayerHealthPacket(Object packet);

    void handleServerPlayerHealthPacket(Object client, Object packet, String username);

    boolean checkServerSpawnPlayerPacket(Object packet);

    boolean checkSpawnPlayerName(Object packet, String checkName);

    List<Object> getSpawnPlayerMetadata(Object packet);

    Double[] getSpawnPlayerLocation(Object packet);

    int getSpawnPlayerEntityId(Object packet);

    void moveToLocation(Object client, Double[] targetLocation, double moveSpeed);

    boolean checkServerOpenWindowPacket(Object packet);

    int getWindowIDFromServerOpenWindowPacket(Object packet);

    int getWindowSlotsFromPacket(Object packet);

    String getWindowNameFromPacket(Object packet);

    boolean checkServerWindowItemsPacket(Object packet);

    int getWindowIDFromWindowItemsPacket(Object packet);

    Object[] getItemStackFromWindowItemsPacket(Object packet);

    String getItemName(Object itemStack);

    List<String> getItemLore(Object itemStack);

    void sendPlayerInteractEntityPacket(Object client, int entityId, float[] location);

    void sendLeftClickWindowItemPacket(Object client, int windowId, int slot, Object itemStack);

    void sendRightClickWindowItemPacket(Object client, int windowId, int slot, Object itemStack);

    void handleOtherPacket(Object packet);

    void sendChatPacket(Object client, String text);

    void sendTabCompletePacket(Object client, String cmd);

    void sendPositionPacketFromPacket(Object client, Object packet, boolean random);

    void sendCrashBookPacket(Object client);

    Object getMessageFromPacket(Object packet);

    boolean hasMessageClickEvent(Object message);

    String getMessageText(Object message);

    void handleMessageExtra(PacketHandler packetHandler, Object message, Object client, String username);

    String getClickValue(Object message);

    boolean hasMessageExtra(Object message);
}
