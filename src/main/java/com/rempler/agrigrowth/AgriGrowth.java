package com.rempler.agrigrowth;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.Random;

@SuppressWarnings("unused")
@Mod("agrigrowth")
public class AgriGrowth
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public AgriGrowth()
    {
        LOGGER.info("Loading AgriGrowth Mod");
        MinecraftForge.EVENT_BUS.addListener(AgriGrowth::playerTickEvent);
    }

    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        Random rand = new Random();
        Player player = event.player;
        if (player.isShiftKeyDown() && rand.nextDouble() < 0.2) {
            applyGrowing(player, rand);
        }
    }

    private static void applyGrowing(Player player, Random rand) {
        Level level = player.level;
        BlockPos pos = player.blockPosition();

        if (level.isClientSide) {
            return;
        }

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos blockPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    Optional<IAgriCrop> optional = AgriApi.getCrop(level, blockPos);
                    if (optional.isPresent()) {
                        IAgriCrop crop = optional.get();
                        if (rand.nextDouble() < 0.5) {
                            crop.applyGrowthTick();
                            spawnParticles(level, blockPos, level.random);
                        }
                    }
                }
            }
        }
    }

    private static void spawnParticles(LevelAccessor level, BlockPos blockPos, Random random) {
        double d0 = random.nextDouble();
        for (int a = 0; a < 2; a++) {
            level.addParticle(ParticleTypes.HAPPY_VILLAGER, blockPos.getX() + d0, blockPos.getY() + d0, blockPos.getZ() + d0,
                    0D, 0D,0D);
        }
    }
}
