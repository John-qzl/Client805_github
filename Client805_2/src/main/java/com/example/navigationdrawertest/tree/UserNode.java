package com.example.navigationdrawertest.tree;

import java.util.ArrayList;
import java.util.Iterator;

public class UserNode extends TreeNode{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3850069050326254577L;
	long id;
	String name;
	TreeNode parentNode;

	String tablesize;
	int layer;
	int checkStatus = 0;
	int expandStatus= 2;
	
	
	public UserNode(long id, String name, TreeNode parentNode, int layer) {
//	public UserNode(long id, String name, String tablesize, TreeNode parentNode, int layer) {
		super();
		this.id = id;
		this.name = name;
		this.parentNode = parentNode;
		this.layer = layer;
		this.tablesize = tablesize;
	}

	@Override
	public String getTableSize() {
		return tablesize;
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return id;
	}
	@Override
	public int getLayer() {
		// TODO Auto-generated method stub
		return layer;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
			
	@Override
	public int getCheckStatus() {
		// TODO Auto-generated method stub
		return checkStatus;
	}
	@Override
	public void setCheckStatus(int status) {
		// TODO Auto-generated method stub
		checkStatus = status;
	}
	@Override
	public TreeNode getParent() {
		// TODO Auto-generated method stub
		return parentNode;
	}
	@Override
	public int getExpandStatus() {
		// TODO Auto-generated method stub
		return expandStatus;
	}
	@Override
	public void setExpandStatus(int status) {
		// TODO Auto-generated method stub
		expandStatus=status;
	}
	
	@Override
	public void expandAllNode() {
		// TODO Auto-generated method stub
		return;
	}
	@Override
	public void filterVisibleNode(ArrayList<TreeNode> nodeList) {
		// TODO Auto-generated method stub
		nodeList.add(this);
	}

	@Override
	public void print() {
		// TODO Auto-generated method stub
		System.out.println(name+"-"+id);;
	}
	public Iterator<TreeNode> createIterator(){
		return new NullIterator();
	}
}
