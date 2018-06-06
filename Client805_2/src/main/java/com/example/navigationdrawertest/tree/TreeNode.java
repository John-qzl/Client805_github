package com.example.navigationdrawertest.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


public abstract class TreeNode implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5254630190410273624L;
	public void add(TreeNode node){
		throw new UnsupportedOperationException();
	}
	public void remove(TreeNode node){
		throw new UnsupportedOperationException();
	}
	public TreeNode getChild(int index){
		throw new UnsupportedOperationException();
	}
	public long getId(){
		throw new UnsupportedOperationException();
	}
	public TreeNode getParent(){
		throw new UnsupportedOperationException();
	}
	public String getName(){
		throw new UnsupportedOperationException();
	}
	public int getLayer(){
		throw new UnsupportedOperationException();
	}
	public String getTableSize(){
		throw new UnsupportedOperationException();
	}
	public void print(){
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 选中状态。0、未选中，1、选中，2、三态。
	 */
	public int getCheckStatus(){
		throw new UnsupportedOperationException();
	}
	public void setCheckStatus(int status){
		throw new UnsupportedOperationException();
	}
	/**
	 * 向上变更CheckStatus。
	 */
	public void UpChecked(){
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 折叠，展开。0，折叠，1、展开，2三态。
	 */
	public int getExpandStatus(){
		throw new UnsupportedOperationException();
	}
	
	public void setExpandStatus(int status){
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 展开所有
	 */
	public void expandAllNode(){
		throw new UnsupportedOperationException();
	}
	/**
	 * 收缩一层
	 * @param node
	 */
	public void shrinkNode(){
		throw new UnsupportedOperationException();
	}
	public void filterVisibleNode(ArrayList<TreeNode> nodeList){
		
	}
	public Iterator<TreeNode> createIterator(){
		throw new UnsupportedOperationException();
	}
}
