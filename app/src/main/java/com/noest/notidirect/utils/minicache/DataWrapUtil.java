package com.noest.notidirect.utils.minicache;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

class DataWrapUtil {
    /**
     * for item split, if some data broken, can protect useful data
     */
    private static final byte SPLIT_MARK = 0x52;

    /**
     * wrapped data format is : len(4 byte) data(len byte)
     */
    static byte[] getWrappedData(ByteBuffer buffer) {
        if (buffer.remaining() < 4) {
            return null;
        }
        int len = buffer.getInt();
        if (buffer.remaining() < len) {
            return null;
        }
        byte[] data = new byte[len];
        buffer.get(data);
        return data;
    }

    /**
     * wrap a set of bytes, two bytes is one group
     * each group have one sum
     */
    static void wrapData(ByteBuffer buffer, byte[]... datas) {
//        int len = getWrappedLen(datas);
//        if (buffer.remaining() < len) {
//            // todo
//        }
        byte sum = checkSum(datas);
        buffer.put(SPLIT_MARK);
        for (byte[] data : datas) {
            buffer.putInt(data.length);
            buffer.put(data);
        }
        buffer.put(sum);
    }

    /**
     * get a set of bytes wrapped len, a set have one byte check sum
     */
    private static int getWrappedLen(byte[]... datas) {
        int len = 2; // checksum + mark
        for (byte[] data : datas) {
            len += 4 + data.length; // datalen(int) + data
        }
        return len;
    }

    static int getWrappedLen(int... dataLens) {
        int len = 2; // checksum + mark
        for (int l : dataLens) {
            len += 4 + l; // datalen(int) + data
        }
        return len;
    }

    /**
     * simple calculate sum of bytes
     */
    static byte checkSum(byte[]... datas) {
        if (datas == null) {
            return 0;
        }
        byte sum = 0;
        for (byte[] data : datas) {
            if (data == null) {
                return 0;
            }
            for (byte b : data) {
                sum ^= b;
            }
        }
        return sum;
    }

    static boolean findItemStart(MappedByteBuffer mbb, int rawDataLen) {
        while (mbb.get() != SPLIT_MARK) {
            if (mbb.position() > rawDataLen) {
                return false;
            }
        }
        return true;
    }
}
