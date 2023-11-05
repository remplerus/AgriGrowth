package com.rempler.agrigrowth.compat;

import com.rempler.agrigrowth.AgriGrowth;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

//TODO re-enable when Mystical Agriculture is updated
public class MysticalAgriCompat {
    private MysticalAgriCompat() {}

    public static void initMysticalAgriCompat(Level level, BlockPos blockPos, BlockState state, Player player) {
         //if(state.getBlock() instanceof ICropProvider) {
         //   ((CropBlock) state.getBlock()).performBonemeal((ServerLevel) level, level.getRandom(), blockPos, state);
         //   AgriGrowth.spawnParticles(player, level, blockPos);
         //}
    }
}
