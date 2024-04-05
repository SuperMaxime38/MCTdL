package mctdl.game.utils;


public class Pair {
	Object key;
	Object value;
	public Pair(Object key, Object value) {
		this.key = key;
		this.value = value;
	}

	public Object getKey() {
		return key;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void clear() {
		this.key = null;
		this.value = null;
		System.gc();
	}
}
