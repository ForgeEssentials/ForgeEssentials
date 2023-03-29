package com.forgeessentials.core.preloader.mixin;

public enum Mixins {

    MixinBlock("block.MixinBlock"),
    MixinBlockEndPortal("block.MixinBlockEndPortal"),
    MixinBlockFire("block.MixinBlockFire"),
    MixinBlockPortal("block.MixinBlockPortal"),
    MixinCommandHandler("command.MixinCommandHandler"),
    MixinEntity("entity.MixinEntity"),
    MixinEntityTracker("entity.MixinEntityTracker"),
    MixinEntityPlayerMP("entity.player.MixinEntityPlayerMP"),
    MixinCraftingManager("item.crafting.MixinCraftingManager"),
    MixinNetHandlerPlayServer("network.MixinNetHandlerPlayServer"),
    MixinNetHandlerPlayServerCauldron("network.MixinNetHandlerPlayServerCauldron"),
    MixinNetHandlerPlayServerForge("network.MixinNetHandlerPlayServerForge"),
    MixinSimpleChannelHandlerWrapper("network.MixinSimpleChannelHandlerWrapper"),
    MixinItemInWorldManager("server.management.MixinItemInWorldManager"),
    MixinDimensionManager("MixinDimensionManager");
    private String mixinRelativePath;

    Mixins(String mixinRelativePath) {
        this.mixinRelativePath = mixinRelativePath;
    }

    public String getMixinRelativePath() {
        return mixinRelativePath;
    }

    public String getMixinClassName() {
    //return only what is after the last .
        return getMixinRelativePath().substring(getMixinRelativePath().lastIndexOf('.')+1);
    }

}
