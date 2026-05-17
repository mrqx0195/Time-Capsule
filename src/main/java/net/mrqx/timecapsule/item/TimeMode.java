package net.mrqx.timecapsule.item;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Locale;

public enum TimeMode {
    GAME_TICK,
    REAL_TIME;
    
    public static final Codec<TimeMode> CODEC = Codec.STRING.xmap(
        s -> TimeMode.valueOf(s.toUpperCase(Locale.ROOT)),
        TimeMode::name
    );
    
    public static final StreamCodec<FriendlyByteBuf, TimeMode> STREAM_CODEC = StreamCodec.of(
        (buf, mode) -> buf.writeByte(mode.ordinal()),
        buf -> TimeMode.values()[buf.readByte()]
    );
}
