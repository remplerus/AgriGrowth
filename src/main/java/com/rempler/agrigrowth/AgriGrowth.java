package com.rempler.agrigrowth;

import com.blakebr0.mysticalagriculture.api.crop.ICropProvider;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@SuppressWarnings("unused")
@Mod("agrigrowth")
public class AgriGrowth
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public AgriGrowth() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, "agrigrowth.toml");
        LOGGER.info("Loading AgriGrowth Mod");
        MinecraftForge.EVENT_BUS.addListener(AgriGrowth::playerTickEvent);
    }

    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.player.level.isClientSide) {
            return;
        }
        RandomSource rand = RandomSource.create();
        Player player = event.player;
        if (player.isShiftKeyDown() && rand.nextDouble() < Config.getGrowSpeed()) {
            applyGrowing(player, rand);
        }
    }

    private static void applyGrowing(Player player, RandomSource rand) {
        Level level = player.level;
        BlockPos pos = player.blockPosition();

        if (level.isClientSide) {
            return;
        }

        int a = Config.getGrowRange()/2;
        int b = Config.getGrowHeight()/2;

        for (int x = -a; x <= a; x++) {
            for (int z = -a; z <= a; z++) {
                for (int y = -b; y <= b; y++) {
                    BlockPos blockPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (ModList.get().isLoaded("agricraft") && Config.enablePosts()) {
                        /*
                        Optional<IAgriCrop> optional = AgriApi.getCrop(level, blockPos);
                        if (optional.isPresent()) {
                            IAgriCrop crop = optional.get();
                            if (rand.nextDouble() < Config.getGrowSpeed()) {
                                crop.applyGrowthTick();
                                spawnParticles(player, level, blockPos);
                            }
                        }
                        */
                        player.sendSystemMessage(Component.literal("AgriCraft has no integration yet!"));
                    } else if (rand.nextDouble() < Config.getGrowSpeed()) {
                        BlockState state = level.getBlockState(blockPos);
                        if (!(state.getBlock() instanceof AirBlock || Config.getBlacklist().contains(ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString()))) {
                            if (ModList.get().isLoaded("mysticalagriculture")) {
                                if (Config.activateMystAgri() && state.getBlock() instanceof ICropProvider) {
                                    ((CropBlock) state.getBlock()).performBonemeal((ServerLevel) level, rand, blockPos, state);
                                    spawnParticles(player, level, blockPos);
                                    return;
                                } else if (!Config.activateMystAgri() && state.getBlock() instanceof ICropProvider) {
                                    return;
                                } else {
                                    standardGrow(player, level, blockPos, state);
                                }
                            } else {
                                standardGrow(player, level, blockPos, state);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void standardGrow(Player player, LevelAccessor level, BlockPos blockPos, BlockState state) {
        if (state.hasProperty(CropBlock.AGE)) {
            if (state.getValue(CropBlock.AGE) >= 7) {
                return;
            }
            BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), (Level) level, blockPos, player);
            spawnParticles(player, level, blockPos);
        } else if (state.getBlock() instanceof BonemealableBlock) {
            BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), (Level) level, blockPos, player);
            spawnParticles(player, level, blockPos);
        }
    }

    private static void spawnParticles(Player player, LevelAccessor level, BlockPos blockPos) {
        if (Config.shouldSpawnParticles()) {
            double d0 = level.getRandom().nextDouble();
            for (int a = 0; a < 2; a++) {
                ((ServerLevel) level).sendParticles((ServerPlayer) player, ParticleTypes.CLOUD, false, blockPos.getX() + d0,
                        blockPos.getY() + d0, blockPos.getZ() + d0, 1, 0.5, 0.5, 0.5, 0.25);
            }
        }
    }
}
