package net.boreeas.daycount;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Malte Schütze
 */
public class Daycount extends JavaPlugin {

    ScheduledExecutorService thread;

    @Override
    public void onEnable() {
        thread = Executors.newScheduledThreadPool(1);
        thread.scheduleAtFixedRate(new Runnable() {
            int lastDay = 0;

            @Override
            public void run() {
                int day = (int) (((getServer().getWorlds().get(0).getFullTime() + 1000) // Adjust to hit actual sunrise
                        / 1000) / 24);

                if (day != lastDay) {
                    getServer().dispatchCommand(getServer().getConsoleSender(),
                            "title @a title {text:\"Dawn\",bold:true}"
                    );
                    getServer().dispatchCommand(getServer().getConsoleSender(),
                            "title @a subtitle {text:\"of the " + dayify(day + 1) + " day\",italic:true,color:gray}");
                    lastDay = day;
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
        getLogger().info("[Daycount] Enabled");
    }

    // 1 => 1st
    // 2 => 2nd
    // 3 => 3rd
    // 4 => 4th
    private String dayify(int day) {
        if (day % 10 == 1) {
            return day + "st";
        } else if (day % 10 == 2) {
            return day + "snd";
        } else if (day % 10 == 3) {
            return day + "rd";
        }

        return day + "th";
    }

    @Override
    public void onDisable() {
        thread.shutdownNow();
        getLogger().info("[Daycount] Disabled");
    }
}
