package cn.serendipityr.EndMinecraftPlusV2.MultipleVersion;

import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Bot.BotManager;
import cn.serendipityr.EndMinecraftPlusV2.Tools.ConfigUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.DataUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.OtherUtils;
import cn.serendipityr.EndMinecraftPlusV2.Tools.ProxyUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.Random;

public class UniverseMethods {
    public static String getRandMessage(String userName) {
        Random random = new Random();
        int randomIndex = random.nextInt(ConfigUtil.CustomChat.size());

        String randomMessage = ConfigUtil.CustomChat.get(randomIndex);
        String randomString = OtherUtils.getRandomString(4, 6);
        String userPassword = DataUtil.botRegPasswordsMap.get(userName);

        randomMessage = randomMessage.replace("$rnd", randomString);
        randomMessage = randomMessage.replace("$pwd", userPassword);
        randomMessage = randomMessage.replace("$userName", userName);

        return randomMessage;
    }

    public void getMotd(Proxy proxy, String ip, int port) throws IOException {
        try (Socket socket = new Socket(proxy)) {
            socket.connect(new InetSocketAddress(ip, port));

            if (socket.isConnected()) {
                try (OutputStream out = socket.getOutputStream()) {
                    out.write(new byte[]{0x07, 0x00, 0x05, 0x01, 0x30, 0x63, (byte) 0xDD, 0x01});
                    out.write(new byte[]{0x01, 0x00});
                    out.flush();
                }
            }
        }
    }

    public static Proxy getProxy(Proxy.Type proxyType) {
        int size = ProxyUtil.proxies.size();
        String p = ProxyUtil.proxies.get(BotManager.clientList.size() % size);
        String[] _p = p.split(":");
        return new Proxy(proxyType, new InetSocketAddress(_p[0], Integer.parseInt(_p[1])));
    }

    public static Proxy.Type getProxyType(int type) {
        Proxy.Type proxyType;
        switch (type) {
            case 3:
            case 2:
                proxyType = Proxy.Type.SOCKS;
                break;
            case 1:
            default:
                proxyType = Proxy.Type.HTTP;
                break;
        }

        return proxyType;
    }

    public static String getRandomUser() {
        if (BotManager.doubleAttack) {
            return ConfigUtil.DoubleExploitPlayer + "@12345678Aa!";
        }

        return DataUtil.botRegPasswords.get(new Random().nextInt(DataUtil.botRegPasswords.size()));
    }

    public static byte[] getRandomMAC() {
        Random random = new Random();
        byte[] macAddress = new byte[6];
        random.nextBytes(macAddress);
        macAddress[0] = (byte) (macAddress[0] | (byte) (1 << 1));
        macAddress[0] = (byte) (macAddress[0] & (byte) ~1);
        return macAddress;
    }
}
