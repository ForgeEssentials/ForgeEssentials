package net.minecraft.network.chat;

import net.minecraft.network.chat.contents.TranslatableContents;

import com.google.common.collect.Lists;

public class TranslatableComponent extends MutableComponent
{
    public TranslatableComponent(String text, Object... contents) {
        super(new TranslatableContents(text, contents), Lists.newArrayList(), Style.EMPTY);
    }
}
