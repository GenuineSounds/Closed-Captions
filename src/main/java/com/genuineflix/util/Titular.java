package com.genuineflix.util;

import java.util.Map.Entry;

public interface Titular<T> extends Entry<String, T> {

	int compareToKey(String str);

	@Override
	boolean equals(Object obj);

	@Override
	String getKey();

	@Override
	T getValue();

	boolean nameEquals(String str);

	@Override
	T setValue(T value);
}
