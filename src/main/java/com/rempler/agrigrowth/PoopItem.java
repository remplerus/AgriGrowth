package com.rempler.agrigrowth;

import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;

public class PoopItem extends BoneMealItem {
    public PoopItem() {
        super(new Item.Properties().stacksTo(64));
    }
}
