package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.NMSService;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R1.*;

import java.util.Random;
import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor
public class WorldGenDecoratorNetherHeightNormalOverrider_v1_13_R1 extends WorldGenDecoratorNetherHeight {

    @NonNull
    private final Biome biome;

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    @Override
    public <C extends WorldGenFeatureConfiguration> boolean a(final GeneratorAccess generatorAccess, final ChunkGenerator<? extends GeneratorSettings> chunkGenerator, final Random random, final BlockPosition blockPosition, final WorldGenFeatureChanceDecoratorCountConfiguration worldGenFeatureChanceDecoratorCountConfiguration, final WorldGenerator<C> worldGenerator, final C worldGenFeatureConfigured) {
        final NMSService nmsService = serviceSupplier.get().getNMSService();

        return nmsService.generate(generatorAccess.getMinecraftWorld().getWorld(), biome, nmsService.getNMSUtil().getOre(((WorldGenFeatureOreConfiguration) worldGenFeatureConfigured).d.getBlock()), new ChunkCoordIntPair(blockPosition.getX() >> 4, blockPosition.getZ() >> 4), worldGenFeatureChanceDecoratorCountConfiguration, worldGenFeatureConfigured,
                null,
                (configuration, featureConfiguration) -> super.a(generatorAccess, chunkGenerator, random, blockPosition, (WorldGenFeatureChanceDecoratorCountConfiguration) configuration, worldGenerator, (C) featureConfiguration)
                , random);
    }
}
