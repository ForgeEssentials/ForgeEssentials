package com.forgeessentials.playerlogger;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import javax.sql.rowset.serial.SerialBlob;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.playerlogger.entity.ActionBlock;
import com.forgeessentials.playerlogger.entity.ActionBlock.ActionBlockType;
import com.forgeessentials.playerlogger.entity.ActionCommand;
import com.forgeessentials.playerlogger.entity.Action_;
import com.forgeessentials.playerlogger.entity.BlockData;
import com.forgeessentials.playerlogger.entity.BlockData_;
import com.forgeessentials.playerlogger.entity.PlayerData;
import com.forgeessentials.playerlogger.entity.PlayerData_;
import com.forgeessentials.playerlogger.entity.WorldData;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.PlayerChangedZone;
import com.forgeessentials.util.events.ServerEventHandler;
import com.google.common.base.Charsets;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.registry.GameData;

public class PlayerLogger extends ServerEventHandler {

    private EntityManagerFactory entityManagerFactory;

    private EntityManager em;

    private int transactionIndex;

    private Map<String, BlockData> blockCache = new HashMap<>();

    private Map<UUID, PlayerData> playerCache = new HashMap<>();

    /**
     * Closes any existing database connection and frees resources
     */
    public void close()
    {
        transactionIndex = 0;
        playerCache.clear();
        blockCache.clear();

        if (em != null && em.isOpen())
            em.close();
        if (entityManagerFactory != null && entityManagerFactory.isOpen())
            entityManagerFactory.close();
    }

    /**
     * 
     */
    public void loadDatabase()
    {
        close();

        Properties properties = new Properties();
        switch (PlayerLoggerConfig.databaseType)
        {
        case "h2":
            properties.setProperty("hibernate.connection.url", "jdbc:h2:" + PlayerLoggerConfig.databaseUrl);
            break;
        case "mysql":
            properties.setProperty("hibernate.connection.url", "jdbc:mysql://" + PlayerLoggerConfig.databaseUrl); // e.g.: jdbc:mysql://localhost:3306/forgeessentials
            break;
        default:
            throw new RuntimeException("PlayerLogger database type must be either h2 or mysql.");
        }
        properties.setProperty("hibernate.connection.username", PlayerLoggerConfig.databaseUsername);
        properties.setProperty("hibernate.connection.password", PlayerLoggerConfig.databasePassword);
        //properties.setProperty("hibernate.hbm2ddl.auto", "update");
        //properties.setProperty("hibernate.format_sql", "false");
        //properties.setProperty("hibernate.show_sql", "true");

        entityManagerFactory = Persistence.createEntityManagerFactory("playerlogger_" + PlayerLoggerConfig.databaseType, properties);
        //entityManagerFactory = Persistence.createEntityManagerFactory("playerlogger_eclipselink_" + PlayerLoggerConfig.databaseType, properties);
        em = entityManagerFactory.createEntityManager();
    }

    // ============================================================
    // Utilities

    protected <T> TypedQuery<T> buildSimpleQuery(Class<T> clazz, String fieldName, Object value)
    {
        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> cQuery = cBuilder.createQuery(clazz);
        Root<T> cRoot = cQuery.from(clazz);
        cQuery.select(cRoot).where(cBuilder.equal(cRoot.get(fieldName), value));
        return em.createQuery(cQuery);
    }

    protected <T,V> TypedQuery<T> buildSimpleQuery(Class<T> clazz, SingularAttribute<T, V> field, V value)
    {
        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> cQuery = cBuilder.createQuery(clazz);
        Root<T> cRoot = cQuery.from(clazz);
        cQuery.select(cRoot).where(cBuilder.equal(cRoot.get(field), value));
        return em.createQuery(cQuery);
    }

    protected <T> T getOneOrNullResult(TypedQuery<T> query)
    {
        List<T> results = query.getResultList();
        if (results.size() == 1)
            return results.get(0);
        if (results.isEmpty())
            return null;
        throw new NonUniqueResultException();
    }

    protected void beginTransaction()
    {
        if (transactionIndex <= 0)
        {
            em.getTransaction().begin();
            transactionIndex = 0;
        }
        transactionIndex++;
    }

    protected void commitTransaction()
    {
        transactionIndex--;
        if (transactionIndex == 0)
            em.getTransaction().commit();
    }

    protected void rollbackTransaction()
    {
        em.getTransaction().rollback();
        transactionIndex = 0;
    }

    protected PlayerData getPlayer(String uuid)
    {
        beginTransaction();
        PlayerData data = getOneOrNullResult(buildSimpleQuery(PlayerData.class, PlayerData_.uuid, uuid));
        if (data == null)
        {
            data = new PlayerData();
            data.uuid = uuid;
            em.persist(data);
        }
        commitTransaction();
        return data;
    }
    
    protected PlayerData getPlayer(UUID uuid)
    {
        PlayerData data = playerCache.get(uuid);
        if (data != null)
            return data;
        data = getPlayer(uuid.toString());
        playerCache.put(uuid, data);
        return data;
    }

    protected BlockData getBlock(String name)
    {
        BlockData data = blockCache.get(name);
        if (data != null)
            return data;

        beginTransaction();
        data = getOneOrNullResult(buildSimpleQuery(BlockData.class, BlockData_.name, name));
        if (data == null)
        {
            data = new BlockData();
            data.name = name;
            em.persist(data);
        }
        commitTransaction();
        blockCache.put(name, data);
        return data;
    }

    protected BlockData getBlock(Block block)
    {
        return getBlock(GameData.getBlockRegistry().getNameForObject(block));
    }

    protected SerialBlob getTileEntityBlob(TileEntity entity) {
        if (entity == null)
        	return null;
        NBTTagCompound nbt = new NBTTagCompound();
        entity.writeToNBT(nbt);
        nbt.setString("ENTITY_CLASS", entity.getClass().getName());
        try
        {
            return new SerialBlob(nbt.toString().getBytes(Charsets.UTF_8));
        }
        catch (Exception ex)
        {
            OutputHandler.felog.severe(ex.toString());
            ex.printStackTrace();
        }
    	return null;
	}

    // ============================================================
    // Rollback

    public List<ActionBlock> getBlockChangeSet(Selection area, Date startTime)
    {
        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ActionBlock> cQuery = cBuilder.createQuery(ActionBlock.class);
        Root<ActionBlock> cRoot = cQuery.from(ActionBlock.class);
        cQuery.select(cRoot);
        cQuery.where(cBuilder.and(
                cBuilder.greaterThanOrEqualTo(cRoot.get(Action_.time), cBuilder.literal(startTime)),
                cBuilder.equal(cRoot.<Integer>get(Action_.world.getName()), cBuilder.literal(area.getDimension())),
                cBuilder.between(cRoot.get(Action_.x), cBuilder.literal(area.getLowPoint().x), cBuilder.literal(area.getHighPoint().x)),
                cBuilder.between(cRoot.get(Action_.y), cBuilder.literal(area.getLowPoint().y), cBuilder.literal(area.getHighPoint().y)),
                cBuilder.between(cRoot.get(Action_.z), cBuilder.literal(area.getLowPoint().z), cBuilder.literal(area.getHighPoint().z))
        ));
        cQuery.orderBy(cBuilder.desc(cRoot.get(Action_.time)));
        TypedQuery<ActionBlock> query = em.createQuery(cQuery);

        beginTransaction();
        List<ActionBlock> changes = query.getResultList();
        commitTransaction();
        
        return changes;
    }

    // ============================================================
    // Block events

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void worldLoad(WorldEvent.Load e)
    {
        beginTransaction();
        WorldData world = em.find(WorldData.class, e.world.provider.dimensionId);
        if (world == null)
        {
            world = new WorldData();
            world.id = e.world.provider.dimensionId;
            world.name = e.world.provider.getDimensionName();
            em.persist(world);
        }
        commitTransaction();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void placeEvent(BlockEvent.PlaceEvent e)
    {
        beginTransaction();
        ActionBlock action = new ActionBlock();
        action.time = new Date();
        action.player = getPlayer(e.player.getPersistentID());
        action.world = em.getReference(WorldData.class, e.world.provider.dimensionId);
        action.block = getBlock(e.block);
        action.metadata = e.blockMetadata;
        action.type = ActionBlockType.PLACE;
        action.x = e.x;
        action.y = e.y;
        action.z = e.z;
        em.persist(action);
        commitTransaction();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void multiPlaceEvent(BlockEvent.MultiPlaceEvent e)
    {
    	/*
        beginTransaction();
        ActionBlock action = new ActionBlock();
        action.time = new Date();
        action.player = getPlayer(e.player.getPersistentID());
        action.world = em.getReference(WorldData.class, e.world.provider.dimensionId);
        action.block = getBlock(e.block);
        action.metadata = e.blockMetadata;
        action.type = ActionBlockType.PLACE;
        action.x = e.x;
        action.y = e.y;
        action.z = e.z;
        em.persist(action);
        commitTransaction();
        */
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void breakEvent(BreakEvent e)
    {
        beginTransaction();
        ActionBlock action = new ActionBlock();
        action.time = new Date();
        action.player = getPlayer(e.getPlayer().getPersistentID());
        action.world = em.getReference(WorldData.class, e.world.provider.dimensionId);
        action.block = getBlock(e.block);
        action.metadata = e.blockMetadata;
        action.entity = getTileEntityBlob(e.world.getTileEntity(e.x, e.y, e.z));
        action.type = ActionBlockType.BREAK;
        action.x = e.x;
        action.y = e.y;
        action.z = e.z;
        em.persist(action);
        commitTransaction();
    }

    @SuppressWarnings("unchecked")
	@SubscribeEvent(priority = EventPriority.LOWEST)
    public void explosionEvent(ExplosionEvent.Detonate e)
    {
        beginTransaction();
        WorldData worldData = em.getReference(WorldData.class, e.world.provider.dimensionId);
        for (ChunkPosition blockPos : (List<ChunkPosition>) e.explosion.affectedBlockPositions)
        {
	        ActionBlock action = new ActionBlock();
	        action.time = new Date();
	        action.world = worldData;
	        action.block = getBlock(e.world.getBlock(blockPos.chunkPosX, blockPos.chunkPosY, blockPos.chunkPosZ));
	        action.metadata = e.world.getBlockMetadata(blockPos.chunkPosX, blockPos.chunkPosY, blockPos.chunkPosZ);
	        action.entity = getTileEntityBlob(e.world.getTileEntity(blockPos.chunkPosX, blockPos.chunkPosY, blockPos.chunkPosZ));
	        action.type = ActionBlockType.DETONATE;
	        action.x = blockPos.chunkPosX;
	        action.y = blockPos.chunkPosY;
	        action.z = blockPos.chunkPosZ;
	        em.persist(action);
        }
        commitTransaction();
    }

    // ============================================================
    // Other events

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void commandEvent(CommandEvent e)
	{
        beginTransaction();
        ActionCommand action = new ActionCommand();
        action.time = new Date();
        action.command = e.command.getCommandName();
        if (e.sender instanceof EntityPlayer)
        {
        	EntityPlayer player = ((EntityPlayer) e.sender);
            action.player = getPlayer(player.getPersistentID());
            action.world = em.getReference(WorldData.class, player.worldObj.provider.dimensionId);
            action.x = (int) player.posX;
            action.y = (int) player.posY;
            action.z = (int) player.posZ;
        }
        else if (e.sender instanceof TileEntityCommandBlock)
        {
        	TileEntityCommandBlock block = ((TileEntityCommandBlock) e.sender);
            action.player = getPlayer("commandblock");
            action.world = em.getReference(WorldData.class, block.getWorldObj().provider.dimensionId);
            action.x = block.xCoord;
            action.y = block.yCoord;
            action.z = block.zCoord;
        }
        em.persist(action);
        commitTransaction();
	}

    // ============================================================
    // Player events

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent e)
	{
        // TODO
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent e)
	{
        // TODO
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void playerRespawnEvent(PlayerEvent.PlayerRespawnEvent e)
	{
        // TODO
	}

    @SubscribeEvent(priority = EventPriority.LOWEST)
	public void playerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent e)
	{
        // TODO
	}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerChangedZoneEvent(PlayerChangedZone e)
    {
        // TODO
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerOpenContainerEvent(PlayerOpenContainerEvent e)
    {
        // TODO
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void itemPickupEvent(EntityItemPickupEvent e)
    {
        // TODO
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void dropItemEvent(StartTracking e)
    {
        // TODO
    }

    // ============================================================
    // Interact events

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void attackEntityEvent(AttackEntityEvent e)
    {
        // TODO
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void livingHurtEvent(LivingHurtEvent e)
    {
        // TODO
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerInteractEvent(PlayerInteractEvent e)
    {
        // TODO
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void entityInteractEvent(EntityInteractEvent e)
    {
        // TODO
    }

    // ============================================================
    // Spawn events

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void checkSpawnEvent(CheckSpawn e)
    {
        // TODO
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void specialSpawnEvent(SpecialSpawn e)
    {
        // TODO
    }

}
