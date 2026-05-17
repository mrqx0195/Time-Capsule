package net.mrqx.timecapsule.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

public class BlockWeatheringTimeCapsule extends BlockTimeCapsule implements WeatheringCopper {
    public static final MapCodec<BlockWeatheringTimeCapsule> CODEC = RecordCodecBuilder.mapCodec(
        i -> i.group(WeatherState.CODEC.fieldOf("weathering_state")
                .forGetter(BlockWeatheringTimeCapsule::getWeatheringState), propertiesCodec())
            .apply(i, BlockWeatheringTimeCapsule::new)
    );
    
    public BlockWeatheringTimeCapsule(WeatherState weatherState, Properties properties) {
        super(weatherState, properties);
    }
    
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
    
    @Override
    public WeatherState getAge() {
        return this.getWeatheringState();
    }
    
    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return WeatheringCopper.getNext(state.getBlock()).isPresent();
    }
    
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.changeOverTime(state, level, pos, random);
    }
}
