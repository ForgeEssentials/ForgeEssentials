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
import javax.sql.rowset.serial.SerialBlob;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.forgeessentials.playerlogger.entity.ActionBlock;
import com.forgeessentials.playerlogger.entity.ActionBlock.ActionBlockType;
import com.forgeessentials.playerlogger.entity.BlockData;
import com.forgeessentials.playerlogger.entity.PlayerData;
import com.forgeessentials.playerlogger.entity.WorldData;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.PlayerChangedZone;
import com.forgeessentials.util.events.ServerEventHandler;
import com.google.common.base.Charsets;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class PlayerLogger extends ServerEventHandler {

    private Session session;

    private SessionFactory sessionFactory;

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

        if (session != null && session.isOpen())
            session.close();
        if (sessionFactory != null && !sessionFactory.isClosed())
            sessionFactory.close();

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
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            properties.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
            properties.setProperty("hibernate.connection.url", "jdbc:h2:" + PlayerLoggerConfig.databaseUrl);
            break;
        case "mysql":
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
            properties.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
            properties.setProperty("hibernate.connection.url", "jdbc:mysql://" + PlayerLoggerConfig.databaseUrl);
            // properties.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/forgeessentials");
            break;
        default:
            throw new RuntimeException("PlayerLogger database type must be either h2 or mysql.");
        }
        properties.setProperty("hibernate.connection.username", PlayerLoggerConfig.databaseUsername);
        properties.setProperty("hibernate.connection.password", PlayerLoggerConfig.databasePassword);
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "false");

        entityManagerFactory = Persistence.createEntityManagerFactory("playerlogger_" + PlayerLoggerConfig.databaseType, properties);
        em = entityManagerFactory.createEntityManager();

        sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        session = em.unwrap(Session.class);

        // Configuration cfg = new Configuration();
        // cfg.setProperties(properties);
        // cfg.addPackage(Action.class.getPackage().getName());
        // cfg.addAnnotatedClass(PlayerData.class);
        // cfg.addAnnotatedClass(WorldData.class);
        // cfg.addAnnotatedClass(BlockData.class);
        // cfg.addAnnotatedClass(Action.class);
        // cfg.addAnnotatedClass(ActionBlock.class);
        // ServiceRegistry serviceRegistry = new
        // StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
        // sessionFactory = cfg.buildSessionFactory(serviceRegistry);
        // session = sessionFactory.openSession();
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

    protected PlayerData getPlayer(UUID uuid)
    {
        PlayerData data = playerCache.get(uuid);
        if (data != null)
            return data;

        beginTransaction();
        data = getOneOrNullResult(buildSimpleQuery(PlayerData.class, "uuid", uuid.toString()));
        if (data == null)
        {
            data = new PlayerData();
            data.uuid = uuid.toString();
            em.persist(data);
        }
        commitTransaction();
        playerCache.put(uuid, data);
        return data;
    }

    protected BlockData getBlock(String name)
    {
        BlockData data = blockCache.get(name);
        if (data != null)
            return data;

        beginTransaction();
        data = getOneOrNullResult(buildSimpleQuery(BlockData.class, "name", name));
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

    // ============================================================
    // Event listeners

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
        action.block = getBlock(e.block.getUnlocalizedName());
        action.type = ActionBlockType.PLACE;
        action.x = e.x;
        action.y = e.y;
        action.z = e.z;

        em.persist(action);
        commitTransaction();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void breakEvent(BreakEvent e)
    {
        beginTransaction();

        ActionBlock action = new ActionBlock();
        action.time = new Date();
        action.player = getPlayer(e.getPlayer().getPersistentID());
        action.world = em.getReference(WorldData.class, e.world.provider.dimensionId);
        action.block = getBlock(e.block.getUnlocalizedName());
        action.type = ActionBlockType.BREAK;
        action.x = e.x;
        action.y = e.y;
        action.z = e.z;

        TileEntity entity = e.world.getTileEntity(e.x, e.y, e.z);
        if (entity != null)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            entity.writeToNBT(nbt);
            nbt.setString("ENTITY_CLASS", entity.getClass().getName());
            try
            {
                action.entity = new SerialBlob(nbt.toString().getBytes(Charsets.UTF_8));
            }
            catch (Exception ex)
            {
                OutputHandler.felog.severe(e.toString());
                ex.printStackTrace();
            }
        }

        em.persist(action);
        commitTransaction();
    }

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
    public void explosionEvent(ExplosionEvent.Start e)
    {
        // TODO
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void multiPlaceEvent(BlockEvent.MultiPlaceEvent e)
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerOpenContainerEvent(PlayerOpenContainerEvent e)
    {
        // TODO
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoginEvent(PlayerLoggedInEvent e)
    {
        // TODO
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerChangedZoneEvent(PlayerChangedZone e)
    {
        // TODO
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent e)
    {
        // TODO
    }

}
