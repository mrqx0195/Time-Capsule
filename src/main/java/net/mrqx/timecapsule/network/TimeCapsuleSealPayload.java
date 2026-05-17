package net.mrqx.timecapsule.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.mrqx.timecapsule.TimeCapsule;
import net.mrqx.timecapsule.item.TimeMode;

public record TimeCapsuleSealPayload(Component message, TimeMode timeMode,
                                     int years, int months, int days, int hours, int minutes, int seconds
) implements CustomPacketPayload {
    public static final Type<TimeCapsuleSealPayload> TYPE = new Type<>(TimeCapsule.prefix("seal_time_capsule"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, TimeCapsuleSealPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            ComponentSerialization.STREAM_CODEC.encode(buf, payload.message);
            TimeMode.STREAM_CODEC.encode(buf, payload.timeMode);
            buf.writeInt(payload.years);
            buf.writeInt(payload.months);
            buf.writeInt(payload.days);
            buf.writeInt(payload.hours);
            buf.writeInt(payload.minutes);
            buf.writeInt(payload.seconds);
        },
        buf -> {
            Component message = ComponentSerialization.STREAM_CODEC.decode(buf);
            TimeMode timeMode = TimeMode.STREAM_CODEC.decode(buf);
            int years = buf.readInt();
            int months = buf.readInt();
            int days = buf.readInt();
            int hours = buf.readInt();
            int minutes = buf.readInt();
            int seconds = buf.readInt();
            return new TimeCapsuleSealPayload(message, timeMode, years, months, days, hours, minutes, seconds);
        }
    );
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
