package com.genuineflix.caption.translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.genuineflix.caption.caption.Caption;
import com.genuineflix.util.Titulary;

public class Translation implements Titulary<List<String>> {

	public static final Translation NONE = new Translation("NONE");
	private final String name;
	private final List<String> list;
	private transient int index = 0;

	public Translation(final Caption caption) {
		this(caption.key, new ArrayList<String>());
	}

	public Translation(final String name) {
		this(name, new ArrayList<String>());
	}

	public Translation(final String name, final List<String> list) {
		this.name = name;
		this.list = list;
	}

	public boolean add(final String trans) {
		return getValue().add(trans);
	}

	@Override
	public int compareTo(final List<String> t) {
		return list.size() - t.size();
	}

	@Override
	public int compareToKey(final String o) {
		return getKey().compareToIgnoreCase(o);
	}

	public boolean contains(final Object o) {
		return getValue().contains(o);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof List)
			return getValue().equals(obj);
		if (obj instanceof Titulary)
			return getValue().equals(((Titulary) obj).getValue());
		return super.equals(obj);
	}

	public String get(final int index) {
		return getValue().get(index);
	}

	public String getCurrent() {
		return get(index % size());
	}

	@Override
	public String getKey() {
		return name;
	}

	public String getNext() {
		return get(++index % size());
	}

	public String getPrevious() {
		return get(--index % size());
	}

	@Override
	public List<String> getValue() {
		return list;
	}

	@Override
	public int hashCode() {
		return getKey().hashCode();
	}

	public boolean isEmpty() {
		return this == Translation.NONE || getValue().isEmpty();
	}

	@Override
	public boolean nameEquals(final String s) {
		return getKey().equals(s);
	}

	@Override
	public List<String> setValue(final List<String> value) {
		final List<String> old = new ArrayList<String>(size());
		Collections.copy(old, getValue());
		getValue().clear();
		getValue().addAll(value);
		return old;
	}

	public int size() {
		return getValue().size();
	}
}
