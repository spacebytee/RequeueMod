package com.bytespacegames.requeue;

import com.bytespacegames.requeue.auto.IAutoRequeue;
import com.bytespacegames.requeue.auto.WhoRequeue;
import com.bytespacegames.requeue.commands.Requeue;
import com.bytespacegames.requeue.commands.RequeuePartyList;
import com.bytespacegames.requeue.listeners.ChatListener;
import com.bytespacegames.requeue.listeners.TickListener;
import com.bytespacegames.requeue.settings.BooleanSetting;
import com.bytespacegames.requeue.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = RequeueMod.MODID, version = RequeueMod.VERSION)
public class RequeueMod {
    public static final String MODID = "requeuemod";
    public static final String VERSION = "1.0.2";
    public static final String MOD_PREFIX = "§cspace's requeue";
    public static RequeueMod instance;

    private ChatListener chatHandler;
    private TickListener tickListener;
    private IAutoRequeue req = new WhoRequeue();

    private List<Setting> settings = new ArrayList<Setting>();

    public List<Setting> getSettings() {
        return settings;
    }

    public void registerSettings() {
        settings.add(new BooleanSetting("auto", false));
        settings.add(new BooleanSetting("safeguard", true));
        settings.add(new BooleanSetting("kickoffline", true));
        settings.add(new BooleanSetting("clientplayer", true));
        settings.add(new BooleanSetting("requeueonwin", false));
        settings.add(new BooleanSetting("hypixelonly", true));
    }
    public Setting getSettingByName(String name) {
        for (Setting s : settings) {
            if (s.getName().trim().equalsIgnoreCase(name.trim())) {
                return s;
            }
        }
        return null;
    }

    public IAutoRequeue getRequeue() {
        return req;
    }
    public ChatListener getChatHandler() {
        return chatHandler;
    }
    public TickListener getTickListener() {
        return tickListener;
    }
    public void setRequeue(IAutoRequeue rq) {
        this.req=rq;
    }
    public boolean modEnabled() {
        if (getSettingByName("hypixelonly").isEnabled()) {
            String ip = Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase();
            return ip.contains("hypixel.net") || ip.contains("hypixel.io");
        }
        return true;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        instance = this;
        registerSettings();
        ConfigManager.loadSettings();
        MinecraftForge.EVENT_BUS.register(chatHandler = new ChatListener());
        MinecraftForge.EVENT_BUS.register(tickListener = new TickListener());
        ClientCommandHandler.instance.registerCommand(new Requeue());
        ClientCommandHandler.instance.registerCommand(new RequeuePartyList());
        //ClientCommandHandler.instance.registerCommand(new LocrawDebug());
        new LocationManager();
        new PartyManager();
    }
}
