package blue.nightmarish.milked.networking;

import blue.nightmarish.milked.MilkedMod;
import blue.nightmarish.milked.networking.packets.ClientBoundCowData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class MilkMessages {
    private static SimpleChannel INSTANCE;

    private static int packetID = 0;
    private static int ID() {
        return packetID++;
    }

    public static void register() {
        SimpleChannel network = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(MilkedMod.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();
        INSTANCE = network;

        network.messageBuilder(ClientBoundCowData.class, ID(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientBoundCowData::new)
                .encoder(ClientBoundCowData::toBytes)
                .consumerMainThread(ClientBoundCowData::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void updateEntity(Entity entity, boolean state) {
        ClientBoundCowData data = new ClientBoundCowData(state, entity.getId());
        INSTANCE.send(PacketDistributor.ALL.with(() -> null), data);
//        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), data);
    }
}
