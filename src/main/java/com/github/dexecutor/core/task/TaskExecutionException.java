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

package com.github.dexecutor.core.task;

/**
 * Tasks can throw this exception to report any issues encountered during task execution
 * 
 * @author Nadeem Mohammad
 *
 */

public class TaskExecutionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new TaskExecutionException.
	 * @param msg the detail message
	 */
	public TaskExecutionException(final String msg) {
		super(msg);
	}

	/**
	 * Create a new TaskExecutionException.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public TaskExecutionException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
