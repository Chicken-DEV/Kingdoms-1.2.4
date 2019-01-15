package com.songoda.kingdoms.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CaseInsensitiveCocurrentHashMap<V> extends ConcurrentHashMap<String, V>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2439939722936663245L;
	
	@Override
	public V put(String key, V value) {
		return super.put(key.toLowerCase(), value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends V> m) {
		for(Map.Entry<? extends String, ? extends V> entry : m.entrySet()){
			this.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public V get(Object key) {
		if(key instanceof String) return super.get(((String) key).toLowerCase());
		return super.get(key);
	}
	
	@Override
	public V getOrDefault(Object key, V defaultValue) {
		if(key instanceof String) return super.getOrDefault(((String) key).toLowerCase(), defaultValue);
		return super.getOrDefault(key, defaultValue);
	}

	@Override
	public boolean containsKey(Object key) {
		if(key instanceof String) return super.containsKey(((String) key).toLowerCase());
		return super.containsKey(key);
	}

	@Override
	public V remove(Object key) {
		if(key instanceof String) return super.remove(((String) key).toLowerCase());
		return super.remove(key);
	}

	@Override
	public boolean remove(Object key, Object value) {
		if(key instanceof String) return super.remove(((String) key).toLowerCase(), value);
		return super.remove(key, value);
	}

	
}