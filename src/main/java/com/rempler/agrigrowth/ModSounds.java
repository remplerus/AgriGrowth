package com.rempler.agrigrowth;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class ModSounds {
    public static DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AgriGrowth.MOD_ID);
    public static final RegistryObject<SoundEvent> FART = SOUND_EVENTS.register("fart", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(AgriGrowth.MOD_ID, "fart")));

    public static void init(IEventBus eventBus) {
        AgriGrowth.LOGGER.info("Registering sounds");
        SOUND_EVENTS.register(eventBus);
    }
}
