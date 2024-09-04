/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.ai.springboot.samples.service;

import org.apache.dubbo.ai.core.DubboAiService;
import org.apache.dubbo.ai.core.Prompt;
import org.apache.dubbo.ai.core.Val;
import org.apache.dubbo.ai.core.function.FunctionCall;
import org.apache.dubbo.ai.springboot.samples.function.SreFunctions;

@DubboAiService(providerConfigs = "m1")
public interface SreAiService {
    
    @Prompt(
            """
               You are a **Kubernetes SRE assistant**. Please provide actionable suggestions based on the following issue: **{msg}**.
                       
                       - **Available Functions**:\s
                          - `getK8sMachineStatus`
                          - `getMachineDetail` can get machine details include `cpu`, `memory`, `disk`, `network` and k8s namespace,container etc.
                          -  `getK8sContainerInfo` can get K8s Container info include replica.
                          
                       
                       - **Tasks**:
                         1. **Analyze the problem**:
                            - If **CPU usage** is abnormally high, execute a `kubectl` command to **expand CPU resources**.
                            - If **memory usage** is abnormally high, execute a `kubectl` command to **expand memory resources**.
                            - If necessary, consider **upscaling the container**.
                         2. **Handle other errors** using your knowledge of Kubernetes (`kubectl` commands) to resolve the issue.
                         3. **Return a summary** of the actions you performed.
                       
                       - **Execution Steps**:
                         - Leverage the provided functions to **gather missing information** if needed.
                         - Submit the appropriate **k8s commands** to address the issue.
                         - **Document** all steps and actions taken.
                       
            """
    )
    @FunctionCall(functionClasses = SreFunctions.class)
    String processProblem(@Val("msg") String input);
    
    
}
