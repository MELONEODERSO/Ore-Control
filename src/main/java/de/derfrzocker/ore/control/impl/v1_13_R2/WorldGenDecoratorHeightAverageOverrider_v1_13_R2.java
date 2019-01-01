package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import net.minecraft.server.v1_13_R2.*;

import java.util.Random;

public class WorldGenDecoratorHeightAverageOverrider_v1_13_R2 extends WorldGenDecoratorHeightAverage {

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatorAccess, ChunkGenerator<? extends GeneratorSettings> chunkGenerator, Random random, BlockPosition blockPosition, WorldGenDecoratorHeightAverageConfiguration worldGenDecoratorHeightAverageConfiguration, WorldGenerator<C> worldGenerator, C c) {
        return OreControl.getService().
                getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld()).
                map(value -> value.getOreSettings(Ore.LAPIS)).
                map(oreSettings -> super.a(generatorAccess, chunkGenerator, random, blockPosition, new WorldGenDecoratorHeightAverageConfiguration(oreSettings.getValue(Setting.VEINS_PER_CHUNK).orElse(0), oreSettings.getValue(Setting.HEIGHT_CENTER).orElse(0), oreSettings.getValue(Setting.HEIGHT_RANGE).orElse(0)), worldGenerator, c)).
                orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenDecoratorHeightAverageConfiguration, worldGenerator, c));
    }
}
