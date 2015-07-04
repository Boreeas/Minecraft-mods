package net.boreeas.lively.runeforge.render;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.boreeas.lively.Lively;
import net.boreeas.lively.runeforge.messages.AddActiveLightRune;
import net.boreeas.lively.runeforge.messages.RemoveActiveLightRune;
import net.boreeas.lively.runeforge.messages.RenderList;
import net.boreeas.lively.util.Vec3Int;
import net.minecraft.entity.player.EntityPlayerMP;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Malte Schütze
 */
public class ServerRenderHandler {
    private Set<Vec3Int> activeLightRuneLocations = new HashSet<>();

    public void addLightRune(@NotNull Vec3Int loc) {
        activeLightRuneLocations.add(loc);
        Lively.INSTANCE.network.sendToAll(new AddActiveLightRune(loc));
    }

    public void removeLightRune(@NotNull Vec3Int loc) {
        activeLightRuneLocations.remove(loc);
        Lively.INSTANCE.network.sendToAll(new RemoveActiveLightRune(loc));
    }

    @SubscribeEvent
    public void onClientConnected(PlayerEvent.PlayerLoggedInEvent evt) {
        if (evt.player instanceof EntityPlayerMP) {
            Lively.INSTANCE.network.sendTo(new RenderList(activeLightRuneLocations), (EntityPlayerMP) evt.player);
        } else {
            Lively.INSTANCE.logger.warn("Unable to send initial render list to client: EntityPlayer isn't EntityPlayerMP");
        }
    }
}
