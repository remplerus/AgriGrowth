package com.rempler.agrigrowth.compat;

import com.rempler.agrigrowth.AgriGrowth;
import com.rempler.agrigrowth.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

//TODO re-enable when AgriCraft is updated
public class AgriCraftCompat {
    private AgriCraftCompat(){}
    private final RandomSource rand = RandomSource.create();

    public static void initAgriCompat(Level level, BlockPos blockPos, Player player) {
        //Optional<IAgriCrop> optional = AgriApi.getCrop(level, blockPos);
        //if (optional.isPresent()) {
        //    IAgriCrop crop = optional.get();
        //    if (rand.nextDouble() < Config.getGrowSpeed()) {
        //        crop.applyGrowthTick();
        //        AgriGrowth.spawnParticles(player, level, blockPos);
        //    }
        //}
    }
}
