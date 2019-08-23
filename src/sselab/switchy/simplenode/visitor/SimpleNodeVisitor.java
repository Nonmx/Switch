package sselab.switchy.simplenode.visitor;

import java.util.Set;

import sselab.switchy.simplenode.SimpleAPINode;
import sselab.switchy.simplenode.SimpleControlNode;
import sselab.switchy.simplenode.SimpleDeclarationNode;
import sselab.switchy.simplenode.SimpleLoopNode;
import sselab.switchy.simplenode.SimpleNode;
import sselab.switchy.simplenode.SimpleNormalNode;
import sselab.switchy.simplenode.SimplePseudoNode;

public interface SimpleNodeVisitor {
	public int visit(SimpleNode node, Set<SimpleNode> visited);
	public int visit(SimpleAPINode node, Set<SimpleNode> visited);
	public int visit(SimpleLoopNode node, Set<SimpleNode> visited);
	public int visit(SimplePseudoNode node, Set<SimpleNode> visited);
	public int visit(SimpleDeclarationNode node, Set<SimpleNode> visited);
	public int visit(SimpleControlNode node, Set<SimpleNode> visited);
	public int visit(SimpleNormalNode node, Set<SimpleNode> visited);
}
