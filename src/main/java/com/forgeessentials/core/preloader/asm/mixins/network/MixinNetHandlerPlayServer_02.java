package com.forgeessentials.core.preloader.asm.mixins.network;

import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer_02
{
    
//    @Shadow
//    public EntityPlayerMP playerEntity;
//
//    @Shadow
//    public MinecraftServer serverController;
//
//    // patch method
//    @Overwrite
//    public void processVanilla250Packet(C17PacketCustomPayload p_147349_1_)
//    {
//        PacketBuffer packetbuffer;
//        ItemStack itemstack;
//        ItemStack itemstack1;
//
//        if ("MC|BEdit".equals(p_147349_1_.func_149559_c()))
//        {
//            packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(p_147349_1_.func_149558_e()));
//
//            try
//            {
//                itemstack = packetbuffer.readItemStackFromBuffer();
//
//                if (itemstack == null)
//                {
//                    return;
//                }
//
//                if (!ItemWritableBook.func_150930_a(itemstack.getTagCompound()))
//                {
//                    throw new IOException("Invalid book tag!");
//                }
//
//                itemstack1 = playerEntity.inventory.getCurrentItem();
//
//                if (itemstack1 != null)
//                {
//                    if (itemstack.getItem() == Items.writable_book && itemstack.getItem() == itemstack1.getItem())
//                    {
//                        itemstack1.setTagInfo("pages", itemstack.getTagCompound().getTagList("pages", 8));
//                    }
//
//                    return;
//                }
//            }
//            catch (Exception exception4)
//            {
//                NetHandlerPlayServer.logger.error("Couldn\'t handle book info", exception4);
//                return;
//            }
//            finally
//            {
//                packetbuffer.release();
//            }
//
//            return;
//        }
//        else if ("MC|BSign".equals(p_147349_1_.func_149559_c()))
//        {
//            packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(p_147349_1_.func_149558_e()));
//
//            try
//            {
//                itemstack = packetbuffer.readItemStackFromBuffer();
//
//                if (itemstack != null)
//                {
//                    if (!ItemEditableBook.validBookTagContents(itemstack.getTagCompound()))
//                    {
//                        throw new IOException("Invalid book tag!");
//                    }
//
//                    itemstack1 = playerEntity.inventory.getCurrentItem();
//
//                    if (itemstack1 == null)
//                    {
//                        return;
//                    }
//
//                    if (itemstack.getItem() == Items.written_book && itemstack1.getItem() == Items.writable_book)
//                    {
//                        itemstack1.setTagInfo("author", new NBTTagString(playerEntity.getName()));
//                        itemstack1.setTagInfo("title", new NBTTagString(itemstack.getTagCompound().getString("title")));
//                        itemstack1.setTagInfo("pages", itemstack.getTagCompound().getTagList("pages", 8));
//                        itemstack1.func_150996_a(Items.written_book);
//                    }
//
//                    return;
//                }
//            }
//            catch (Exception exception3)
//            {
//                NetHandlerPlayServer.logger.error("Couldn\'t sign book", exception3);
//                return;
//            }
//            finally
//            {
//                packetbuffer.release();
//            }
//
//            return;
//        }
//        else
//        {
//            DataInputStream datainputstream;
//            int i;
//
//            if ("MC|TrSel".equals(p_147349_1_.func_149559_c()))
//            {
//                try
//                {
//                    datainputstream = new DataInputStream(new ByteArrayInputStream(p_147349_1_.func_149558_e()));
//                    i = datainputstream.readInt();
//                    Container container = playerEntity.openContainer;
//
//                    if (container instanceof ContainerMerchant)
//                    {
//                        ((ContainerMerchant) container).setCurrentRecipeIndex(i);
//                    }
//                }
//                catch (Exception exception2)
//                {
//                    NetHandlerPlayServer.logger.error("Couldn\'t select trade", exception2);
//                }
//            }
//            else if ("MC|AdvCdm".equals(p_147349_1_.func_149559_c()))
//            {
//                if (!serverController.isCommandBlockEnabled())
//                {
//                    playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notEnabled", new Object[0]));
//                }
//                else if (PermissionManager.checkPermission(playerEntity, "mc.cmdblocks") && playerEntity.capabilities.isCreativeMode)
//                {
//                    packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(p_147349_1_.func_149558_e()));
//
//                    try
//                    {
//                        byte b0 = packetbuffer.readByte();
//                        CommandBlockLogic commandblocklogic = null;
//
//                        if (b0 == 0)
//                        {
//                            TileEntity tileentity = playerEntity.worldObj.getTileEntity(packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt());
//
//                            if (tileentity instanceof TileEntityCommandBlock)
//                            {
//                                commandblocklogic = ((TileEntityCommandBlock) tileentity).func_145993_a();
//                            }
//                        }
//                        else if (b0 == 1)
//                        {
//                            Entity entity = playerEntity.worldObj.getEntityByID(packetbuffer.readInt());
//
//                            if (entity instanceof EntityMinecartCommandBlock)
//                            {
//                                commandblocklogic = ((EntityMinecartCommandBlock) entity).func_145822_e();
//                            }
//                        }
//
//                        String s1 = packetbuffer.readStringFromBuffer(packetbuffer.readableBytes());
//
//                        if (commandblocklogic != null)
//                        {
//                            commandblocklogic.func_145752_a(s1);
//                            commandblocklogic.func_145756_e();
//                            playerEntity.addChatMessage(new ChatComponentTranslation("advMode.setCommand.success", new Object[] { s1 }));
//                        }
//                    }
//                    catch (Exception exception1)
//                    {
//                        NetHandlerPlayServer.logger.error("Couldn\'t set command block", exception1);
//                    }
//                    finally
//                    {
//                        packetbuffer.release();
//                    }
//                }
//                else
//                {
//                    playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notAllowed", new Object[0]));
//                }
//            }
//            else if ("MC|Beacon".equals(p_147349_1_.func_149559_c()))
//            {
//                if (playerEntity.openContainer instanceof ContainerBeacon)
//                {
//                    try
//                    {
//                        datainputstream = new DataInputStream(new ByteArrayInputStream(p_147349_1_.func_149558_e()));
//                        i = datainputstream.readInt();
//                        int j = datainputstream.readInt();
//                        ContainerBeacon containerbeacon = (ContainerBeacon) playerEntity.openContainer;
//                        Slot slot = containerbeacon.getSlot(0);
//
//                        if (slot.getHasStack())
//                        {
//                            slot.decrStackSize(1);
//                            TileEntityBeacon tileentitybeacon = containerbeacon.func_148327_e();
//                            tileentitybeacon.setPrimaryEffect(i);
//                            tileentitybeacon.setSecondaryEffect(j);
//                            tileentitybeacon.markDirty();
//                        }
//                    }
//                    catch (Exception exception)
//                    {
//                        NetHandlerPlayServer.logger.error("Couldn\'t set beacon", exception);
//                    }
//                }
//            }
//            else if ("MC|ItemName".equals(p_147349_1_.func_149559_c()) && playerEntity.openContainer instanceof ContainerRepair)
//            {
//                ContainerRepair containerrepair = (ContainerRepair) playerEntity.openContainer;
//
//                if (p_147349_1_.func_149558_e() != null && p_147349_1_.func_149558_e().length >= 1)
//                {
//                    String s = ChatAllowedCharacters.filerAllowedCharacters(new String(p_147349_1_.func_149558_e(), Charsets.UTF_8));
//
//                    if (s.length() <= 30)
//                    {
//                        containerrepair.updateItemName(s);
//                    }
//                }
//                else
//                {
//                    containerrepair.updateItemName("");
//                }
//            }
//        }
//    }
    
}
