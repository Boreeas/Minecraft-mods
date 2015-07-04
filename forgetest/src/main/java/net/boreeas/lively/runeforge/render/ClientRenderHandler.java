package net.boreeas.lively.runeforge.render;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.boreeas.lively.Lively;
import net.boreeas.lively.runeforge.messages.RemoveActiveLightRune;
import net.boreeas.lively.runeforge.messages.AddActiveLightRune;
import net.boreeas.lively.runeforge.messages.RenderList;
import net.boreeas.lively.util.Vec3Int;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class ClientRenderHandler {

    private static final Sphere sphere = new Sphere();

    private Set<Vec3Int> lightRuneFXs = new HashSet<>();

    public void addLightRuneFx(@NotNull Vec3Int pos) {
        lightRuneFXs.add(pos);
    }

    public void removeLightRuneFx(@NotNull Vec3Int pos) {
        lightRuneFXs.remove(pos);
    }

    @SubscribeEvent
    public void renderWorldLast(RenderWorldLastEvent evt) {
        lightRuneFXs.forEach(this::renderLightRuneFx);
    }

    private void renderLightRuneFx(Vec3Int coord) {
        GL11.glPushMatrix();
        GL11.glTranslated(coord.x + 0.5, coord.y + 0.5, coord.z + 0.5);
        GL11.glColor3b(((byte) 100), ((byte) 100), ((byte) 0));

        System.out.println("render at " + coord);
        sphere.draw(3, 12, 6);

        GL11.glPopMatrix();
    }




    public static class RenderListMessageHandler implements IMessageHandler<RenderList, IMessage> {
        @Override
        public IMessage onMessage(RenderList renderList, MessageContext messageContext) {
            System.out.println("Received initial locations: " + renderList.getActiveLightRuneLocations());
            Lively.INSTANCE.clientRenderHandler.lightRuneFXs = renderList.getActiveLightRuneLocations();

            return null; // no response
        }
    }

    public static class AddLightRuneHandler implements IMessageHandler<AddActiveLightRune, IMessage> {

        @Override
        public IMessage onMessage(AddActiveLightRune addActiveLightRune, MessageContext messageContext) {
            System.out.println("New rune at " + addActiveLightRune.getLoc());
            Lively.INSTANCE.clientRenderHandler.lightRuneFXs.add(addActiveLightRune.getLoc());

            return null;
        }
    }

    public static class RemoveLightRuneHandler implements IMessageHandler<RemoveActiveLightRune, IMessage> {

        @Override
        public IMessage onMessage(RemoveActiveLightRune removeActiveLightRune, MessageContext messageContext) {
            System.out.println("Remove rune at " + removeActiveLightRune.getLoc());
            Lively.INSTANCE.clientRenderHandler.lightRuneFXs.remove(removeActiveLightRune.getLoc());

            return null;
        }
    }
}
