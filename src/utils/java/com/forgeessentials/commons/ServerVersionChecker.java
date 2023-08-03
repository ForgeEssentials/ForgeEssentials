package com.forgeessentials.commons;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

public class ServerVersionChecker {

	public class Commit{
	    public String sha;
	    public String url;
	}

	public class Root{
	    public String name;
	    public String zipball_url;
	    public String tarball_url;
	    public Commit commit;
	    public String node_id;
	}


	
	public static void doCheckLatestVersion(){

		try {
			String json = readUrl("https://api.github.com/repos/ForgeEssentials/ForgeEssentials/tags");
			Gson gson = new Gson();        
		    Root[] pages = gson.fromJson(json, Root[].class);

		    for (Root tag : pages) {
		    	String[] values = StringUtils.split(tag.name, '.');
	            if (values.length != 3 || !values[0].matches("[0-9]+") || !values[1].matches("[0-9]+")
	                    || !values[2].matches("[0-9]+") || !values[0].equals(BuildInfo.BASE_VERSION))
	            {
	                continue;
	            }
	            BuildInfo.febuildinfo.debug("Found valid update tag: " +  tag.name);
	            if (Integer.parseInt(values[1]) > Integer.parseInt(BuildInfo.MAJOR_VERSION)
	                    && Integer.parseInt(values[1]) > BuildInfo.majorNumberLatest)
	            {
	            	BuildInfo.majorNumberLatest = Integer.parseInt(values[1]);
	            	BuildInfo.minorNumberLatest = Integer.parseInt(values[2]);
	                continue;
	            }
	            if (Integer.parseInt(values[2]) > BuildInfo.MINOR_VERSION 
	            		&& Integer.parseInt(values[2]) > BuildInfo.minorNumberLatest
	            		&& Integer.parseInt(values[1]) >= Integer.parseInt(BuildInfo.MAJOR_VERSION))
	            {
	            	BuildInfo.majorNumberLatest = Integer.parseInt(values[1]);
	            	BuildInfo.minorNumberLatest = Integer.parseInt(values[2]);
	                continue;
	            }
		    }
		    BuildInfo.postNewVersionNotice();
		} catch (Exception e) {
			BuildInfo.febuildinfo.error("Unable to retrieve version info from API");
			e.printStackTrace();
			return;
		}
	}

	private static String readUrl(String urlString) throws Exception {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 

	        return buffer.toString();
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}
}
