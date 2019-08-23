package sselab.switchy.simplenode;

import java.util.Set;

import sselab.cfg.node.specifiednode.TailNode;
import sselab.cfg.parser.CFGNode;
import sselab.switchy.simplenode.visitor.SimpleNodeVisitor;

public class SimplePseudoNode extends SimpleNode{

	public SimplePseudoNode() {
		// TODO Auto-generated constructor stub
	}

	public SimplePseudoNode(CFGNode node, String taskID) {
		super(node, taskID);
	}

	@Override
	public String getStringVal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void accept(SimpleNodeVisitor visitor, Set<SimpleNode> visited) {
		if(visited.contains(this))
			return;
		visitor.visit(this, visited);
		if(this.node instanceof TailNode)
			return;
		this.getAfterNode(0).accept(visitor, visited);
	}

}
