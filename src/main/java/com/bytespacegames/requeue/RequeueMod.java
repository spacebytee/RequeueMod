package com.bytespacegames.requeue;

import com.bytespacegames.requeue.auto.IAutoRequeue;
import com.bytespacegames.requeue.auto.WhoRequeue;
import com.bytespacegames.requeue.commands.Requeue;
import com.bytespacegames.requeue.listeners.ChatListener;
import com.bytespacegames.requeue.listeners.TickListener;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
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
    private Minecraft mc;
    private boolean isAuto = true;
    private IAutoRequeue req = new WhoRequeue();
    public boolean isAuto() {
        return isAuto;
    }
    public IAutoRequeue getRequeue() {
        return req;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        instance = this;
        mc = Minecraft.getMinecraft();
        MinecraftForge.EVENT_BUS.register(new ChatListener());
        MinecraftForge.EVENT_BUS.register(new TickListener());
        ClientCommandHandler.instance.registerCommand(new Requeue());
        new LocationManager();
    }

    public ChatListener getChatHandler() {
        return chatHandler;
    }
}
