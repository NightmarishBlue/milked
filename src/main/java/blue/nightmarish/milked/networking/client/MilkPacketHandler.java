package blue.nightmarish.milked.networking.client;

import blue.nightmarish.milked.MilkableEntity;
import blue.nightmarish.milked.networking.packets.ClientBoundCowData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraftforge.network.NetworkEvent;

public class MilkPacketHandler {
    public static void unwrapCowDataPacket(ClientBoundCowData cowData, NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            int ID = cowData.getID();
            boolean state = cowData.getMilk();
            Entity entity = level.getEntity(ID);
            if (!(entity instanceof Cow)) return;
            updateCow((MilkableEntity) entity, state);
        });
    }

    public static void updateCow(MilkableEntity cow, boolean milkState) {
        cow.milked$setMilk(milkState);
    }
}
