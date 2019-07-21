package de.derfrzocker.ore.control.impl.v_14_R1;

import com.mojang.datafixers.Dynamic;
import de.derfrzocker.ore.control.api.*;
import lombok.NonNull;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class WorldGenDecoratorHeightAverageOverrider_v1_14_R1 extends WorldGenDecoratorHeightAverage {

    @NonNull
    private final Biome biome;

    public WorldGenDecoratorHeightAverageOverrider_v1_14_R1(final Function<Dynamic<?>, ? extends WorldGenDecoratorHeightAverageConfiguration> dynamicFunction, final Biome biome) {
        super(dynamicFunction);
        this.biome = biome;
    }

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettingsDefault> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenDecoratorHeightAverageConfiguration worldGenDecoratorHeightAverageConfiguration, final WorldGenFeatureConfigured<C> worldGenFeatureConfigured) {
        final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);
        final Optional<WorldOreConfig> oreConfig = service.getWorldOreConfig(generatorAccess.getMinecraftWorld().getWorld());

        if (oreConfig.isPresent() && !service.isActivated(Ore.LAPIS, oreConfig.get(), biome))
            return true;

        return oreConfig.map(worldOreConfig -> super.a(generatorAccess, chunkGenerator, random, blockPosition, new WorldGenDecoratorHeightAverageConfiguration(
                service.getValue(Ore.LAPIS, Setting.VEINS_PER_CHUNK, worldOreConfig, biome),
                service.getValue(Ore.LAPIS, Setting.HEIGHT_CENTER, worldOreConfig, biome),
                service.getValue(Ore.LAPIS, Setting.HEIGHT_RANGE, worldOreConfig, biome)), new WorldGenFeatureConfigured<>(worldGenFeatureConfigured.a, NMSUtil_v1_14_R1.getFeatureConfiguration(oreConfig.get(), Ore.LAPIS, worldGenFeatureConfigured.b, biome)))).
                orElseGet(() -> super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenDecoratorHeightAverageConfiguration, worldGenFeatureConfigured));
    }

}