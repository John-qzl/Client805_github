package com.example.navigationdrawertest.tree;

import java.util.ArrayList;
import java.util.Iterator;

public class DepartmentNode extends TreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4272214649572655288L;
	ArrayList<TreeNode> nodeList = new ArrayList<TreeNode>();
	long id;
	String name;
	TreeNode parentNode;

	String tablesize;
	int layer;
	int checkStatus = 0;
	int expandStatus= 0;
	
	public DepartmentNode(long id, String name, String tablesize, TreeNode parentNode, int layer) {
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
	public void add(TreeNode node) {
		// TODO Auto-generated method stub
		nodeList.add(node);
	}
	@Override
	public void remove(TreeNode node) {
		// TODO Auto-generated method stub
		nodeList.remove(node);
	}
	@Override
	public TreeNode getChild(int index) {
		// TODO Auto-generated method stub
		return nodeList.get(index);
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
	
	public TreeNode getParent() {
		// TODO Auto-generated method stub
		return parentNode;
	}

	@Override
	public void UpChecked() {
		// TODO Auto-generated method stub
		int checkNum=0;
		for(int i=0;i<nodeList.size();++i){
			if(nodeList.get(i).getCheckStatus()==1)
				checkNum++;
		}
		if(checkNum==nodeList.size())
			this.setCheckStatus(1);
		else if(checkNum==0)
			this.setCheckStatus(0);
		else
			this.setCheckStatus(2);
		if(parentNode!=null)
			parentNode.UpChecked();
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
		this.expandStatus = 1;
		Iterator<TreeNode> it = nodeList.iterator();
		while(it.hasNext()){
			TreeNode node = it.next();
			node.expandAllNode();
		}
	}
	
	@Override
	public void filterVisibleNode(ArrayList<TreeNode> nodeList) {
		// TODO Auto-generated method stub
		nodeList.add(this);
		Iterator<TreeNode> it = this.nodeList.iterator();
		while(it.hasNext()){
			TreeNode node = it.next();
			if(this.getExpandStatus()>0)
				node.filterVisibleNode(nodeList);
		}
	}
	
	@Override
	public void print() {
		// TODO Auto-generated method stub
		Iterator<TreeNode> it = nodeList.iterator();
		while(it.hasNext()){
			TreeNode node = it.next();
			node.print();
		}
	}
	public Iterator<TreeNode> createIterator(){
		return new TreeNodeIterator(nodeList.iterator());
	}
}
