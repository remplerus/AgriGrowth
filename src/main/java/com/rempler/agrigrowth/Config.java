package com.rempler.agrigrowth;

import net.neoforged.neoforge.common.ModConfigSpec;
import java.util.ArrayList;
import java.util.List;

public class Config {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.DoubleValue RANDOM_SPEED;
    private static final ModConfigSpec.IntValue GROW_SPEED;
    private static final ModConfigSpec.IntValue GROW_RANGE;
    private static final ModConfigSpec.IntValue GROW_HEIGHT;
    private static final ModConfigSpec.ConfigValue<List<String>> BLACKLIST;
    private static final ModConfigSpec.BooleanValue MYST_AGRI;
    private static final ModConfigSpec.BooleanValue ENABLE_POSTS;
    private static final ModConfigSpec.BooleanValue SPAWN_PARTICLES;
    private static final ModConfigSpec.BooleanValue AGRICRAFT;
    private static final ModConfigSpec.BooleanValue TWERKING;
    private static final ModConfigSpec.BooleanValue POOP;

    public static Double getRandomSpeed() { return RANDOM_SPEED.get(); }
    public static int getGrowSpeed() { return GROW_SPEED.get(); }
    public static int getGrowRange() { return GROW_RANGE.get(); }
    public static int getGrowHeight() { return GROW_HEIGHT.get(); }
    public static List<String> getBlacklist() { return BLACKLIST.get(); }
    public static boolean activateMystAgri() { return MYST_AGRI.get(); }
    public static boolean activateAgriCraft() { return AGRICRAFT.get(); }
    public static boolean enablePosts() { return ENABLE_POSTS.get(); }
    public static boolean shouldSpawnParticles() { return SPAWN_PARTICLES.get(); }
    public static boolean shouldTwerk() { return TWERKING.get(); }
    public static boolean shouldPoopSpawn() { return POOP.get(); }


    static {
        List<String> blacklist = new ArrayList<>();
        blacklist.add("minecraft:netherrack");
        blacklist.add("minecraft:warped_nylium");
        blacklist.add("minecraft:crimson_nylium");
        blacklist.add("minecraft:grass_block");
        blacklist.add("minecraft:grass");
        blacklist.add("minecraft:tall_grass");
        BUILDER.push("Grow speed");
        RANDOM_SPEED = BUILDER.comment("defines how lucky you are with growing ").defineInRange("random_speed", 0.1, 0, 1);
        GROW_SPEED = BUILDER.comment("defines how fast the random_speed should trigger, executes every here defined ticks").defineInRange("grow_speed", 10, 1, 10000);
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
        BUILDER.push("Activate AgriCraft");
        AGRICRAFT = BUILDER.define("activate_agricraft", false);
        BUILDER.pop();
        BUILDER.push("Enable posts");
        ENABLE_POSTS = BUILDER.define("enable_posts", true);
        BUILDER.pop();
        BUILDER.push("Should spawn particles");
        SPAWN_PARTICLES = BUILDER.define("spawn_particles", true);
        BUILDER.pop();
        BUILDER.push("Should twerking work");
        TWERKING = BUILDER.define("twerking", true);
        BUILDER.pop();
        BUILDER.push("Should poop spawn");
        POOP = BUILDER.define("poop", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
