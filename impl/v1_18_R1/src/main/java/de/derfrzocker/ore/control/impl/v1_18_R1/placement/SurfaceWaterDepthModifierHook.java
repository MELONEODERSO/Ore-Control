/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.derfrzocker.ore.control.impl.v1_18_R1.placement;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.feature.impl.v1_18_R1.placement.configuration.SurfaceWaterDepthModifierConfiguration;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.config.ConfigManager;
import de.derfrzocker.ore.control.impl.v1_18_R1.NMSReflectionNames;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Random;

public class SurfaceWaterDepthModifierHook extends MinecraftPlacementModifierHook<SurfaceWaterDepthFilter, SurfaceWaterDepthModifierConfiguration> {

    public static SurfaceWaterDepthModifierConfiguration createDefaultConfiguration(@NotNull SurfaceWaterDepthFilter defaultModifier, @NotNull FeaturePlacementModifier<?> modifier) {
        try {
            Field maxWaterDepth = SurfaceWaterDepthFilter.class.getDeclaredField(NMSReflectionNames.SURFACE_WATER_DEPTH_FILTER_MAX_WATER_DEPTH);
            maxWaterDepth.setAccessible(true);
            Object value = maxWaterDepth.get(defaultModifier);
            return new SurfaceWaterDepthModifierConfiguration(modifier, new FixedDoubleToIntegerValue(NumberConversions.toInt(value)));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SurfaceWaterDepthModifierHook(@NotNull Registries registries, ConfigManager configManager, @NotNull Biome biome, @NotNull NamespacedKey namespacedKey, @NotNull SurfaceWaterDepthFilter defaultModifier) {
        super(registries, configManager, "surface_water_depth_filter", defaultModifier, biome, namespacedKey);
    }

    @Override
    public SurfaceWaterDepthModifierConfiguration createDefaultConfiguration(@NotNull SurfaceWaterDepthFilter defaultModifier) {
        return createDefaultConfiguration(defaultModifier, getPlacementModifier());
    }

    @Override
    public SurfaceWaterDepthFilter createModifier(@NotNull SurfaceWaterDepthModifierConfiguration defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull SurfaceWaterDepthModifierConfiguration configuration) {
        int maxWaterDepth;
        if (configuration.getMaxWaterDepth() == null) {
            maxWaterDepth = defaultConfiguration.getMaxWaterDepth().getValue(worldInfo, random, position, limitedRegion);
        } else {
            maxWaterDepth = configuration.getMaxWaterDepth().getValue(worldInfo, random, position, limitedRegion);
        }

        return SurfaceWaterDepthFilter.forMaxDepth(maxWaterDepth);
    }
}