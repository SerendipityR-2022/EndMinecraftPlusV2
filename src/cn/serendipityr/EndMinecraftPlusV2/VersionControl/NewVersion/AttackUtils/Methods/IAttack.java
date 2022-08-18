package cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.Methods;

public abstract class IAttack {
    public String ip;
    public int port;

    public int attack_time;
    public int attack_maxconnect;
    public long attack_joinsleep;

    public IAttack(String ip, int port, int time, int maxconnect, long joinsleep) {
        this.ip = ip;
        this.port = port;
        this.attack_time = time;
        this.attack_maxconnect = maxconnect;
        this.attack_joinsleep = joinsleep;
    }

    public abstract void start();

    public abstract void stop();
}
