package com.bingzer.android.dbv.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Locale;

public class IOHelper {

	/**
	 * Delete dir and its children
	 * @param dir
	 */
	public static void deleteTree(File dir, boolean deleteSelf) {
		if(dir == null || !dir.isDirectory() || !dir.exists()) return;
		// delete tree
		for(File f : dir.listFiles()) {
			if(f.isDirectory()) deleteTree(f, true);
			else f.delete();
		}
		if(deleteSelf) dir.delete();
	}
	
	public static void safeCreateDir(File dir){
		if(dir.exists()) return;
		dir.mkdir();
	}
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
		 if(sourceFile.equals(destFile)) return;
		 if(!destFile.exists()) destFile.createNewFile();
		 
		 FileChannel source = null;
		 FileChannel destination = null;
		 try {
			 source = new FileInputStream(sourceFile).getChannel();
			 destination = new FileOutputStream(destFile).getChannel();
			 destination.transferFrom(source, 0, source.size());
		 }
		 finally {
			 if(source != null) {
				 source.close();
			 }
			 if(destination != null) {
				 destination.close();
			 }
		}
	}// end copyFile()
	
	/**
	 * Copy source stream and write to destFile
	 * @param input
	 * @param destFile
	 * @throws java.io.IOException
	 */
	public static void copyFile(InputStream input, File destFile) throws IOException {
		if(!(input instanceof BufferedInputStream)) input = new BufferedInputStream(input);
		
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
		byte[] buffer = new byte[1024];
		int read;
		while((read = input.read(buffer)) != -1){
			bos.write(buffer, 0, read);
		}
		bos.flush();
		bos.close();
		input.close();
		buffer = null;
	}
	
	public static void copy(InputStream source, StringBuilder builder) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(source);
		byte[] buffer = new byte[1024];
		while(bis.read(buffer) != -1){
			builder.append(new String(buffer));
		}
		bis.close();
		buffer = null;
	}
	
	public static String stripExtension(File file){
		if(file == null) return "";
    	return stripExtension(file.getName());
	}
	
	public static String stripExtension(String filename){
		if(filename == null) return "";
		
		if(filename.indexOf("") > 0){
			return filename.substring(0, filename.indexOf(""));
		}
		return filename;
	}
	
	public static String getExtension(File file){
		if(file == null) return null;
		return getExtension(file.getName());
	}
	
	public static String getExtension(String filename){
		if(filename == null) return null;
		
		if(filename.indexOf("") > 0){
			return filename.substring(filename.indexOf(""));
		}
		
		return "";
	}

	public static File[] getFiles(File dir, final String... extensions) {
		File[] files = dir.listFiles(new FileFilter(){
			@Override
            public boolean accept(File file) {
				boolean accept = false;
				for(String ext : extensions){
					if(accept = (file.getName().toLowerCase(Locale.getDefault()).endsWith(ext)))
						break;
				}
				return accept;
			}	
		});
		return files;
	}
	
	public static long getDirectorySize(File dir) {
		long size = 0;
		for (File file : dir.listFiles()) {
		    if (file.isFile()) {
		        size += file.length();
		    }
		    else
		        size += getDirectorySize(file);
		}
		return size;
	}
}
