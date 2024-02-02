package cn.serendipityr.EndMinecraftPlusV2.Tools;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface SetTitle extends Library {
    SetTitle INSTANCE = Native.loadLibrary((Platform.isWindows() ? "kernel32" : "c"), SetTitle.class);

    boolean SetConsoleTitleA(String title);
}