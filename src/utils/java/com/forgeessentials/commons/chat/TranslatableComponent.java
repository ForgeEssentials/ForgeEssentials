package com.forgeessentials.commons.chat;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;

import com.google.common.collect.Lists;

public class TranslatableComponent extends MutableComponent
{
    public TranslatableComponent(String text, Object... contents) {
        super(new TranslatableContents(text, contents), Lists.newArrayList(), Style.EMPTY);
    }
}
