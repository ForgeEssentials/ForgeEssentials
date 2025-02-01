package com.forgeessentials.mapper;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraftforge.fml.common.registry.GameData;

import com.forgeessentials.core.preloader.api.ServerBlock;
import com.forgeessentials.util.output.LoggingHandler;

public final class MapperUtil
{

    public static final int CHUNK_BLOCKS = 1 << 4;
    public static final int REGION_CHUNKS = 1 << 5;
    public static final int REGION_CHUNK_COUNT = REGION_CHUNKS * REGION_CHUNKS;
    public static final int REGION_BLOCKS = CHUNK_BLOCKS << 5;

    public static Map<Block, Integer[]> colorMap = new HashMap<Block, Integer[]>();

    public static Color[][] colors = new Color[4096][];
    public static Color[][][] datacolors = new Color[4096][][];
    public static Color[][] biomecolors = new Color[BiomeMap.values().length][];
    public static Color[][] raincolors = new Color[64][];
    public static Color[][] tempcolors = new Color[64][];

    /* ------------------------------------------------------------ */

    public static void renderChunk(BufferedImage image, int offsetX, int offsetY, Chunk chunk)
    {
        for (int ix = 0; ix < 16; ix++)
        {
            for (int iz = 0; iz < 16; iz++)
            {
                int iy = chunk.getHeightValue(ix, iz);
                for (; iy >= 0; iy--)
                {
                	Block block = chunk.getBlock(new BlockPos(ix, 0, iz));
                    if (block == Blocks.air)
                        continue;
                    image.setRGB(offsetX + ix, offsetY + iz, getBlockColor(block, block.getMetaFromState(chunk.getBlockState(new BlockPos(ix, iy, iz)))).getRGB());
                    break;
                }
                if (iy < 0)
                {
                    /* error */
                }
            }
        }
    }

    public static BufferedImage renderChunk(Chunk chunk)
    {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_3BYTE_BGR);
        renderChunk(image, 0, 0, chunk);
        return image;
    }

    public static BufferedImage renderRegion(WorldServer world, int regionX, int regionZ)
    {
        int chunkStartX = regionX * MapperUtil.REGION_CHUNKS;
        int chunkStartZ = regionZ * MapperUtil.REGION_CHUNKS;
        // File regionFile = new File(file2, "r." + regionX + "." + regionZ + ".mca");
        BufferedImage image = new BufferedImage(MapperUtil.REGION_BLOCKS, MapperUtil.REGION_BLOCKS, BufferedImage.TYPE_3BYTE_BGR);
        for (int rx = 0; rx < MapperUtil.REGION_CHUNKS; rx++)
        {
            for (int rz = 0; rz < MapperUtil.REGION_CHUNKS; rz++)
            {
                int cx = chunkStartX + rx;
                int cz = chunkStartZ + rz;
                if (!MapperUtil.chunkExists(world, cx, cz))
                    continue;
                Chunk chunk = loadChunk(world, cx, cz);
                MapperUtil.renderChunk(image, rx * MapperUtil.CHUNK_BLOCKS, rz * MapperUtil.CHUNK_BLOCKS, chunk);
            }
        }
        return image;
    }

    public static Chunk loadChunk(WorldServer world, int cx, int cz)
    {
        Chunk chunk = (Chunk) world.theChunkProviderServer.id2ChunkMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(cx, cz));
        if (chunk != null)
            return chunk;
        try
        {
            AnvilChunkLoader loader = (AnvilChunkLoader) world.theChunkProviderServer.chunkLoader;
            Object[] data = loader.loadChunk__Async(world, cx, cz);
            return (Chunk) data[0];
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public static boolean chunkExists(WorldServer world, int cx, int cz)
    {
        return RegionFileCache.createOrLoadRegionFile(world.getChunkSaveLocation(), cx, cz).chunkExists(cx & 0x1F, cz & 0x1F);
    }

    /* ------------------------------------------------------------ */

    public static Color getBlockColor(Block block, int meta)
    {
        int id = GameData.getBlockRegistry().getId(block);
        if (id >= colors.length)
            return Color.BLACK;

        if (datacolors[id] != null && meta < datacolors[id].length && datacolors[id][meta] != null)
            return datacolors[id][meta][0];
        if (colors[id] != null && colors[id][0] != null)
            return colors[id][0];

        // IIcon icon = block.getIcon(ForgeDirection.UP.ordinal(), chunk.getBlockMetadata(ix, iy, iz));
        // if (icon == null) continue;

        Color color;
        BufferedImage image = getBlockImage(block);
        if (image != null)
            color = getAverageColor(image);
        else if (colors[id] != null && colors[id].length > 0 && colors[id][0] != null)
            color = colors[id][0];
        else
            color = Color.BLACK;

        if (id >= colors.length)
        {
            Color[][] newColors = new Color[id + 1][];
            System.arraycopy(colors, 0, newColors, 0, colors.length);
            colors = newColors;

            Color[][][] newDataColors = new Color[id + 1][][];
            System.arraycopy(datacolors, 0, newDataColors, 0, datacolors.length);
            datacolors = newDataColors;
        }

        if (meta > 0 || datacolors[id] != null)
        {
            if (datacolors[id] == null)
                datacolors[id] = new Color[16][];
            if (datacolors[id][meta] == null)
                datacolors[id][meta] = new Color[5];
            datacolors[id][meta][0] = color;
            datacolors[id][meta][1] = color;
            datacolors[id][meta][2] = color;
            datacolors[id][meta][3] = color;
            datacolors[id][meta][4] = color;
        }
        else
        {
            if (colors[id] == null)
                colors[id] = new Color[5];
            colors[id][0] = color;
            colors[id][1] = color;
            colors[id][2] = color;
            colors[id][3] = color;
            colors[id][4] = color;
        }
        return color;
    }

    /* ------------------------------------------------------------ */

    public static BufferedImage getBlockImage(Block block)
    {
        try
        {
            String textureName = ((ServerBlock) block).getTextureNameServer();
            ResourceLocation relLoc = new ResourceLocation(textureName);
            String resourceName = "/assets/" + relLoc.getResourceDomain() + "/textures/blocks/" + relLoc.getResourcePath();

            String[] endings = { "_top", "" };
            for (String ending : endings)
            {
                InputStream is = Object.class.getResourceAsStream(resourceName + ending + ".png");
                if (is != null)
                    return ImageIO.read(is);
            }
            // Pattern resourcePattern = Pattern.compile(Pattern.quote(resourceName) + ".*\\.png");
            // List<String> resources = ResourceList.getResources(resourcePattern);
            // if (resources.isEmpty())
            // return null;
            // resourceName = resources.get(0);
            // InputStream is = Object.class.getResourceAsStream(resourceName);
            // if (is == null)
            // return null;
            // return ImageIO.read(is);
        }
        catch (IOException e)
        {
            LoggingHandler.felog.warn(String.format("Unable to get texture for block %s", GameData.getBlockRegistry().getNameForObject(block)));
        }
        return null;
    }

    public static Color getAverageColor(BufferedImage image)
    {
        long r = 0;
        long g = 0;
        long b = 0;
        for (int ix = 0; ix < image.getWidth(); ix++)
        {
            for (int iy = 0; iy < image.getHeight(); iy++)
            {
                int rgb = image.getRGB(ix, iy);
                Color color = new Color(rgb);
                r += color.getRed();
                g += color.getGreen();
                b += color.getBlue();
            }
        }
        int count = image.getWidth() * image.getHeight();
        r /= count;
        g /= count;
        b /= count;
        return new Color((int) r, (int) g, (int) b);
    }

    /* ------------------------------------------------------------ */

    public static void loadColorScheme(InputStream stream)
    {
        colors = new Color[4096][];
        datacolors = new Color[4096][][];
        // biomecolors = new Color[BiomeMap.values().length][];
        // raincolors = new Color[64][];
        // tempcolors = new Color[64][];

        /* Default the biome color */
        for (int i = 0; i < biomecolors.length; i++)
        {
            Color[] c = new Color[5];
            int red = 0x80 | (0x40 * ((i >> 0) & 1)) | (0x20 * ((i >> 3) & 1)) | (0x10 * ((i >> 6) & 1));
            int green = 0x80 | (0x40 * ((i >> 1) & 1)) | (0x20 * ((i >> 4) & 1)) | (0x10 * ((i >> 7) & 1));
            int blue = 0x80 | (0x40 * ((i >> 2) & 1)) | (0x20 * ((i >> 5) & 1));
            c[0] = new Color(red, green, blue);
            c[3] = new Color(red * 4 / 5, green * 4 / 5, blue * 4 / 5);
            c[1] = new Color(red / 2, green / 2, blue / 2);
            c[2] = new Color(red * 2 / 5, green * 2 / 5, blue * 2 / 5);
            c[4] = new Color((c[1].getRed() + c[3].getRed()) / 2, (c[1].getGreen() + c[3].getGreen()) / 2, (c[1].getBlue() + c[3].getBlue()) / 2,
                    (c[1].getAlpha() + c[3].getAlpha()) / 2);

            biomecolors[i] = c;
        }

        try
        {
            Scanner scanner = new Scanner(stream);
            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                if (line.startsWith("#") || line.equals(""))
                {
                    continue;
                }
                /* Make parser less pedantic - tabs or spaces should be fine */
                String[] split = line.split("[\t ]");
                int cnt = 0;
                for (String s : split)
                {
                    if (s.length() > 0)
                        cnt++;
                }
                String[] nsplit = new String[cnt];
                cnt = 0;
                for (String s : split)
                {
                    if (s.length() > 0)
                    {
                        nsplit[cnt] = s;
                        cnt++;
                    }
                }
                split = nsplit;
                if (split.length < 17)
                {
                    continue;
                }
                Integer id;
                Integer dat = null;
                boolean isbiome = false;
                boolean istemp = false;
                boolean israin = false;
                int idx = split[0].indexOf(':');
                if (idx > 0)
                { /* ID:data - data color */
                    id = new Integer(split[0].substring(0, idx));
                    dat = new Integer(split[0].substring(idx + 1));
                }
                else if (split[0].charAt(0) == '[')
                { /* Biome color data */
                    String bio = split[0].substring(1);
                    idx = bio.indexOf(']');
                    if (idx >= 0)
                        bio = bio.substring(0, idx);
                    isbiome = true;
                    id = -1;
                    BiomeMap[] bm = BiomeMap.values();
                    for (int i = 0; i < bm.length; i++)
                    {
                        if (bm[i].toString().equalsIgnoreCase(bio))
                        {
                            id = i;
                            break;
                        }
                        else if (bio.equalsIgnoreCase("BIOME_" + i))
                        {
                            id = i;
                            break;
                        }
                    }
                    if (id < 0)
                    { /* Not biome - check for rain or temp */
                        if (bio.startsWith("RAINFALL-"))
                        {
                            try
                            {
                                double v = Double.parseDouble(bio.substring(9));
                                if ((v >= 0) && (v <= 1.00))
                                {
                                    id = (int) (v * 63.0);
                                    israin = true;
                                }
                            }
                            catch (NumberFormatException nfx)
                            {
                            }
                        }
                        else if (bio.startsWith("TEMPERATURE-"))
                        {
                            try
                            {
                                double v = Double.parseDouble(bio.substring(12));
                                if ((v >= 0) && (v <= 1.00))
                                {
                                    id = (int) (v * 63.0);
                                    istemp = true;
                                }
                            }
                            catch (NumberFormatException nfx)
                            {
                            }
                        }
                    }
                }
                else
                {
                    id = new Integer(split[0]);
                }
                if ((!isbiome) && (id >= colors.length))
                {
                    Color[][] newcolors = new Color[id + 1][];
                    System.arraycopy(colors, 0, newcolors, 0, colors.length);
                    colors = newcolors;
                    Color[][][] newdatacolors = new Color[id + 1][][];
                    System.arraycopy(datacolors, 0, newdatacolors, 0, datacolors.length);
                    datacolors = newdatacolors;
                }

                Color[] c = new Color[5];

                /* store colors by raycast sequence number */
                c[0] = new Color(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]));
                c[3] = new Color(Integer.parseInt(split[5]), Integer.parseInt(split[6]), Integer.parseInt(split[7]), Integer.parseInt(split[8]));
                c[1] = new Color(Integer.parseInt(split[9]), Integer.parseInt(split[10]), Integer.parseInt(split[11]), Integer.parseInt(split[12]));
                c[2] = new Color(Integer.parseInt(split[13]), Integer.parseInt(split[14]), Integer.parseInt(split[15]), Integer.parseInt(split[16]));
                /* Blended color - for 'smooth' option on flat map */
                c[4] = new Color((c[1].getRed() + c[3].getRed()) / 2, (c[1].getGreen() + c[3].getGreen()) / 2, (c[1].getBlue() + c[3].getBlue()) / 2,
                        (c[1].getAlpha() + c[3].getAlpha()) / 2);

                if (isbiome)
                {
                    if (istemp)
                    {
                        tempcolors[id] = c;
                    }
                    else if (israin)
                    {
                        raincolors[id] = c;
                    }
                    else if ((id >= 0) && (id < biomecolors.length))
                        biomecolors[id] = c;
                }
                else if (dat != null)
                {
                    Color[][] dcolor = datacolors[id]; /* Existing list? */
                    if (dcolor == null)
                    {
                        dcolor = new Color[16][]; /* Make 16 index long list */
                        datacolors[id] = dcolor;
                    }
                    if ((dat >= 0) && (dat < 16))
                    { /* Add color to list */
                        dcolor[dat] = c;
                    }
                    if (dat == 0)
                    { /* Index zero is base color too */
                        colors[id] = c;
                    }
                }
                else
                {
                    colors[id] = c;
                }
            }
            scanner.close();
            /* Last, push base color into any open slots in data colors list */
            for (int k = 0; k < datacolors.length; k++)
            {
                Color[][] dc = datacolors[k]; /* see if data colors too */
                if (dc != null)
                {
                    Color[] c = colors[k];
                    for (int i = 0; i < 16; i++)
                    {
                        if (dc[i] == null)
                            dc[i] = c;
                    }
                }
            }
            /* And interpolate any missing rain and temperature colors */
            interpolateColorTable(tempcolors);
            interpolateColorTable(raincolors);
        }
        catch (RuntimeException e)
        {
            // Log.severe("Could not load colors '" + name + "' ('" + colorSchemeFile + "').", e);
        }
    }

    public static void interpolateColorTable(Color[][] c)
    {
        int idx = -1;
        for (int k = 0; k < c.length; k++)
        {
            if (c[k] == null)
            { /* Missing? */
                if ((idx >= 0) && (k == (c.length - 1)))
                { /* We're last - so fill forward from last color */
                    for (int kk = idx + 1; kk <= k; kk++)
                    {
                        c[kk] = c[idx];
                    }
                }
                /* Skip - will backfill when we find next color */
            }
            else if (idx == -1)
            { /* No previous color, just backfill this color */
                for (int kk = 0; kk < k; kk++)
                {
                    c[kk] = c[k];
                }
                idx = k; /* This is now last defined color */
            }
            else
            { /* Else, interpolate between last idx and this one */
                int cnt = c[k].length;
                for (int kk = idx + 1; kk < k; kk++)
                {
                    double interp = (double) (kk - idx) / (double) (k - idx);
                    Color[] cc = new Color[cnt];
                    for (int jj = 0; jj < cnt; jj++)
                    {
                        cc[jj] = new Color((int) ((1.0 - interp) * c[idx][jj].getRed() + interp * c[k][jj].getRed()), (int) ((1.0 - interp)
                                * c[idx][jj].getGreen() + interp * c[k][jj].getGreen()), (int) ((1.0 - interp) * c[idx][jj].getBlue() + interp
                                * c[k][jj].getBlue()), (int) ((1.0 - interp) * c[idx][jj].getAlpha() + interp * c[k][jj].getAlpha()));
                    }
                    c[kk] = cc;
                }
                idx = k;
            }
        }
    }

    public static int worldToChunk(int v)
    {
        return v >> 4;
    }

    public static int worldToRegion(int v)
    {
        return v >> 9;
    }

    public static int chunkToRegion(int v)
    {
        return v >> 5;
    }

    public static int regionToChunk(int v)
    {
        return v << 5;
    }

}
