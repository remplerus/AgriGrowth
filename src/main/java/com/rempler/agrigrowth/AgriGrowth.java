package com.rempler.agrigrowth;

import com.mojang.logging.LogUtils;
import com.rempler.agrigrowth.compat.AgriCraftCompat;
import com.rempler.agrigrowth.compat.MysticalAgriCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.Objects;

@Mod(AgriGrowth.MOD_ID)
public class AgriGrowth {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static boolean hasPlayedSound = false;
    private static int tickCounter = 0;
    private static int fartCounter = 0;
    public static final String MOD_ID = "agrigrowth";
    public static TagKey<Block> WHITELIST = TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation(MOD_ID, "whitelist"));
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Item> POOP = ITEMS.register("poop", PoopItem::new);

    public AgriGrowth() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, MOD_ID + ".toml");
        LOGGER.info("Loading "+ MOD_ID + " Mod");
        NeoForge.EVENT_BUS.addListener(AgriGrowth::playerTickEvent);
        NeoForge.EVENT_BUS.addListener(AgriGrowth::onRightClickBlockEvent);
        NeoForge.EVENT_BUS.addListener(AgriGrowth::onBonemealEvent);
        ModSounds.init(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        Level level = event.player.level();
        Player player = event.player;
        if (player.isShiftKeyDown() || player.isSprinting()) {
            if (Config.shouldTwerk() && level.getRandom().nextDouble() < Config.getRandomSpeed() && tickCounter >= Config.getGrowSpeed()) {
                if (level.isClientSide) {
                    return;
                }
                tickCounter = 0;
                applyGrowing(player);
            }
            if (Config.shouldPoopSpawn()) {
                fartCounter++;
                if (fartCounter > 200) {
                    if (level.getRandom().nextInt() * 200 <= fartCounter) {
                        if (level.isClientSide) {
                            if (hasPlayedSound) {
                                return;
                            }
                            level.playSound(player, player.getX(), player.getY(), player.getZ(), ModSounds.FART.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                            hasPlayedSound = true;
                        } else if (hasPlayedSound) {
                            level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), new ItemStack(POOP.get())));
                            fartCounter = 0;
                            hasPlayedSound = false;
                        }
                    }
                }
            }
            tickCounter++;
        }
    }

    public static void onBonemealEvent(BonemealEvent event) {
        if (event.getStack().is(POOP.get())) {
            if (!(event.getBlock().is(BlockTags.CROPS) || event.getBlock().is(BlockTags.SAPLINGS) || event.getBlock().is(WHITELIST))) {
                event.setCanceled(true);
            }
        }
    }

    public static void onRightClickBlockEvent(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = onRightClick(event.getEntity(), event.getHand(), event.getLevel(), event.getHitVec());

        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    private static InteractionResult onRightClick(Player player, InteractionHand hand, Level level, BlockHitResult blockHit) {
        if (player.isSpectator() || hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }

        BlockState state = level.getBlockState(blockHit.getBlockPos());
        Block originalBlock = state.getBlock();
        ItemStack stack = player.getItemInHand(hand);

        if (originalBlock instanceof CropBlock || originalBlock instanceof CocoaBlock || originalBlock instanceof NetherWartBlock) {
            if (isMature(state)) {
                if (!level.isClientSide) {
                    level.setBlockAndUpdate(blockHit.getBlockPos(), getReplantState(state));
                    dropStacks(state, (ServerLevel) level, blockHit.getBlockPos(), player, player.getItemInHand(hand), blockHit);
                } else {
                    player.playSound(originalBlock instanceof NetherWartBlock ? SoundEvents.NETHER_WART_PLANTED : SoundEvents.CROP_PLANTED, 1.0f, 1.0f);
                }

                return InteractionResult.SUCCESS;
            }
        }
        else if (originalBlock instanceof SugarCaneBlock) {
            if (blockHit.getDirection() == Direction.UP && stack.getItem() == Items.SUGAR_CANE) {
                return InteractionResult.PASS;
            }
            int count = 1;
            BlockPos bottom = blockHit.getBlockPos().below();
            while (level.getBlockState(bottom).is(Blocks.SUGAR_CANE)) {
                count++;
                bottom = bottom.below();
            }

            if (count == 1 && !level.getBlockState(blockHit.getBlockPos().above()).is(Blocks.SUGAR_CANE)) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide) {
                level.destroyBlock(bottom.above(2), true);
            } else {
                player.playSound(SoundEvents.CROP_PLANTED, 1.0f, 1.0f);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private static void applyGrowing(Player player) {
        Level level = player.level();
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
                    if (ModList.get().isLoaded("agricraft")) {
                        if (Config.enablePosts()) {
                            player.sendSystemMessage(Component.literal("AgriCraft has no integration yet!"));
                        }
                        if (Config.activateAgriCraft()) {
                            AgriCraftCompat.initAgriCompat(level, blockPos, player);
                        }
                    } else if (level.getRandom().nextDouble() < Config.getRandomSpeed()) {
                        BlockState state = level.getBlockState(blockPos);
                        if (!(state.getBlock() instanceof AirBlock || Config.getBlacklist().contains(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(state.getBlock())).toString()))) {
                            if (ModList.get().isLoaded("mysticalagriculture")) {
                                if (Config.enablePosts()) {
                                    player.sendSystemMessage(Component.literal("Mystical Agriculture has no integration yet!"));
                                }
                                if (Config.activateMystAgri()) {
                                    MysticalAgriCompat.initMysticalAgriCompat(level, blockPos, state, player);
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
            if (isMature(state)) {
                return;
            }
            BoneMealItem.applyBonemeal(POOP.get().getDefaultInstance(), (Level) level, blockPos, player);
            spawnParticles(player, level, blockPos);
        } else if (state.getBlock() instanceof BonemealableBlock) {
            BoneMealItem.applyBonemeal(POOP.get().getDefaultInstance(), (Level) level, blockPos, player);
            spawnParticles(player, level, blockPos);
        }
    }

    private static boolean isMature(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof CocoaBlock) {
            return state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
        }
        else if (block instanceof CropBlock cropBlock) {
            return cropBlock.isMaxAge(state);
        }
        else if (block instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
        }
        return false;
    }

    public static void spawnParticles(Player player, LevelAccessor level, BlockPos blockPos) {
        if (Config.shouldSpawnParticles()) {
            double d0 = level.getRandom().nextDouble();
            for (int a = 0; a < 2; a++) {
                //TODO: make custom particle
                ((ServerLevel) level).sendParticles((ServerPlayer) player, ParticleTypes.CLOUD, false, blockPos.getX() + d0,
                        blockPos.getY() + d0, blockPos.getZ() + d0, 1, 0.5, 0.5, 0.5, 0.01);
            }
        }
    }

    private static BlockState getReplantState(BlockState state) {
        if (state.getBlock() instanceof CocoaBlock) {
            return state.setValue(CocoaBlock.AGE, 0);
        } else if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.getStateForAge(0);
        } else if (state.getBlock() instanceof NetherWartBlock) {
            return state.setValue(NetherWartBlock.AGE, 0);
        }

        return state;
    }

    private static void dropStacks(BlockState state, ServerLevel level, BlockPos pos, Entity entity,
                                   ItemStack toolStack, HitResult hitResult) {
        Item replant = state.getBlock().getCloneItemStack(state, hitResult, level, pos, (Player) entity).getItem();
        final boolean[] removedReplant = { false };
        Block.getDrops(state, level, pos, null, entity, toolStack).forEach(stack -> {
            if (!removedReplant[0] && stack.getItem() == replant) {
                stack.setCount(stack.getCount() - 1);
                removedReplant[0] = true;
            }
            Block.popResource(level, pos, stack);
        });
        state.spawnAfterBreak(level, pos, toolStack, true);
    }
}
