package net.minecraft.network.chat;

import net.minecraft.network.chat.contents.LiteralContents;

import com.google.common.collect.Lists;

public class TextComponent extends MutableComponent
{
    public TextComponent(String text) {
        super(new LiteralContents(text), Lists.newArrayList(), Style.EMPTY);
    }
}
