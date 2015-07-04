package net.boreeas.lively.runeforge.messages;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.boreeas.lively.util.Vec3Int;
import org.jetbrains.annotations.NotNull;

/**
 * @author Malte Schütze
 */
public class RemoveActiveLightRune implements IMessage {
    private Vec3Int loc;

    public RemoveActiveLightRune() {
    }

    public RemoveActiveLightRune(@NotNull Vec3Int loc) {
        this.loc = loc;
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        loc = new Vec3Int(byteBuf);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        if (loc == null) throw new IllegalStateException("Uninitialized location");
        loc.serialize(byteBuf);
    }

    public @NotNull Vec3Int getLoc() {
        return loc;
    }
}
