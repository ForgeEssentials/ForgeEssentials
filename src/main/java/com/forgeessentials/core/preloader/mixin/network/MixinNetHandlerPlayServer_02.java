package com.forgeessentials.core.preloader.mixin.network;

//import io.netty.buffer.Unpooled;
//
//import java.io.ByteArrayInputStream;
//import java.io.DataInputStream;
//import java.io.IOException;
//
//import net.minecraft.command.server.CommandBlockLogic;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityMinecartCommandBlock;
//import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.init.Items;
//import net.minecraft.inventory.Container;
//import net.minecraft.inventory.ContainerBeacon;
//import net.minecraft.inventory.ContainerMerchant;
//import net.minecraft.inventory.ContainerRepair;
//import net.minecraft.inventory.Slot;
//import net.minecraft.item.ItemEditableBook;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.ItemWritableBook;
//import net.minecraft.nbt.NBTTagString;
//import net.minecraft.network.NetHandlerPlayServer;
//import net.minecraft.network.PacketBuffer;
//import net.minecraft.network.play.client.C17PacketCustomPayload;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.tileentity.TileEntityBeacon;
//import net.minecraft.tileentity.TileEntityCommandBlock;
//import net.minecraft.util.ChatAllowedCharacters;
//import net.minecraft.util.ChatComponentTranslation;
//import net.minecraftforge.permission.PermissionManager;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Overwrite;
//import org.spongepowered.asm.mixin.Shadow;
//
//import com.google.common.base.Charsets;
//
//@Mixin(NetHandlerPlayServer.class)
//public abstract class MixinNetHandlerPlayServer_02
//{
//
//    // @Shadow
//    private static final Logger logger = LogManager.getLogger(NetHandlerPlayServer.class);
//
//    @Shadow
//    public EntityPlayerMP playerEntity;
//
//    @Shadow
//    public MinecraftServer serverController;
//
//    // patch method
//    @Overwrite
//    public void processVanilla250Packet(C17PacketCustomPayload packet)
//    {
//        PacketBuffer packetbuffer;
//        ItemStack itemstack;
//        ItemStack itemstack1;
//
//        if ("MC|BEdit".equals(packet.getChannelName()))
//        {
//            packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(packet.getBufferData()));
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
//                if (!ItemWritableBook.validBookPageTagContents(itemstack.getTagCompound()))
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
//                    return;
//                }
//            }
//            catch (Exception exception4)
//            {
//                logger.error("Couldn\'t handle book info", exception4);
//                return;
//            }
//            finally
//            {
//                packetbuffer.release();
//            }
//
//            return;
//        }
//        else if ("MC|BSign".equals(packet.getChannelName()))
//        {
//            packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(packet.getBufferData()));
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
//                        itemstack1.setItem(Items.written_book);
//                    }
//
//                    return;
//                }
//            }
//            catch (Exception exception3)
//            {
//                logger.error("Couldn\'t sign book", exception3);
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
//            if ("MC|TrSel".equals(packet.getChannelName()))
//            {
//                try
//                {
//                    datainputstream = new DataInputStream(new ByteArrayInputStream(packet.getBufferData()));
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
//                    logger.error("Couldn\'t select trade", exception2);
//                }
//            }
//            else if ("MC|AdvCdm".equals(packet.getChannelName()))
//            {
//                if (!serverController.isCommandBlockEnabled())
//                {
//                    playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notEnabled", new Object[0]));
//                }
//                else if (PermissionManager.checkPermission(playerEntity, "mc.cmdblocks") && playerEntity.capabilities.isCreativeMode)
//                {
//                    packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(packet.getBufferData()));
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
//                        logger.error("Couldn\'t set command block", exception1);
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
//            else if ("MC|Beacon".equals(packet.getChannelName()))
//            {
//                if (playerEntity.openContainer instanceof ContainerBeacon)
//                {
//                    try
//                    {
//                        datainputstream = new DataInputStream(new ByteArrayInputStream(packet.getBufferData()));
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
//                        logger.error("Couldn\'t set beacon", exception);
//                    }
//                }
//            }
//            else if ("MC|ItemName".equals(packet.getChannelName()) && playerEntity.openContainer instanceof ContainerRepair)
//            {
//                ContainerRepair containerrepair = (ContainerRepair) playerEntity.openContainer;
//
//                if (packet.getBufferData() != null && packet.getBufferData().length >= 1)
//                {
//                    String s = ChatAllowedCharacters.filerAllowedCharacters(new String(packet.getBufferData(), Charsets.UTF_8));
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
//
//}
