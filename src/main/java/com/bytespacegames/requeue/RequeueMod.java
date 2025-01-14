package com.bytespacegames.requeue;

import com.bytespacegames.requeue.auto.IAutoRequeue;
import com.bytespacegames.requeue.auto.WhoRequeue;
import com.bytespacegames.requeue.commands.Requeue;
import com.bytespacegames.requeue.commands.RequeuePartyList;
import com.bytespacegames.requeue.listeners.ChatListener;
import com.bytespacegames.requeue.listeners.TickListener;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = RequeueMod.MODID, version = RequeueMod.VERSION)
public class RequeueMod {
    public static final String MODID = "requeuemod";
    public static final String VERSION = "1.0";
    public static final String MOD_PREFIX = "§cspace's requeue";
    public static RequeueMod instance;

    private ChatListener chatHandler;
    private TickListener tickListener;

    private boolean isAuto = false;
    private boolean careAboutClient = true;
    private boolean safeGuardManual = true;
    private boolean kickOffline = true;
    private boolean requeueOnWin = false;
    private IAutoRequeue req = new WhoRequeue();
    public boolean isAuto() {
        return isAuto;
    }
    public boolean caresAboutClient() {
        return careAboutClient;
    }
    public boolean safeguardManualRequeues() {
        return safeGuardManual;
    }
    public boolean kickOffline() {
        return kickOffline;
    }
    public boolean requeueOnWin() { return requeueOnWin; }
    public boolean toggleAuto() { isAuto = !isAuto; return isAuto; }
    public boolean toggleClientPlayer() { careAboutClient = !careAboutClient; return careAboutClient; }
    public boolean toggleSafeGuardManual() { safeGuardManual = !safeGuardManual; return safeGuardManual; }
    public boolean toggleKickOffline() { kickOffline = !kickOffline; return kickOffline; }
    public boolean toggleRequeueOnWin() { requeueOnWin = !requeueOnWin; return requeueOnWin; }
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

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        instance = this;
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
