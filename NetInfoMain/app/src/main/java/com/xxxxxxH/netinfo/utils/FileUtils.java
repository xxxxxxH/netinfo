package com.xxxxxxH.netinfo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class FileUtils {

    private static final int MIN_CLICK_DELAY_TIME = 3000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    public static boolean copyFile(File src, String destPath) {
        boolean result = false;
        if ((src == null) || (destPath== null)) {
            return result;
        }
        File dest= new File(destPath);
        if (dest!= null && dest.exists()) {
            dest.delete(); // delete file
        }
        try {
            dest.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileChannel srcChannel = null;
        FileChannel dstChannel = null;

        try {
            srcChannel = new FileInputStream(src).getChannel();
            dstChannel = new FileOutputStream(dest).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), dstChannel);
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        try {
            srcChannel.close();
            dstChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取文件夹下的所有文件名字
     *
     * @param path 文件夹路径
     * @return
     */
    public static ArrayList<String> getFilesAllName(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            list.add(path + File.separator + files[i].getName());
        }
        return list;
    }

    public static void writeTxt2File(String content, String filePath,String fileName) throws Exception{

        deletOldFile(filePath + File.separator + fileName);

        makeFilePath(filePath,fileName);

        String strFilePath = filePath + File.separator + fileName;

        File file = new File(strFilePath);

        if (!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        RandomAccessFile raf = new RandomAccessFile(file,"rwd");
        raf.seek(file.length());
        raf.write(content.getBytes());
        raf.close();
    }

    public static void makeFilePath(String filePath,String fileName)throws Exception{
        File file = null;
        file = new File(filePath + File.separator + fileName);

        if (!file.exists()){
            file.createNewFile();
        }
    }

    public static void makeRootDirectory(String filePath) throws Exception{
        File fileDir = null;
        fileDir = new File(filePath);
        if (!fileDir.exists()){
            fileDir.mkdir();
        }
    }

    public static void deletOldFile(String path){
        File file = new File(path);
        if (file.exists()){
            file.delete();
        }
    }
}
