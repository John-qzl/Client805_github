package com.example.navigationdrawertest.tree;

import java.util.Iterator;

public class NullIterator implements Iterator{
	@Override
	public Object next() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void remove() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
