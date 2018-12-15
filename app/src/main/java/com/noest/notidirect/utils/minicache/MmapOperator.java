package com.noest.notidirect.utils.minicache;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class MmapOperator {
    /**
     * the first few bytes store file info, after that store real data
     */
    private static final int DATA_START_POS = 16;

    /**
     * initial length of mapped file
     */
    private static final int INITIAL_MAP_LEN = 2 * 1024;

    /**
     * mapped file
     */
    private final String mMappedFile;

    /**
     * mmap handler
     */
    private MappedByteBuffer mbb;

    /**
     * raw data length, also current write ptr
     */
    private int mRawDataLen;

    /**
     * all data stored
     */
    private Map<String, byte[]> mAllData = new TreeMap<>();

    MmapOperator(String cachFile) {
        mMappedFile = cachFile;
        init();
    }

    /**
     * init cache mmap, get saved state {@code mRawDataLen} {@code mCapacity} from mapped file
     */
    private void init() {
        // first get mapped file state, after that create real mmap
        createMmap(INITIAL_MAP_LEN);

        mbb.clear();

        // initial mapped file capacity and raw data len is zero
        int capacity = Math.max(mbb.getInt(), INITIAL_MAP_LEN);
        mRawDataLen = Math.max(mbb.getInt(), DATA_START_POS);

        if (capacity != INITIAL_MAP_LEN) {
            // if mCpacity is bigger than initial len, remap file
            createMmap(capacity);
        }

        loadData();
    }

    /**
     * load data from mmap, create {@code mAllData} from mapped file
     */
    void loadData() {
        mAllData.clear();

        mbb.clear();
        // move read ptr to data start pos
        mbb.position(DATA_START_POS);

        byte[] key;
        byte[] value;
        byte sum;

        while (mbb.position() < mRawDataLen) {
            // find item split mark
            boolean find = DataWrapUtil.findItemStart(mbb, mRawDataLen);
            if (!find) {
                break;
            }
            mbb.mark();

            // try get data
            key = DataWrapUtil.getWrappedData(mbb);
            value = DataWrapUtil.getWrappedData(mbb);
            sum = mbb.remaining() > 0 ? mbb.get() : -1;

            // if data not right, try
            if (DataWrapUtil.checkSum(key, value) != sum) {
                mbb.reset();
            } else {
                assert key != null;
                mAllData.put(new String(key), value);
            }
        }
    }

    /**
     * put data to cache, if capacity not enough, growth it
     */
    public void put(String key, byte[] value) {
        byte[] put = mAllData.put(key, value);
        if (put != null && Arrays.equals(put, value)) {
            // put same data, don't need store
            return;
        }
        store(key, value);
    }

    /**
     * do store data to cache
     */
    private void store(String key, byte[] value) {
        int needLen = DataWrapUtil.getWrappedLen(key.length(), value.length);

        // move pos to write ptr
        mbb.position(mRawDataLen);

        if (mbb.remaining() > needLen) {
            // room is enough
            DataWrapUtil.wrapData(mbb, key.getBytes(), value);
        } else {
            // need double capacity
            int totalDataLen = DATA_START_POS;
            for (Map.Entry<String, byte[]> item : mAllData.entrySet()) {
                totalDataLen += DataWrapUtil.getWrappedLen(item.getKey().length(), item.getValue().length);
            }
            int capacity = mbb.capacity();
            while (totalDataLen > capacity) {
                capacity *= 2;
            }
            createMmap(capacity);

            // write new capacity to cache
            mbb.clear();
            mbb.putInt(capacity);

            // move ptr to real data start
            mbb.position(DATA_START_POS);
            // write origin data
            for (Map.Entry<String, byte[]> item : mAllData.entrySet()) {
                byte[] dkey = item.getKey().getBytes();
                byte[] dvalue = item.getValue();
                DataWrapUtil.wrapData(mbb, dkey, dvalue);
            }
        }

        updateRawDataPos();
    }

    /**
     * update {@code mRawDataLen} in mem and cache
     */
    private void updateRawDataPos() {
        mRawDataLen = mbb.position();
        mbb.clear();
        // first int is capacity, need skip 4 bytes
        mbb.position(4);
        mbb.putInt(mRawDataLen);
    }

    private void createMmap(int capacity) {
        try {
            RandomAccessFile rw = new RandomAccessFile(mMappedFile, "rw");
            mbb = rw.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, capacity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    byte[] get(String key) {
        return mAllData.get(key);
    }

    public Map<String, byte[]> getAll() {
        return mAllData;
    }
}
