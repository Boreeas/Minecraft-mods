package net.boreeas.lively.util;

import com.google.common.cache.CacheLoader;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Malte Schütze
 */
public class SetCacheLoader<T2> extends CacheLoader<GlobalCoord, Set<T2>> {
    @Override
    public Set<T2> load(GlobalCoord _) throws Exception {
        return new HashSet<>();
    }
}
