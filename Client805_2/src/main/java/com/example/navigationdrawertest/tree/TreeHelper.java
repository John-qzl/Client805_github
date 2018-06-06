package com.example.navigationdrawertest.tree;

import java.util.ArrayList;
import java.util.Iterator;

public class TreeHelper {
	public static ArrayList<TreeNode> filterVisibleNode(TreeNode rootNode)
	{
		ArrayList<TreeNode> result = new ArrayList<TreeNode>();

		result.add(rootNode);
		Iterator iterator = rootNode.createIterator();
		while(iterator.hasNext()){
			TreeNode node = (TreeNode)iterator.next();
			try{
				if(node.getExpandStatus()>0){
					result.add(node);
				}
			}
			catch(UnsupportedOperationException e){
				
			}
		}
		return result;
	}
	
	public static void onDepartmentChecked(TreeNode rootNode) {
		// TODO Auto-generated method stub
		int checkStatus=1;
		if(rootNode.getCheckStatus()==1)
			checkStatus=0;
		rootNode.setCheckStatus(checkStatus);
		
		Iterator iterator = rootNode.createIterator();
		while(iterator.hasNext()){
			TreeNode node = (TreeNode)iterator.next();
			try{
				node.setCheckStatus(checkStatus);
			}
			catch(UnsupportedOperationException e){
				
			}
		}
		
	}

}
