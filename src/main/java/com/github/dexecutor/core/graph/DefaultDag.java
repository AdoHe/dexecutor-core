/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dexecutor.core.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Default implementation of Graph
 * 
 * @author Nadeem Mohammad
 *
 * @param <T> Type of Node/Task ID
 * @param <R> Type of Node/Task result
 */
public final class DefaultDag<T extends Comparable<T>, R> implements Dag<T, R>, Serializable {

	private static final long serialVersionUID = 1L;

	private Map<T, Node<T, R>> nodes = new HashMap<T, Node<T, R>>();
	
	@Override
	public void addAsDependentOnAllLeafNodes(T nodeValue) {
		if (this.size() == 0) {
			addIndependent(nodeValue);
		} else {
			for (Node<T, R> node : this.getLeafNodes()) {
				addDependency(node.getValue(), nodeValue);
			}
		}
		
	}

	@Override
	public void addAsDependencyToAllInitialNodes(T nodeValue) {
		if (this.size() == 0) {
			addIndependent(nodeValue);
		} else {
			for (Node<T, R> node : this.getInitialNodes()) {
				addDependency(nodeValue, node.getValue());
			}
		}		
	}

	public void addIndependent(final T nodeValue) {
		addOrGet(nodeValue);
	}

	public void addDependency(final T evalFirstNode, final T evalLaterNode) {
		Node<T, R> firstNode = addOrGet(evalFirstNode);
		Node<T, R> afterNode = addOrGet(evalLaterNode);

		addEdges(firstNode, afterNode);
	}

	private void addEdges(final Node<T, R> firstNode, final Node<T, R> afterNode) {
		if (!firstNode.equals(afterNode)) {
			firstNode.addOutGoingNode(afterNode);
			afterNode.addInComingNode(firstNode);			
		}
	}

	private Node<T, R> addOrGet(final T nodeValue) {
		Node<T, R> graphNode = null;
		if (this.nodes.containsKey(nodeValue)) {
			graphNode = this.nodes.get(nodeValue);
		} else {
			graphNode = createNode(nodeValue);
			this.nodes.put(nodeValue, graphNode);
		}
		return graphNode;
	}

	private Node<T, R> createNode(final T value) {
		Node<T, R> node = new Node<T, R>(value);
		return node;
	}

	public Set<Node<T, R>> getInitialNodes() {
		Set<Node<T, R>> initialNodes = new LinkedHashSet<Node<T, R>>();
		for (Entry<T, Node<T, R>> entry : this.nodes.entrySet()) {
			Node<T, R> node = entry.getValue();
			if (node.getInComingNodes().isEmpty()) {				
				initialNodes.add(node);
			}
		}
		return initialNodes;
	}

	public int size() {
		return this.nodes.size();
	}

	public Collection<Node<T, R>> allNodes() {
		return new ArrayList<Node<T, R>>(this.nodes.values());
	}

	public Set<Node<T, R>> getLeafNodes() {
		Set<Node<T, R>> leafNodes = new LinkedHashSet<Node<T, R>>();
		for (Entry<T, Node<T, R>> entry : this.nodes.entrySet()) {
			Node<T, R> node = entry.getValue();
			if (node.getOutGoingNodes().isEmpty()) {				
				leafNodes.add(node);
			}
		}
		return leafNodes;
	}

	@Override
	public Node<T, R> get(final T id) {
		return this.nodes.get(id);
	}
}
