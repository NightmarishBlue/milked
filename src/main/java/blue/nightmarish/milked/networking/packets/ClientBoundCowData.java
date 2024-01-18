package blue.nightmarish.milked.networking.packets;

import blue.nightmarish.milked.networking.client.MilkPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientBoundCowData {
    private final boolean milk;
    private final int cowID;

    public ClientBoundCowData(boolean milk, int cowID) {
        this.milk = milk;
        this.cowID = cowID;
    }

    public ClientBoundCowData(FriendlyByteBuf buf) {
        this.milk = buf.readBoolean();
        this.cowID = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.milk);
        buf.writeInt(this.cowID);
    }

    public static boolean handle(ClientBoundCowData msg, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // inside the client here
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MilkPacketHandler.unwrapCowDataPacket(msg, context));
        });
        context.setPacketHandled(true);
        return true;
    }

    public boolean getMilk() {
        return this.milk;
    }

    public int getID() {
        return this.cowID;
    }
}
