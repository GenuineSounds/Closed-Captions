package com.genuineflix.util;

import java.util.Map.Entry;

public interface Titulary<T> extends Entry<String, T>, Comparable<T> {

	@Override
	int compareTo(T t);

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
