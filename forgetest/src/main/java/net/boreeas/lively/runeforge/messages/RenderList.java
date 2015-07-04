package net.boreeas.lively.runeforge.messages;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.boreeas.lively.util.Vec3Int;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Initial list of things that need to be rendered, sent on connect
 * @author Malte Schütze
 */
public class RenderList implements IMessage {
    private Set<Vec3Int> activeLightRuneLocations;

    public RenderList() {
        this.activeLightRuneLocations = new HashSet<>();
    }

    public RenderList(@NotNull Set<Vec3Int> activeLightRuneLocations) {
        this.activeLightRuneLocations = activeLightRuneLocations;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        int numLocations = byteBuf.readInt();
        for (int i = 0; i < numLocations; i++) {
            activeLightRuneLocations.add(new Vec3Int(byteBuf));
        }
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        byteBuf.writeInt(activeLightRuneLocations.size());
        activeLightRuneLocations.forEach(vec -> vec.serialize(byteBuf));
    }

    public @NotNull Set<Vec3Int> getActiveLightRuneLocations() {
        return activeLightRuneLocations;
    }
}
