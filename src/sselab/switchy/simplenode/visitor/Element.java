package sselab.switchy.simplenode.visitor;

import java.util.Set;

import sselab.switchy.simplenode.SimpleNode;

public interface Element {
	public void accept(SimpleNodeVisitor visitor, Set<SimpleNode> visited);
}
