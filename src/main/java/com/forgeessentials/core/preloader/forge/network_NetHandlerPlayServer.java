package com.forgeessentials.core.preloader.forge;

import io.netty.buffer.Unpooled;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.util.events.forge.SignEditEvent;
import com.google.common.base.Charsets;

public class network_NetHandlerPlayServer
{
    // patch method
    public static void processUpdateSign(NetHandlerPlayServer net, C12PacketUpdateSign p_147343_1_)
    {
        net.playerEntity.func_143004_u();
        WorldServer worldserver = net.serverController.worldServerForDimension(net.playerEntity.dimension);

        if (worldserver.blockExists(p_147343_1_.func_149588_c(), p_147343_1_.func_149586_d(), p_147343_1_.func_149585_e()))
        {
            TileEntity tileentity = worldserver.getTileEntity(p_147343_1_.func_149588_c(), p_147343_1_.func_149586_d(), p_147343_1_.func_149585_e());

            if (tileentity instanceof TileEntitySign)
            {
                TileEntitySign tileentitysign = (TileEntitySign)tileentity;

                if (!tileentitysign.func_145914_a() || tileentitysign.func_145911_b() != net.playerEntity)
                {
                    net.serverController.logWarning("Player " + net.playerEntity.getCommandSenderName() + " just tried to change non-editable sign");
                    return;
                }
            }

            String[] text = onSignEditEvent(net, p_147343_1_);
            if (text == null)return;

            int i;
            int j;

            for (j = 0; j < 4; ++j)
            {
                boolean flag = true;

                if (text[j].length() > 15)
                {
                    flag = false;
                }
                else
                {
                    for (i = 0; i < text[j].length(); ++i)
                    {
                        if (!ChatAllowedCharacters.isAllowedCharacter(text[j].charAt(i)))
                        {
                            flag = false;
                        }
                    }
                }

                if (!flag)
                {
                    text[j] = "!?";
                }
            }

            if (tileentity instanceof TileEntitySign)
            {
                j = p_147343_1_.func_149588_c();
                int k = p_147343_1_.func_149586_d();
                i = p_147343_1_.func_149585_e();
                TileEntitySign tileentitysign1 = (TileEntitySign)tileentity;
                System.arraycopy(text, 0, tileentitysign1.signText, 0, 4);
                tileentitysign1.markDirty();
                worldserver.markBlockForUpdate(j, k, i);
            }
        }
    }

    // helper method
    public static String[] onSignEditEvent(NetHandlerPlayServer net, C12PacketUpdateSign data)
    {
        SignEditEvent e = new SignEditEvent(data.func_149588_c(), data.func_149586_d(), data.func_149585_e(), data.func_149589_f(), net.playerEntity);
        if (MinecraftForge.EVENT_BUS.post(e))
        {
            return null;
        }
        return e.text;

    }
    
    // patch method
    public static  void processVanilla250Packet(NetHandlerPlayServer net, C17PacketCustomPayload p_147349_1_)
    {
        PacketBuffer packetbuffer;
        ItemStack itemstack;
        ItemStack itemstack1;

        if ("MC|BEdit".equals(p_147349_1_.func_149559_c()))
        {
            packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(p_147349_1_.func_149558_e()));

            try
            {
                itemstack = packetbuffer.readItemStackFromBuffer();

                if (itemstack == null)
                {
                    return;
                }

                if (!ItemWritableBook.func_150930_a(itemstack.getTagCompound()))
                {
                    throw new IOException("Invalid book tag!");
                }

                itemstack1 = net.playerEntity.inventory.getCurrentItem();

                if (itemstack1 != null)
                {
                    if (itemstack.getItem() == Items.writable_book && itemstack.getItem() == itemstack1.getItem())
                    {
                        itemstack1.setTagInfo("pages", itemstack.getTagCompound().getTagList("pages", 8));
                    }

                    return;
                }
            }
            catch (Exception exception4)
            {
                NetHandlerPlayServer.logger.error("Couldn\'t handle book info", exception4);
                return;
            }
            finally
            {
                packetbuffer.release();
            }

            return;
        }
        else if ("MC|BSign".equals(p_147349_1_.func_149559_c()))
        {
            packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(p_147349_1_.func_149558_e()));

            try
            {
                itemstack = packetbuffer.readItemStackFromBuffer();

                if (itemstack != null)
                {
                    if (!ItemEditableBook.validBookTagContents(itemstack.getTagCompound()))
                    {
                        throw new IOException("Invalid book tag!");
                    }

                    itemstack1 = net.playerEntity.inventory.getCurrentItem();

                    if (itemstack1 == null)
                    {
                        return;
                    }

                    if (itemstack.getItem() == Items.written_book && itemstack1.getItem() == Items.writable_book)
                    {
                        itemstack1.setTagInfo("author", new NBTTagString(net.playerEntity.getCommandSenderName()));
                        itemstack1.setTagInfo("title", new NBTTagString(itemstack.getTagCompound().getString("title")));
                        itemstack1.setTagInfo("pages", itemstack.getTagCompound().getTagList("pages", 8));
                        itemstack1.func_150996_a(Items.written_book);
                    }

                    return;
                }
            }
            catch (Exception exception3)
            {
                NetHandlerPlayServer.logger.error("Couldn\'t sign book", exception3);
                return;
            }
            finally
            {
                packetbuffer.release();
            }

            return;
        }
        else
        {
            DataInputStream datainputstream;
            int i;

            if ("MC|TrSel".equals(p_147349_1_.func_149559_c()))
            {
                try
                {
                    datainputstream = new DataInputStream(new ByteArrayInputStream(p_147349_1_.func_149558_e()));
                    i = datainputstream.readInt();
                    Container container = net.playerEntity.openContainer;

                    if (container instanceof ContainerMerchant)
                    {
                        ((ContainerMerchant)container).setCurrentRecipeIndex(i);
                    }
                }
                catch (Exception exception2)
                {
                    NetHandlerPlayServer.logger.error("Couldn\'t select trade", exception2);
                }
            }
            else if ("MC|AdvCdm".equals(p_147349_1_.func_149559_c()))
            {
                if (!net.serverController.isCommandBlockEnabled())
                {
                    net.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notEnabled", new Object[0]));
                }
                else if (PermissionsManager.checkPermission(net.playerEntity, "mc.cmdblocks") && net.playerEntity.capabilities.isCreativeMode)
                {
                    packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(p_147349_1_.func_149558_e()));

                    try
                    {
                        byte b0 = packetbuffer.readByte();
                        CommandBlockLogic commandblocklogic = null;

                        if (b0 == 0)
                        {
                            TileEntity tileentity = net.playerEntity.worldObj.getTileEntity(packetbuffer.readInt(), packetbuffer.readInt(), packetbuffer.readInt());

                            if (tileentity instanceof TileEntityCommandBlock)
                            {
                                commandblocklogic = ((TileEntityCommandBlock)tileentity).func_145993_a();
                            }
                        }
                        else if (b0 == 1)
                        {
                            Entity entity = net.playerEntity.worldObj.getEntityByID(packetbuffer.readInt());

                            if (entity instanceof EntityMinecartCommandBlock)
                            {
                                commandblocklogic = ((EntityMinecartCommandBlock)entity).func_145822_e();
                            }
                        }

                        String s1 = packetbuffer.readStringFromBuffer(packetbuffer.readableBytes());

                        if (commandblocklogic != null)
                        {
                            commandblocklogic.func_145752_a(s1);
                            commandblocklogic.func_145756_e();
                            net.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.setCommand.success", new Object[] {s1}));
                        }
                    }
                    catch (Exception exception1)
                    {
                        NetHandlerPlayServer.logger.error("Couldn\'t set command block", exception1);
                    }
                    finally
                    {
                        packetbuffer.release();
                    }
                }
                else
                {
                    net.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notAllowed", new Object[0]));
                }
            }
            else if ("MC|Beacon".equals(p_147349_1_.func_149559_c()))
            {
                if (net.playerEntity.openContainer instanceof ContainerBeacon)
                {
                    try
                    {
                        datainputstream = new DataInputStream(new ByteArrayInputStream(p_147349_1_.func_149558_e()));
                        i = datainputstream.readInt();
                        int j = datainputstream.readInt();
                        ContainerBeacon containerbeacon = (ContainerBeacon)net.playerEntity.openContainer;
                        Slot slot = containerbeacon.getSlot(0);

                        if (slot.getHasStack())
                        {
                            slot.decrStackSize(1);
                            TileEntityBeacon tileentitybeacon = containerbeacon.func_148327_e();
                            tileentitybeacon.setPrimaryEffect(i);
                            tileentitybeacon.setSecondaryEffect(j);
                            tileentitybeacon.markDirty();
                        }
                    }
                    catch (Exception exception)
                    {
                        NetHandlerPlayServer.logger.error("Couldn\'t set beacon", exception);
                    }
                }
            }
            else if ("MC|ItemName".equals(p_147349_1_.func_149559_c()) && net.playerEntity.openContainer instanceof ContainerRepair)
            {
                ContainerRepair containerrepair = (ContainerRepair)net.playerEntity.openContainer;

                if (p_147349_1_.func_149558_e() != null && p_147349_1_.func_149558_e().length >= 1)
                {
                    String s = ChatAllowedCharacters.filerAllowedCharacters(new String(p_147349_1_.func_149558_e(), Charsets.UTF_8));

                    if (s.length() <= 30)
                    {
                        containerrepair.updateItemName(s);
                    }
                }
                else
                {
                    containerrepair.updateItemName("");
                }
            }
        }
    }
}
