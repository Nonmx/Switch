/**
 * 
 */
package sselab.switchy.simplenode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sselab.cfg.node.specifiednode.IfNode;
import sselab.cfg.parser.CFGNode;
import sselab.switchy.simplenode.visitor.SimpleNodePrinter;
import sselab.switchy.simplenode.visitor.SimpleNodeVisitor;

/**
 * The class of Control flow node.
 * IfNode/SwitchNode
 * @author SSELAB-129
 *
 */
public class SimpleControlNode extends SimpleNormalNode{
	public SimpleControlNode() {
	}
	public SimpleControlNode(CFGNode node, String taskID){
		super(node, taskID);
		this.setStringVal(node.getCode());
	}
	
	@Override
	public void accept(SimpleNodeVisitor visitor, Set<SimpleNode> visited) {
		if(visited.contains(this))
			return;
		visitor.visit(this, visited);
		SimpleNode lastElse = findLastElse(this.getFalseNode());
		getTrueNode().accept(visitor, visited);
		//If there are FalseNode(else) branches then traverse them.
		if(!lastElse.getBeforeNode(0).equals(this))
			getFalseNode().accept(visitor, visited);
		//add brace while printing.
		if(this.isEndOfBlock()){
			if(visitor instanceof SimpleNodePrinter) 
				((SimpleNodePrinter) visitor).endOfBlock();
		}
		else{
			if(visited.contains(lastElse))
				lastElse.getAfterNode(0).accept(visitor, visited);
			else
				lastElse.accept(visitor, visited);
		}
	}
	
	public SimpleNode getTrueNode(){
		return this.getAfterNode(0);
	}
	
	public SimpleNode getFalseNode(){
		return this.getAfterNode(1);
	}
	
	public SimpleNode findLastElse(SimpleNode curNode){
		if(curNode.getBeforeNodes().size() > 1)
			return curNode;
		if(curNode instanceof SimpleControlNode)
			return findLastElse(((SimpleControlNode) curNode).getFalseNode());
		else
			return findLastElse(curNode.getAfterNode(0));
	}
	
	@Override
	public String getCode(){
		return this.getStringVal() + "{\n";
	}
}
