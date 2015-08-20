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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemRedstone;
import net.minecraft.item.ItemSkull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fe.event.player.PlayerPostInteractEvent;

import org.hibernate.jpa.criteria.predicate.CompoundPredicate;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.playerlogger.entity.Action;
import com.forgeessentials.playerlogger.entity.ActionBlock;
import com.forgeessentials.playerlogger.entity.ActionCommand;
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
import com.forgeessentials.playerlogger.event.LogEventPostInteract;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.base.Charsets;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
                    LoggingHandler.felog.error("[PL] Playerlogger database closed. Trying to reconnect...");
                    try
                    {
                        em = entityManagerFactory.createEntityManager();
                    }
                    catch (IllegalStateException e)
                    {
                        LoggingHandler.felog.error("[PL] ------------------------------------------------------------------------");
                        LoggingHandler.felog.error("[PL] Fatal error! Database connection was lost and could not be reestablished");
                        LoggingHandler.felog.error("[PL] Stopping playerlogger!");
                        LoggingHandler.felog.error("[PL] ------------------------------------------------------------------------");
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
                    LoggingHandler.felog.error("[PL] Exception while persisting playerlogger entries");
                    e1.printStackTrace();
                    try
                    {
                        em.getTransaction().rollback();
                    }
                    catch (Exception e2)
                    {
                        LoggingHandler.felog.error("[PL] Exception while rolling back changes!");
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
            LoggingHandler.felog.error(ex.toString());
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

    protected CompoundPredicate getActionPredicate(Root<? extends Action> root, WorldArea area, Date startTime, Date endTime)
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CompoundPredicate predicate = (CompoundPredicate) cb.and();
        if (area != null)
        {
            predicate.getExpressions().add(cb.equal(root.<Integer> get(Action_.world.getName()), cb.literal(area.getDimension())));
            Point lp = area.getLowPoint();
            Point hp = area.getHighPoint();
            predicate.getExpressions().add(cb.between(root.get(Action_.x), cb.literal(lp.getX()), cb.literal(hp.getX())));
            predicate.getExpressions().add(cb.between(root.get(Action_.y), cb.literal(lp.getY()), cb.literal(hp.getY())));
            predicate.getExpressions().add(cb.between(root.get(Action_.z), cb.literal(lp.getZ()), cb.literal(hp.getZ())));
        }
        if (startTime != null)
            predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get(Action_.time), cb.literal(startTime)));
        if (endTime != null)
            predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get(Action_.time), cb.literal(endTime)));
        return predicate;
    }

    protected CompoundPredicate getActionPredicate(Root<? extends Action> root, WorldPoint point, Date startTime, Date endTime)
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CompoundPredicate predicate = (CompoundPredicate) cb.and();
        if (point != null)
        {
            predicate.getExpressions().add(cb.equal(root.<Integer> get(Action_.world.getName()), cb.literal(point.getDimension())));
            predicate.getExpressions().add(cb.equal(root.get(Action_.x), cb.literal(point.getX())));
            predicate.getExpressions().add(cb.equal(root.get(Action_.y), cb.literal(point.getY())));
            predicate.getExpressions().add(cb.equal(root.get(Action_.z), cb.literal(point.getZ())));
        }
        if (startTime != null)
            predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get(Action_.time), cb.literal(startTime)));
        if (endTime != null)
            predicate.getExpressions().add(cb.lessThan(root.get(Action_.time), cb.literal(endTime)));
        return predicate;
    }

    public List<ActionBlock> getLoggedBlockChanges(WorldArea area, Date startTime, Date endTime, int maxResults)
    {
        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ActionBlock> cQuery = cBuilder.createQuery(ActionBlock.class);
        Root<ActionBlock> cRoot = cQuery.from(ActionBlock.class);
        cQuery.select(cRoot);
        cQuery.where(getActionPredicate(cRoot, area, startTime, endTime));
        cQuery.orderBy(cBuilder.desc(cRoot.get(Action_.time)));
        TypedQuery<ActionBlock> query = em.createQuery(cQuery);
        if (maxResults > 0)
            query.setMaxResults(maxResults);
        return executeQuery(query);
    }

    public List<ActionBlock> getLoggedBlockChanges(WorldPoint point, Date startTime, Date endTime, int maxResults)
    {
        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ActionBlock> cQuery = cBuilder.createQuery(ActionBlock.class);
        Root<ActionBlock> cRoot = cQuery.from(ActionBlock.class);
        cQuery.select(cRoot);
        cQuery.where(getActionPredicate(cRoot, point, startTime, endTime));
        cQuery.orderBy(cBuilder.desc(cRoot.get(Action_.time)));
        TypedQuery<ActionBlock> query = em.createQuery(cQuery);
        if (maxResults > 0)
            query.setMaxResults(maxResults);
        return executeQuery(query);
    }

    public List<ActionCommand> getLoggedCommands(WorldArea area, Date startTime, Date endTime, int maxResults)
    {
        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ActionCommand> cQuery = cBuilder.createQuery(ActionCommand.class);
        Root<ActionCommand> cRoot = cQuery.from(ActionCommand.class);
        cQuery.select(cRoot);
        cQuery.orderBy(cBuilder.desc(cRoot.get(Action_.time)));
        cQuery.where(getActionPredicate(cRoot, area, startTime, endTime));
        TypedQuery<ActionCommand> query = em.createQuery(cQuery);
        if (maxResults > 0)
            query.setMaxResults(maxResults);
        return executeQuery(query);
    }

    public List<ActionCommand> getLoggedCommands(WorldPoint point, Date startTime, Date endTime, int maxResults)
    {
        CriteriaBuilder cBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ActionCommand> cQuery = cBuilder.createQuery(ActionCommand.class);
        Root<ActionCommand> cRoot = cQuery.from(ActionCommand.class);
        cQuery.select(cRoot);
        cQuery.orderBy(cBuilder.desc(cRoot.get(Action_.time)));
        cQuery.where(getActionPredicate(cRoot, point, startTime, endTime));
        TypedQuery<ActionCommand> query = em.createQuery(cQuery);
        if (maxResults > 0)
            query.setMaxResults(maxResults);
        return executeQuery(query);
    }

    /* ------------------------------------------------------------ */
    /* World events */

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public synchronized void worldLoad(WorldEvent.Load event)
    {
        WorldData world = em.find(WorldData.class, event.world.provider.dimensionId);
        if (world == null)
        {
            em.getTransaction().begin();
            world = new WorldData();
            world.id = event.world.provider.dimensionId;
            world.name = event.world.provider.getDimensionName();
            em.persist(world);
            em.getTransaction().commit();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void placeEvent(BlockEvent.PlaceEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER || em == null)
            return;
        if (event instanceof BlockEvent.MultiPlaceEvent)
        {
            // Get only last state of all changes
            Map<Point, BlockSnapshot> changes = new HashMap<>();
            for (BlockSnapshot snapshot : ((BlockEvent.MultiPlaceEvent) event).getReplacedBlockSnapshots())
                changes.put(new Point(snapshot.x, snapshot.y, snapshot.z), snapshot);
            for (BlockSnapshot snapshot : changes.values())
                eventQueue.add(new LogEventPlace(new BlockEvent.PlaceEvent(snapshot, null, event.player)));
            startThread();
        }
        else
        {
            logEvent(new LogEventPlace(event));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void breakEvent(BlockEvent.BreakEvent event)
    {
        logEvent(new LogEventBreak(event));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void explosionEvent(ExplosionEvent.Detonate event)
    {
        logEvent(new LogEventExplosion(event));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT || (event.useBlock == Result.DENY && event.useItem == Result.DENY))
            return;
        GameType gameType = ((EntityPlayerMP) event.entityPlayer).theItemInWorldManager.getGameType();
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && gameType != GameType.CREATIVE)
        {
            logEvent(new LogEventInteract(event));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerPostInteractEvent(PlayerPostInteractEvent event)
    {
        if (event.stack != null)
        {
            Item item = event.stack.getItem();
            if (item instanceof ItemBlock || item instanceof ItemRedstone || item instanceof ItemBed || item instanceof ItemDoor || item instanceof ItemSkull)
                return;
        }
        logEvent(new LogEventPostInteract(event));
    }

    /* ------------------------------------------------------------ */
    /* Other events */

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void commandEvent(CommandEvent event)
    {
        logEvent(new LogEventCommand(event));
    }

    /* ------------------------------------------------------------ */
    /* Player events */

    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event)
    // {
    // // TODO
    // }
    //
    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event)
    // {
    // // TODO
    // }
    //
    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public void playerRespawnEvent(PlayerEvent.PlayerRespawnEvent event)
    // {
    // // TODO
    // }
    //
    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public void playerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event)
    // {
    // // TODO
    // }
    //
    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public void playerChangedZoneEvent(PlayerChangedZone event)
    // {
    // // TODO
    // }
    //
    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public void playerOpenContainerEvent(PlayerOpenContainerEvent event)
    // {
    // // TODO
    // }
    //
    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public void itemPickupEvent(EntityItemPickupEvent event)
    // {
    // // TODO
    // }
    //
    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public void dropItemEvent(StartTracking event)
    // {
    // // TODO
    // }

    /* ------------------------------------------------------------ */
    /* Interact events */

    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public void attackEntityEvent(AttackEntityEvent e)
    // {
    // // TODO
    // }
    //
    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public void livingHurtEvent(LivingHurtEvent e)
    // {
    // // TODO
    // }
    //
    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public void entityInteractEvent(EntityInteractEvent e)
    // {
    // // TODO
    // }

    /* ------------------------------------------------------------ */
    /* Spawn events */

    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public void checkSpawnEvent(CheckSpawn e)
    // {
    // // TODO
    // }
    //
    // @SubscribeEvent(priority = EventPriority.LOWEST)
    // public void specialSpawnEvent(SpecialSpawn e)
    // {
    // // TODO
    // }

}
