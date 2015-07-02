package com.forgeessentials.playerlogger;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.BlockSnapshot;
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
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.playerlogger.entity.ActionBlock;
import com.forgeessentials.playerlogger.entity.Action_;
import com.forgeessentials.playerlogger.entity.BlockData;
import com.forgeessentials.playerlogger.entity.BlockData_;
import com.forgeessentials.playerlogger.entity.PlayerData;
import com.forgeessentials.playerlogger.entity.PlayerData_;
import com.forgeessentials.playerlogger.entity.WorldData;
import com.forgeessentials.playerlogger.event.LogEventBreak;
import com.forgeessentials.playerlogger.event.LogEventCommand;
import com.forgeessentials.playerlogger.event.LogEventExplosion;
import com.forgeessentials.playerlogger.event.LogEventInteract;
import com.forgeessentials.playerlogger.event.LogEventPlace;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.PlayerChangedZone;
import com.forgeessentials.util.events.ServerEventHandler;
import com.google.common.base.Charsets;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;

public class PlayerLogger extends ServerEventHandler implements Runnable
{

    private Thread thread;

    private EntityManagerFactory entityManagerFactory;

    private EntityManager em;

    private Map<String, Long> blockCache = new HashMap<>();

    private Map<Block, Long> blockTypeCache = new HashMap<>();

    private Map<UUID, Long> playerCache = new HashMap<>();

    /* ------------------------------------------------------------ */

    private ConcurrentLinkedQueue<PlayerLoggerEvent<?>> eventQueue = new ConcurrentLinkedQueue<PlayerLoggerEvent<?>>();

    /* ------------------------------------------------------------ */

    /**
     * Closes any existing database connection and frees resources
     */
    protected void close()
    {
        blockCache.clear();
        blockTypeCache.clear();
        playerCache.clear();

        if (em != null && em.isOpen())
            em.close();
        if (entityManagerFactory != null && entityManagerFactory.isOpen())
            entityManagerFactory.close();
    }

    /**
     * Initialize the database connection
     */
    protected void loadDatabase()
    {
        close();

        // Set log level
        Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);

        Properties properties = new Properties();
        switch (PlayerLoggerConfig.databaseType)
        {
        case "h2":
            properties.setProperty("hibernate.connection.url", "jdbc:h2:" + PlayerLoggerConfig.databaseUrl);
            break;
        case "mysql":
            // e.g.: jdbc:mysql://localhost:3306/forgeessentials
            properties.setProperty("hibernate.connection.url", "jdbc:mysql://" + PlayerLoggerConfig.databaseUrl);
            break;
        default:
            throw new RuntimeException("PlayerLogger database type must be either h2 or mysql.");
        }
        properties.setProperty("hibernate.connection.username", PlayerLoggerConfig.databaseUsername);
        properties.setProperty("hibernate.connection.password", PlayerLoggerConfig.databasePassword);
        // properties.setProperty("hibernate.hbm2ddl.auto", "update");
        // properties.setProperty("hibernate.format_sql", "false");
        // properties.setProperty("hibernate.show_sql", "true");

        entityManagerFactory = Persistence.createEntityManagerFactory("playerlogger_" + PlayerLoggerConfig.databaseType, properties);
        // entityManagerFactory = Persistence.createEntityManagerFactory("playerlogger_eclipselink_" +
        // PlayerLoggerConfig.databaseType, properties);
        em = entityManagerFactory.createEntityManager();
    }

    @Override
    public void run()
    {
        while (!eventQueue.isEmpty())
        {
            synchronized (this)
            {
                if (em == null)
                    return;
                if (!em.isOpen())
                {
                    OutputHandler.felog.error("[PL] Playerlogger database closed. Trying to reconnect...");
                    try
                    {
                        em = entityManagerFactory.createEntityManager();
                    }
                    catch (IllegalStateException e)
                    {
                        OutputHandler.felog.error("[PL] ------------------------------------------------------------------------");
                        OutputHandler.felog.error("[PL] Fatal error! Database connection was lost and could not be reestablished");
                        OutputHandler.felog.error("[PL] Stopping playerlogger!");
                        OutputHandler.felog.error("[PL] ------------------------------------------------------------------------");
                        em = null;
                        eventQueue.clear();
                        return;
                    }
                }
                try
                {
                    em.getTransaction().begin();
                    int count;
                    for (count = 0; count < 1000; count++)
                    {
                        PlayerLoggerEvent<?> logEvent = eventQueue.poll();
                        if (logEvent == null)
                            break;
                        logEvent.process(em);
                    }
                    em.getTransaction().commit();
                    // System.out.println(String.format("%d: Wrote %d playerlogger entries", System.currentTimeMillis()
                    // % (1000 * 60), count));
                }
                catch (Exception e1)
                {
                    OutputHandler.felog.error("[PL] Exception while persisting playerlogger entries");
                    e1.printStackTrace();
                    try
                    {
                        em.getTransaction().rollback();
                    }
                    catch (Exception e2)
                    {
                        OutputHandler.felog.error("[PL] Exception while rolling back changes!");
                        e2.printStackTrace();
                        em.close();
                        return;
                    }
                }
                finally
                {
                    em.clear();
                }
            }
        }
    }

    protected void startThread()
    {
        if (thread != null && thread.isAlive())
            return;
        thread = new Thread(this, "Playerlogger");
        thread.start();
    }

    // ============================================================
    // Utilities

    /**
     * <b>NEVER</b> call this and do write operations with this entity manager unless you do it in a synchronized block
     * with this object.
     * 
     * <pre>
     * <code>synchronized (playerLogger) {
     *      playerLogger.getEntityManager().doShit();
     * }</code>
     * </pre>
     * 
     * @return entity manager
     */
    public EntityManager getEntityManager()
    {
        return em;
    }

    public <T> TypedQuery<T> buildSimpleQuery(Class<T> clazz, String fieldName, Object value)
    {
        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> cQuery = cBuilder.createQuery(clazz);
        Root<T> cRoot = cQuery.from(clazz);
        cQuery.select(cRoot).where(cBuilder.equal(cRoot.get(fieldName), value));
        return em.createQuery(cQuery);
    }

    public <T, V> TypedQuery<T> buildSimpleQuery(Class<T> clazz, SingularAttribute<T, V> field, V value)
    {
        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> cQuery = cBuilder.createQuery(clazz);
        Root<T> cRoot = cQuery.from(clazz);
        cQuery.select(cRoot).where(cBuilder.equal(cRoot.get(field), value));
        return em.createQuery(cQuery);
    }

    public <T, V> TypedQuery<Long> buildCountQuery(Class<T> clazz, SingularAttribute<T, V> field, V value)
    {
        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cBuilder.createQuery(Long.class);
        Root<T> cRoot = cQuery.from(clazz);
        cQuery.select(cBuilder.count(cRoot));
        if (field != null)
            cQuery.where(cBuilder.equal(cRoot.get(field), value));
        return em.createQuery(cQuery);
    }

    public <T> T getOneOrNullResult(TypedQuery<T> query)
    {
        List<T> results = query.getResultList();
        if (results.size() == 1)
            return results.get(0);
        if (results.isEmpty())
            return null;
        throw new NonUniqueResultException();
    }

    protected void logEvent(PlayerLoggerEvent<?> event)
    {
        if (em == null)
            return;
        eventQueue.add(event);
        startThread();
    }

    protected synchronized WorldData getWorld(int dimensionId)
    {
        return em.getReference(WorldData.class, dimensionId);
    }

    protected synchronized PlayerData getPlayer(String uuid)
    {
        PlayerData data = getOneOrNullResult(buildSimpleQuery(PlayerData.class, PlayerData_.uuid, uuid));
        if (data == null)
        {
            data = new PlayerData();
            data.uuid = uuid;
            em.persist(data);
        }
        return data;
    }

    protected synchronized PlayerData getPlayer(UUID uuid)
    {
        Long id = playerCache.get(uuid);
        if (id != null)
            return em.getReference(PlayerData.class, id);
        PlayerData data = getPlayer(uuid.toString());
        playerCache.put(uuid, data.id);
        return data;
    }

    protected synchronized BlockData getBlock(String name)
    {
        Long id = blockCache.get(name);
        if (id != null)
            return em.getReference(BlockData.class, id);

        BlockData data = getOneOrNullResult(buildSimpleQuery(BlockData.class, BlockData_.name, name));
        if (data == null)
        {
            data = new BlockData();
            data.name = name;
            em.persist(data);
        }
        blockCache.put(name, data.id);
        return data;
    }

    protected synchronized BlockData getBlock(Block block)
    {
        Long id = blockTypeCache.get(block);
        if (id != null)
            return em.getReference(BlockData.class, id);
        BlockData data = getBlock(GameData.getBlockRegistry().getNameForObject(block));
        blockTypeCache.put(block, data.id);
        return data;
    }

    protected SerialBlob getTileEntityBlob(TileEntity tileEntity)
    {
        if (tileEntity == null)
            return null;
        NBTTagCompound nbt = new NBTTagCompound();
        tileEntity.writeToNBT(nbt);
        nbt.setString("ENTITY_CLASS", tileEntity.getClass().getName());
        try
        {
            return new SerialBlob(nbt.toString().getBytes(Charsets.UTF_8));
        }
        catch (Exception ex)
        {
            OutputHandler.felog.error(ex.toString());
            ex.printStackTrace();
        }
        return null;
    }

    /* ------------------------------------------------------------ */
    /* data retrieval */

    private synchronized <T> List<T> executeQuery(TypedQuery<T> query)
    {
        em.getTransaction().begin();
        List<T> changes = query.getResultList();
        em.getTransaction().commit();
        return changes;
    }

    public List<ActionBlock> getBlockChanges(Selection area, Date startTime)
    {
        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ActionBlock> cQuery = cBuilder.createQuery(ActionBlock.class);
        Root<ActionBlock> cRoot = cQuery.from(ActionBlock.class);
        cQuery.select(cRoot);
        cQuery.where(cBuilder.and(cBuilder.greaterThanOrEqualTo(cRoot.get(Action_.time), cBuilder.literal(startTime)),
                cBuilder.equal(cRoot.<Integer> get(Action_.world.getName()), cBuilder.literal(area.getDimension())), //
                cBuilder.between(cRoot.get(Action_.x), cBuilder.literal(area.getLowPoint().getX()), cBuilder.literal(area.getHighPoint().getX())), //
                cBuilder.between(cRoot.get(Action_.y), cBuilder.literal(area.getLowPoint().getY()), cBuilder.literal(area.getHighPoint().getY())), //
                cBuilder.between(cRoot.get(Action_.z), cBuilder.literal(area.getLowPoint().getZ()), cBuilder.literal(area.getHighPoint().getZ()))));
        cQuery.orderBy(cBuilder.desc(cRoot.get(Action_.time)));
        return executeQuery(em.createQuery(cQuery));
    }

    public List<ActionBlock> getBlockChanges(WorldPoint point, Date startTime, int maxResults)
    {
        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ActionBlock> cQuery = cBuilder.createQuery(ActionBlock.class);
        Root<ActionBlock> cRoot = cQuery.from(ActionBlock.class);
        cQuery.select(cRoot);
        cQuery.where(cBuilder.and(cBuilder.equal(cRoot.<Integer> get(Action_.world.getName()), cBuilder.literal(point.getDimension())), //
                cBuilder.equal(cRoot.get(Action_.x), cBuilder.literal(point.getX())), //
                cBuilder.equal(cRoot.get(Action_.y), cBuilder.literal(point.getY())), //
                cBuilder.equal(cRoot.get(Action_.z), cBuilder.literal(point.getZ())), //
                cBuilder.lessThan(cRoot.get(Action_.time), cBuilder.literal(startTime))));
        cQuery.orderBy(cBuilder.desc(cRoot.get(Action_.time)));
        return executeQuery(em.createQuery(cQuery).setMaxResults(maxResults));
    }

    /* ------------------------------------------------------------ */
    /* World events */

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public synchronized void worldLoad(WorldEvent.Load e)
    {
        WorldData world = em.find(WorldData.class, e.world.provider.dimensionId);
        if (world == null)
        {
            em.getTransaction().begin();
            world = new WorldData();
            world.id = e.world.provider.dimensionId;
            world.name = e.world.provider.getDimensionName();
            em.persist(world);
            em.getTransaction().commit();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void placeEvent(BlockEvent.PlaceEvent event)
    {
        logEvent(new LogEventPlace(event));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void multiPlaceEvent(BlockEvent.MultiPlaceEvent e)
    {
        if (em == null)
            return;
        for (BlockSnapshot snapshot : e.getReplacedBlockSnapshots())
            eventQueue.add(new LogEventPlace(new BlockEvent.PlaceEvent(snapshot, null, e.player)));
        startThread();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void breakEvent(BlockEvent.BreakEvent e)
    {
        logEvent(new LogEventBreak(e));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void explosionEvent(ExplosionEvent.Detonate e)
    {
        logEvent(new LogEventExplosion(e));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT || (event.useBlock == Result.DENY && event.useItem == Result.DENY))
            return;
        ItemStack itemStack = event.entityPlayer.getCurrentEquippedItem();
        if (event.useBlock != Result.ALLOW && itemStack != null && itemStack.getItem() instanceof ItemBlock)
            return;
        logEvent(new LogEventInteract(event));
    }

    /* ------------------------------------------------------------ */
    /* Other events */

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void commandEvent(CommandEvent e)
    {
        logEvent(new LogEventCommand(e));
    }

    /* ------------------------------------------------------------ */
    /* Player events */

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

    /* ------------------------------------------------------------ */
    /* Interact events */

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
    public void entityInteractEvent(EntityInteractEvent e)
    {
        // TODO
    }

    /* ------------------------------------------------------------ */
    /* Spawn events */

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
