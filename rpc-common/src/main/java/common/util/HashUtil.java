package common.util;

/**
 * @author wanjiahao
 */
public class HashUtil {
    /**
     * 使用String自带的hashCode函数
     * @param str
     * @return
     */
    public static Integer stringHashcode(String str){
        int hash = str.hashCode();
        if (hash<0){
            hash = Math.abs(hash);
        }
        return hash;
    }

    /**
     * 使用FNV1_32_HASH算法计算hash值
     * @param str
     * @return
     */
    public static Integer FNV1_32_HASH(String str){
        final int p = 1677769;

        int hash = 0;
        int len = str.length();
        for (int i=0;i<len;i++){
            hash = (hash^str.charAt(i))*p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        if (hash<0){
            hash=Math.abs(hash);
        }
        return hash;
    }
}
