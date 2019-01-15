package com.songoda.kingdoms.manager.game;

import com.songoda.kingdoms.main.Config;
import com.songoda.kingdoms.manager.Manager;
import org.bukkit.plugin.Plugin;
import com.songoda.kingdoms.main.Kingdoms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class DataZipper extends Manager {

	protected DataZipper(Plugin plugin) {
		super(plugin);
		//new Thread(new autoBackupTask()).start();
	}

	@Override
	public void onDisable() {
		AppZip.main();
		

	}
	

	private class autoBackupTask implements Runnable{

		@Override
		public void run() {
			while(plugin.isEnabled()){
				try {
					Thread.sleep(60 * 1000L * Config.getConfig().getInt("minutes-per-backup"));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				AppZip.main();
			}
		}
		
	}

	public static class AppZip
	{
	    List<String> fileList = new ArrayList<String>();
	    
	    private static final String SOURCE_FOLDER = new File(".").getAbsolutePath() + File.separator + Kingdoms.getInstance().getDataFolder().getPath();
		
	    public AppZip(){
	    	
	    }
		
	    public static void main()
	    {
	    	if(!Config.getConfig().getBoolean("enable-auto-backup")) return;
	    	File output = new File(Kingdoms.getInstance().getDataFolder().getPath() + File.separator + "backups");	    	
	    	if(!output.exists()){
	    		output.mkdirs();
	    	}
	    	AppZip appZip = new AppZip();
	    	appZip.generateFileList(new File(SOURCE_FOLDER));
	    	Date date = new Date() ;
	    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
	    	String name = dateFormat.format(date);//DateFormat.getInstance().format(Calendar.getInstance().getTime()).replaceAll("\\", "-");
	    	String OUTPUT_ZIP_FILE = Kingdoms.getInstance().getDataFolder().getPath() + File.separator + "backups" + File.separator + name + ".zip";
	    	appZip.zipIt(OUTPUT_ZIP_FILE);
	    }
	    
	    /**
	     * Zip it
	     * @param zipFile output ZIP file location
	     */
	    public void zipIt(String zipFile){

	     byte[] buffer = new byte[1024];
	    	
	     try{
	    	
	    	FileOutputStream fos = new FileOutputStream(zipFile);
	    	ZipOutputStream zos = new ZipOutputStream(fos);
	    		
	    	Kingdoms.logInfo("Saving new backup to: " + zipFile);
	    	int i = 0;
	    	for(String file : this.fileList){
	    		i++;
	    		ZipEntry ze= new ZipEntry(file);
	        	zos.putNextEntry(ze);
	               
	        	FileInputStream in = 
	        			new FileInputStream(SOURCE_FOLDER + File.separator + file);
	       	   
	        	int len;
	        	while ((len = in.read(buffer)) > 0) {
	        		zos.write(buffer, 0, len);
	        	}
	               
	        	in.close();
	    	}
	    	Kingdoms.logInfo(i + " files backed up");
	    		
	    	zos.closeEntry();
	    	//remember close it
	    	zos.close();
	          
	    	Kingdoms.logInfo("Done");
	    }catch(IOException ex){
	       ex.printStackTrace();   
	    }
	   }
	    
	    /**
	     * Traverse a directory and get all files,
	     * and add the file into fileList  
	     * @param node file or directory
	     */
	    public void generateFileList(File node){

	    	//add file only
		if(node.isFile()){
			fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
		}
			
		if(node.isDirectory()){
			if(node.getName().equals("backups")) return;
			String[] subNote = node.list();
			for(String filename : subNote){
				generateFileList(new File(node, filename));
			}
		}
	 
	    }

	    /**
	     * Format the file path for zip
	     * @param file file path
	     * @return Formatted file path
	     */
	    private String generateZipEntry(String file){
	    	return file.substring(SOURCE_FOLDER.length()+1, file.length());
	    }
	}
	
	
}
