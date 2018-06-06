package com.example.navigationdrawertest.tree;

import java.util.Iterator;
import java.util.Stack;

public class TreeNodeIterator implements Iterator{
	Stack stack = new Stack();
	public TreeNodeIterator(Iterator iterator){
		stack.push(iterator);
	}
	@Override
	public Object next() {
		// TODO Auto-generated method stub
		if(hasNext()){
			Iterator iterator = (Iterator)stack.peek();
			TreeNode node = (TreeNode)iterator.next();
			if(node instanceof DepartmentNode){
				stack.push(((DepartmentNode) node).createIterator());
			}
			return node;
		}
		else
		{
			return null;
		}
	}
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		if(stack.empty()){
			return false;
		}
		else{
			Iterator iterator = (Iterator)stack.peek();
			if(!iterator.hasNext()){
				stack.pop();
				return hasNext();
			}
			else{
				return true;
			}
		}
	}
	@Override
	public void remove() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
