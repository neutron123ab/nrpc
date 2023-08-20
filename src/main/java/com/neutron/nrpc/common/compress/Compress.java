package com.neutron.nrpc.common.compress;

/**
 * @author zzs
 * @date 2023/8/17 23:16
 */
public interface Compress {
    
    byte[] compress(byte[] bytes);
    
    byte[] decompress(byte[] bytes);
    
}
