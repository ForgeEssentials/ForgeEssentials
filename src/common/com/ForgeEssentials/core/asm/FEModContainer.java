package com.ForgeEssentials.core.asm;

import java.util.Arrays;

import com.ForgeEssentials.core.Version;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class FEModContainer extends DummyModContainer {
        public FEModContainer() {
                super(new ModMetadata());
                /* ModMetadata is the same as mcmod.info */
                ModMetadata myMeta = super.getMetadata();
                myMeta.authorList = Arrays.asList(new String[] { "AbrarSyed", "Bob-A-Red-Dino", "bspkrs", "MysteriousAges", "luacs1998" });
                myMeta.description = "A permissions and protection system for use on Forge servers, replacing WorldEdit and WorldGuard functionality. ";
                myMeta.modId = "ForgeEssentials";
                myMeta.version = Version.version;
                myMeta.name = "ForgeEssentials";
                myMeta.url = "";
        }
        
        public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
        }
        /* 
         * Use this in place of @Init, @Preinit, @Postinit in the file.
         */
        @Subscribe                
        public void onServerStarting(FMLServerStartingEvent e) {
                
                
        }
        
}