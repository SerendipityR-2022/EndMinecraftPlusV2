package cn.serendipityr.EndMinecraftPlusV2.Tools;

public class LogUtil {
    public static void doLog(int type, String content, String extra) {
        String logType;
        String msg;

        switch (type) {
            case 1:
                logType = "[Internal Error]";
                break;
            case 2:
                logType = "[Attack Error]";
                break;
            case 3:
                logType = "[DEBUG]";
                break;
            default:
                logType = "[" + extra + "]";
        }

        msg = logType + " " + content;

        if (type == -1) {
            System.out.print(msg);
        } else {
            System.out.println(msg);
        }
    }

    public static void emptyLog() {
        System.out.println("");
    }
}
