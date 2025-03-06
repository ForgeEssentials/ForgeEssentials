package com.forgeessentials.commons.chat;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;

import com.google.common.collect.Lists;

public class TextComponent extends MutableComponent
{
    public TextComponent(String text) {
        super(new LiteralContents(text), Lists.newArrayList(), Style.EMPTY);
    }
}
