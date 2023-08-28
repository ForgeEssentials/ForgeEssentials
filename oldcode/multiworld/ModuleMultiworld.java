package com.forgeessentials.multiworld;

	@SubscribeEvent public void postLoad(e) { try { multiworldManager.loadWorldProviders(); multiworldManager.loadWorldTypes();
      
      } catch (java.lang.NoSuchMethodError
      noSuchMethodError) { CrashReport report = CrashReport.makeCrashReport(noSuchMethodError,
      "MultiWorld Unable to Load, please update Forge or Disable MultiWorld in the main.cfg!" ); report.addCategory("MultiWorld"); throw new ReportedException(report); } }

     

}
