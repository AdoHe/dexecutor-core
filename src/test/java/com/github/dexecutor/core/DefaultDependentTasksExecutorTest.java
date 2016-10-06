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

package com.github.dexecutor.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.dexecutor.core.graph.Dag;
import com.github.dexecutor.core.graph.Node;
import com.github.dexecutor.core.support.ThreadPoolUtil;
import com.github.dexecutor.core.task.ExecutionResult;
import com.github.dexecutor.core.task.Task;
import com.github.dexecutor.core.task.TaskExecutionException;
import com.github.dexecutor.core.task.TaskProvider;

import mockit.Deencapsulation;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;

/**
 * 
 * @author Nadeem Mohammad
 *
 */
@RunWith(JMockit.class)
public class DefaultDependentTasksExecutorTest {

	@Test
	public void testAddAsDependencyToAllInitialNodes() {
		new MockedCompletionService();
		DefaultDependentTasksExecutor<Integer, Integer> executor = newTaskExecutor(false);
		executor.addAsDependencyToAllInitialNodes(1);
		Dag<Integer, Integer> graph = Deencapsulation.getField(executor, "graph");
		assertThat(graph.size(), equalTo(1));
		executor.addDependency(1, 2);
		executor.addAsDependencyToAllInitialNodes(1);
		assertThat(graph.size(), equalTo(2));
	}

	@Test
	public void testAddAsDependentOnAllLeafNodes() {
		new MockedCompletionService();
		DefaultDependentTasksExecutor<Integer, Integer> executor = newTaskExecutor(false);
		executor.addAsDependentOnAllLeafNodes(1);
		Dag<Integer, Integer> graph = Deencapsulation.getField(executor, "graph");
		assertThat(graph.size(), equalTo(1));
		executor.addDependency(1, 2);
		executor.addAsDependentOnAllLeafNodes(1);
		assertThat(graph.size(), equalTo(2));
	}

	@Test
	public void testPrint() {
		new MockedCompletionService();
		DefaultDependentTasksExecutor<Integer, Integer> executor = newTaskExecutor(false);
		executor.addDependency(1, 2);
		StringWriter writer = new StringWriter();
		executor.print(writer);
		assertThat(writer.toString(), equalTo("Path #0\n1[] \n2[1] \n\n"));
	}

	@Test
	public void testDependentTaskExecutionOrderWithOutException() {

		new MockedCompletionService();

		DefaultDependentTasksExecutor<Integer, Integer> executor = newTaskExecutor(false);

		addDependencies(executor);

		executor.execute(new ExecutionConfig().immediateRetrying(1));

		Collection<Node<Integer, Integer>> processedNodesOrder = Deencapsulation.getField(executor, "processedNodes");

		assertThat(processedNodesOrder, equalTo(executionOrderExpectedResult()));
	}

	@Test
	public void testNonTerminatingDependentTaskExecutionOrderWithOutException() {

		new MockedCompletionService();

		DefaultDependentTasksExecutor<Integer, Integer> executor = newTaskExecutor(false);

		addDependencies(executor);

		executor.execute(ExecutionConfig.NON_TERMINATING);

		Collection<Node<Integer, Integer>> processedNodesOrder = Deencapsulation.getField(executor, "processedNodes");

		assertThat(processedNodesOrder, equalTo(executionOrderExpectedResult()));
	}

	@Test
	public void testNotTerminatingRetryingDependentTaskExecutionOrderWithException() {

		new MockedCompletionService();

		DefaultDependentTasksExecutor<Integer, Integer> executor = newTaskExecutor(true);

		addDependencies(executor);

		executor.execute(ExecutionConfig.NON_TERMINATING);

		Collection<Node<Integer, Integer>> processedNodesOrder = Deencapsulation.getField(executor, "processedNodes");

		assertThat(processedNodesOrder, equalTo(executionOrderExpectedResult()));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowExectionRunningTeminatedExecutor() {

		new MockedCompletionService();

		final DefaultDependentTasksExecutor<Integer, Integer> executor = newTaskExecutor(false);

		addDependencies(executor);

		executor.execute(new ExecutionConfig().scheduledRetrying(3, new Duration(1, TimeUnit.SECONDS)));
		executor.execute(ExecutionConfig.TERMINATING);
	}

	private void addDependencies(DefaultDependentTasksExecutor<Integer, Integer> executor) {
		executor.addDependency(1, 2);
		executor.addDependency(1, 2);
		executor.addDependency(1, 3);
		executor.addDependency(3, 4);
		executor.addDependency(3, 5);
		executor.addDependency(3, 6);
		executor.addDependency(2, 7);
		executor.addDependency(2, 9);
		executor.addDependency(2, 8);
		executor.addDependency(9, 10);
		executor.addDependency(12, 13);
		executor.addDependency(13, 4);
		executor.addDependency(13, 14);
		executor.addIndependent(11);
	}

	private Collection<Node<Integer, Integer>> executionOrderExpectedResult() {
		List<Node<Integer, Integer>> result = new ArrayList<Node<Integer, Integer>>();
		result.add(new Node<Integer, Integer>(1));
		result.add(new Node<Integer, Integer>(11));
		result.add(new Node<Integer, Integer>(12));
		result.add(new Node<Integer, Integer>(2));
		result.add(new Node<Integer, Integer>(3));
		result.add(new Node<Integer, Integer>(13));
		result.add(new Node<Integer, Integer>(7));
		result.add(new Node<Integer, Integer>(9));
		result.add(new Node<Integer, Integer>(8));
		result.add(new Node<Integer, Integer>(5));
		result.add(new Node<Integer, Integer>(6));
		result.add(new Node<Integer, Integer>(4));
		result.add(new Node<Integer, Integer>(14));
		result.add(new Node<Integer, Integer>(10));
		return result;
	}

	private DefaultDependentTasksExecutor<Integer, Integer> newTaskExecutor(boolean throwEx) {
		ExecutionEngine<Integer, Integer> engine = new DefaultExecutionEngine<Integer, Integer>(newExecutor());
		return new DefaultDependentTasksExecutor<Integer, Integer>(engine, new DummyTaskProvider(throwEx));
	}

	private ExecutorService newExecutor() {
		return Executors.newFixedThreadPool(ThreadPoolUtil.ioIntesivePoolSize());
	}

	private static class DummyTaskProvider implements TaskProvider<Integer, Integer> {
		private boolean throwEx;

		public DummyTaskProvider(boolean throwEx) {
			this.throwEx = throwEx;
		}

		public Task<Integer, Integer> provideTask(final Integer id) {

			return new Task<Integer, Integer>() {

				private static final long serialVersionUID = 1L;

				public Integer execute() {
					shouldConsiderExecutionError();
					doExecute(id);
					return id;
				}

				private void doExecute(final Integer id) {
					if (throwEx) {
						if (id == 2) {
							throw new TaskExecutionException("Error Executing task " + id);
						}
					}
				}
				
			};
		}
	}

	private static class MockedCompletionService
			extends MockUp<ExecutorCompletionService<ExecutionResult<Integer, Integer>>> {
		List<Callable<ExecutionResult<Integer, Integer>>> nodes = new ArrayList<Callable<ExecutionResult<Integer, Integer>>>();
		int index = 0;

		@Mock
		public void $init(Executor executor) {

		}

		@Mock
		public void submit(Callable<ExecutionResult<Integer, Integer>> task) {
			nodes.add(task);
		}

		@Mock
		public Future<ExecutionResult<Integer, Integer>> take() throws InterruptedException {
			return new Future<ExecutionResult<Integer, Integer>>() {

				public boolean isDone() {
					return false;
				}

				public boolean isCancelled() {
					return false;
				}

				public boolean cancel(boolean mayInterruptIfRunning) {
					return false;
				}

				public ExecutionResult<Integer, Integer> get(long timeout, TimeUnit unit)
						throws InterruptedException, ExecutionException, TimeoutException {
					return doGet();
				}

				public ExecutionResult<Integer, Integer> get() throws InterruptedException, ExecutionException {
					return doGet();
				}

				private ExecutionResult<Integer, Integer> doGet() {
					try {
						Callable<ExecutionResult<Integer, Integer>> callable = nodes.get(index);
						if (callable == null) {
							throw new RuntimeException("Node is null");
						}
						ExecutionResult<Integer, Integer> call = callable.call();
						index++;
						return call;
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}

			};
		}
	}
}
