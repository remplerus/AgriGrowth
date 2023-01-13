package com.rempler.agrigrowth;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.DoubleValue GROW_SPEED;
    private static final ForgeConfigSpec.IntValue GROW_RANGE;
    private static final ForgeConfigSpec.IntValue GROW_HEIGHT;
    private static final ForgeConfigSpec.ConfigValue<List<String>> BLACKLIST;
    private static final ForgeConfigSpec.BooleanValue MYST_AGRI;
    private static final ForgeConfigSpec.BooleanValue ENABLE_POSTS;
    private static final ForgeConfigSpec.BooleanValue SPAWN_PARTICLES;

    public static Double getGrowSpeed() { return GROW_SPEED.get(); }
    public static int getGrowRange() { return GROW_RANGE.get(); }
    public static int getGrowHeight() { return GROW_HEIGHT.get(); }
    public static List<String> getBlacklist() { return BLACKLIST.get(); }
    public static boolean activateMystAgri() { return MYST_AGRI.get(); }
    public static boolean enablePosts() { return ENABLE_POSTS.get(); }
    public static boolean shouldSpawnParticles() { return SPAWN_PARTICLES.get(); }

    static {
        List<String> blacklist = new ArrayList<>();
        blacklist.add("minecraft:netherrack");
        blacklist.add("minecraft:warped_nylium");
        blacklist.add("minecraft:crimson_nylium");
        blacklist.add("minecraft:grass_block");
        blacklist.add("minecraft:grass");
        blacklist.add("minecraft:tall_grass");
        BUILDER.push("Grow speed");
        GROW_SPEED = BUILDER.defineInRange("grow_speed", 0.2, 0, 1);
        BUILDER.pop();
        BUILDER.push("Grow range");
        GROW_RANGE = BUILDER.defineInRange("grow_range", 4, 1, 128);
        BUILDER.pop();
        BUILDER.push("Grow height");
        GROW_HEIGHT = BUILDER.defineInRange("grow_height", 2, 1, 64);
        BUILDER.pop();
        BUILDER.push("Blacklist blocks");
        BLACKLIST = BUILDER.define("blacklist", blacklist);
        BUILDER.pop();
        BUILDER.push("Activate Mystical Agriculture");
        MYST_AGRI = BUILDER.define("activate_myst_agri", false);
        BUILDER.pop();
        BUILDER.push("Enable posts");
        ENABLE_POSTS = BUILDER.define("enable_posts", true);
        BUILDER.pop();
        BUILDER.push("Should spawn particles");
        SPAWN_PARTICLES = BUILDER.define("spawn_particles", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
