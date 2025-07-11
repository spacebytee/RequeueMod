package com.bytespacegames.requeue;

import com.bytespacegames.requeue.auto.IAutoRequeue;
import com.bytespacegames.requeue.auto.WhoRequeue;
import com.bytespacegames.requeue.commands.Requeue;
import com.bytespacegames.requeue.commands.RequeuePartyList;
import com.bytespacegames.requeue.commands.Rq;
import com.bytespacegames.requeue.listeners.ChatListener;
import com.bytespacegames.requeue.listeners.TickListener;
import com.bytespacegames.requeue.listeners.WorldListener;
import com.bytespacegames.requeue.settings.BooleanSetting;
import com.bytespacegames.requeue.settings.Setting;
import com.bytespacegames.requeue.util.Timer;
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
    public static final String VERSION = "1.0.9";
    public static final String MOD_PREFIX = "space's requeue";
    public static final String PRIMARY_COLOR = "§c";
    public static final String TEXT_COLOR = "§e";
    private final Timer requeueTimer = new Timer();
    public static RequeueMod instance;

    private ChatListener chatHandler;
    private TickListener tickListener;
    private IAutoRequeue req = new WhoRequeue();

    private final List<Setting> settings = new ArrayList<>();
    private final String[] excludedGames = {"BEDWARS", "PAINTBALL", "QUAKECRAFT", "ARENA", "GINGERBREAD", "WALLS3", "PIT", "SKYBLOCK", "REPLAY", "HOUSING"};

    public List<Setting> getSettings() {
        return settings;
    }

    public void registerSettings() {
        settings.add(new BooleanSetting("auto", "Automatically requeues when your party is dead.", false));
        settings.add(new BooleanSetting("safeguard", "Prevents you from using /requeue when your party is still alive.", true));
        settings.add(new BooleanSetting("kickoffline", "Automatically kicks offline players 5 seconds after they disconnect.", true));
        settings.add(new BooleanSetting("clientplayer", "Considers the client player (you) when requeueing. If off, it will requeue when your party is dead, even if you're alive.", true));
        settings.add(new BooleanSetting("requeueonwin", "Automatically requeues when the game is over.", false));
        settings.add(new BooleanSetting("hypixelonly", "Only runs any of the mods functions when connected to a hypixel server.",true));
        settings.add(new BooleanSetting("useforge", "Prefer forge event handlers. This can fix issues if another mod is conflicting with the mod.",false));
    }
    public Setting getSettingByName(String name) {
        for (Setting s : settings) {
            if (s.getName().trim().equalsIgnoreCase(name.trim())) {
                return s;
            }
        }
        return null;
    }
    public String[] settingsToArray() {
        String[] settingNames = new String[settings.size()];
        for (int i = 0; i < settings.size(); i++) {
            settingNames[i] = settings.get(i).getName();
        }
        return settingNames;
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
        if (Minecraft.getMinecraft().getCurrentServerData() == null || Minecraft.getMinecraft().getCurrentServerData().serverIP == null) return false;
        if (getSettingByName("hypixelonly").isEnabled()) {
            String ip = Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase();
            return ip.contains("hypixel.net") || ip.contains("hypixel.io");
        }
        return true;
    }

    public boolean isUsingWhoRequeue() {
        if (req == null) return false;
        if (LocationManager.instance == null) return false;
        return req instanceof WhoRequeue;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        instance = this;
        registerSettings();
        ConfigManager.loadSettings();
        MinecraftForge.EVENT_BUS.register(chatHandler = new ChatListener());
        MinecraftForge.EVENT_BUS.register(tickListener = new TickListener());
        MinecraftForge.EVENT_BUS.register(new WorldListener());
        // remove existing requeue commands (potentially from other mods) to prevent conflicts.
        ClientCommandHandler.instance.getCommands().remove("requeue");
        ClientCommandHandler.instance.getCommands().remove("rq");

        ClientCommandHandler.instance.registerCommand(new Requeue());
        ClientCommandHandler.instance.registerCommand(new Rq());
        ClientCommandHandler.instance.registerCommand(new RequeuePartyList());
        //ClientCommandHandler.instance.registerCommand(new LocrawDebug());
        new LocationManager();
        new PartyManager();
    }
    public Timer getRequeueTimer() {
        return requeueTimer;
    }

    public String[] getExcludedGames() {
        return excludedGames;
    }
}