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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Set;

import org.junit.Test;

/**
 * 
 * @author Nadeem Mohammad
 *
 */
public class DefaultDagTest {
	
	
	@Test
	public void testAddAsDependencyToAllInitialNodes() {
		Dag<Integer, Integer> graph = new DefaultDag<Integer, Integer>();
		graph.addAsDependencyToAllInitialNodes(1);

		assertThat(graph.size(), equalTo(1));
		graph.addDependency(1, 2);
		graph.addAsDependencyToAllInitialNodes(1);
		assertThat(graph.size(), equalTo(2));
	}

	@Test
	public void testAddAsDependentOnAllLeafNodes() {
		Dag<Integer, Integer> graph = new DefaultDag<Integer, Integer>();
		graph.addAsDependentOnAllLeafNodes(1);
		assertThat(graph.size(), equalTo(1));
		graph.addDependency(1, 2);
		graph.addAsDependentOnAllLeafNodes(1);
		assertThat(graph.size(), equalTo(2));		
	}
	
	@Test
	public void nodeTest() {
		Node<Integer, Integer> intNode = new Node<Integer, Integer>(1);
		assertThat(intNode.equals(null)).isFalse();
		Node<String, String> strNode = new Node<String, String>("");
		assertThat(intNode.equals(strNode)).isFalse();
	}
	
	@Test
	public void testAddSameDependency() {
		Dag<Integer, Integer> graph = new DefaultDag<Integer, Integer>();
		
		graph.addIndependent(1);		
		graph.addDependency(1, 1);
		assertThat(graph.size()).isEqualTo(1);
		
		graph.addDependency(1, 3);
		assertThat(graph.size()).isEqualTo(2);
	}

	@Test
	public void testGraphSize() {
		Dag<Integer, Integer> graph = new DefaultDag<Integer, Integer>();
		
		graph.addIndependent(1);		
		graph.addDependency(1, 2);		
		graph.addIndependent(3);
		graph.addDependency(1, 3);
		assertThat(graph.size()).isEqualTo(3);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInitialNodes() {
		Dag<Integer, Integer> graph = new DefaultDag<Integer, Integer>();
		
		graph.addIndependent(1);		
		graph.addDependency(1, 2);		
		graph.addIndependent(3);
		graph.addDependency(1, 3);
		Set<Node<Integer, Integer>> initialNodes = graph.getInitialNodes();
		
		assertThat(initialNodes.size()).isEqualTo(1);
		assertThat(initialNodes).containsSequence(new Node<Integer, Integer>(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAllNodes() {
		Dag<Integer, Integer> graph = new DefaultDag<Integer, Integer>();
		
		graph.addIndependent(1);		
		graph.addDependency(1, 2);		
		graph.addIndependent(3);
		graph.addDependency(1, 3);
		Collection<Node<Integer, Integer>> initialNodes = graph.allNodes();
		
		assertThat(initialNodes.size()).isEqualTo(3);
		assertThat(initialNodes).containsSequence(new Node<Integer, Integer>(1), new Node<Integer, Integer>(2), new Node<Integer, Integer>(3));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testLeafNodes() {
		Dag<Integer, Integer> graph = new DefaultDag<Integer, Integer>();
		
		graph.addIndependent(1);		
		graph.addDependency(1, 2);		
		graph.addIndependent(3);
		graph.addDependency(1, 3);
		Collection<Node<Integer, Integer>> initialNodes = graph.getLeafNodes();
		
		assertThat(initialNodes.size()).isEqualTo(2);
		assertThat(initialNodes).containsSequence(new Node<Integer, Integer>(2), new Node<Integer, Integer>(3));
	}
}
