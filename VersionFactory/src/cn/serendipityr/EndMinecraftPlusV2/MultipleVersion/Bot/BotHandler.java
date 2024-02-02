package cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Bot;

import java.net.Proxy;

public interface BotHandler {
    Object createClient(Proxy proxy, String userName);

    Object createClientLocal(String userName);

    void connect(Object client, Proxy proxy);

    void disconnect(Object client);

    void connectLocal(Object client);

    boolean checkClientStatus(Object client);

    String getClientDisconnectMsg(Object client);

    boolean hasClientFlag(Object client, String flag);

    void setClientFlag(Object client, String flag, Object value);

    Object getClientFlag(Object client, String flag);

    void setClientTimeout(Object client, int timeout);
}
