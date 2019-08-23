package sselab.switchy.simplenode;

import java.util.Set;

import sselab.cfg.parser.CFGNode;
import sselab.switchy.simplenode.visitor.SimpleNodePrinter;
import sselab.switchy.simplenode.visitor.SimpleNodeVisitor;

public class SimpleNormalNode extends SimpleNode {

	private String stringVal;
	
	public SimpleNormalNode() {
		// TODO Auto-generated constructor stub
	}
	public SimpleNormalNode(String val){
		this.stringVal = val;
	}
	public SimpleNormalNode(CFGNode node, String taskID){
		this.node = node;
		stringVal = node.getCode();
		setTaskID(taskID);
	}
	
	/**
	 * The method set the string value of the CFG node statement.
	 * @param val
	 */
	public void setStringVal(String val){
		this.stringVal = val;
	}
	
	/**
	 * The method returns the string value of the CFG node statement.
	 * @return
	 */
	public String getStringVal(){
		return this.stringVal;
	}
	@Override
	public void accept(SimpleNodeVisitor visitor, Set<SimpleNode> visited) {
		if(visited.contains(this))
			return;
		visitor.visit(this, visited);
		if(this.isEndOfBlock()){
			if(visitor instanceof SimpleNodePrinter){
				((SimpleNodePrinter)visitor).endOfBlock();
			}
		}
		else
			getAfterNode(0).accept(visitor, visited);
	}
	public String getCode(){
		String code = "";
		if(this.getBeforeNode(0) instanceof SimpleControlNode){
			SimpleControlNode parentBranch = (SimpleControlNode) getBeforeNode(0);
			if(parentBranch.getFalseNode().equals(this))
				code += "\n";
		}
		code += getStringVal();
		return code;
	}
	
}
