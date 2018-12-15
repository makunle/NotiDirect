package com.noest.notidirect.utils.minicache;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * you can cache byte[] here, with high io speed,
 * data is safe, it will not lost when crash
 */
public class MiniCache {
    /**
     * cache folder, cache file in {@code sCacheFolder}/minicache/*
     */
    private static String sCacheFolder;

    /**
     * same process share one minicach instance
     */
    private static Map<String, MiniCache> cacheMap = new HashMap<>();
    private MmapOperator mOperator;

    private MiniCache() {
    }

    /**
     * init cache folder, if not exist create it
     *
     * @return cache file path
     */
    public static String init(String cacheFolder) {
        File file = new File(cacheFolder);
        if (file.exists() && file.isFile()) {
            // folder path exist, and is a file
            return null;
        }
        File folder = new File(cacheFolder, "minicache");
        if (folder.exists() && folder.isFile()) {
            // folder path exist, and is a file
            return null;
        }
        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (!success) {
                // make cache folder failed
                return null;
            }
        }
        sCacheFolder = folder.getAbsolutePath();
        return sCacheFolder;
    }

    public static MiniCache getDefaultCache() {
        return getCache("default");
    }

    /**
     * create with id(file name)
     */
    public static MiniCache getCache(String id) {
        if (sCacheFolder == null) {
            throw new IllegalStateException("need call MiniCache.init() first");
        }
        if (cacheMap.containsKey(id)) {
            return cacheMap.get(id);
        }
        File cacheFile = new File(sCacheFolder, id);

        if (!cacheFile.exists()) {
            try {
                boolean success = cacheFile.createNewFile();
                if (!success) {
                    throw new IllegalStateException("create cache file failed , check permission or storage");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MiniCache miniCache = new MiniCache();
        miniCache.mOperator = new MmapOperator(cacheFile.getAbsolutePath());

        cacheMap.put(id, miniCache);
        return miniCache;
    }

    public void put(String key, boolean data) {
        mOperator.put(key, new byte[]{(byte) (data ? 1 : 0)});
    }

    public boolean getBoolean(String key) {
        byte[] bytes = mOperator.get(key);
        return convertBoolean(bytes);
    }

    public Map<String, Boolean> getAllBoolean() {
        Map<String, byte[]> all = mOperator.getAll();
        HashMap<String, Boolean> ret = new HashMap<>();
        for (Map.Entry<String, byte[]> i : all.entrySet()) {
            ret.put(i.getKey(), convertBoolean(i.getValue()));
        }
        return ret;
    }

    private boolean convertBoolean(byte[] data) {
        return data != null && data[0] == 1;
    }
}
