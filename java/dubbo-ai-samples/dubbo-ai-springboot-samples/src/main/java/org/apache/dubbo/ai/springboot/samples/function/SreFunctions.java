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
package org.apache.dubbo.ai.springboot.samples.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.dubbo.ai.core.function.AiFunction;
import org.apache.dubbo.ai.springboot.samples.pojo.MachineInfo;

public class SreFunctions {


    record MachineFuncReq(String machineId) {

    }

    record SubmitProcessReq(String executeCommand) {

    }

    record K8sInfoReq(@JsonProperty(required = true) String k8sNamespace, String k8sContainerName) {

    }

    @AiFunction(value = "getK8sMachineStatus")
    public String getK8sMachineStatus(MachineFuncReq machineFuncReq) {
        double random = Math.random() * 10;
        if (random > 0 && random <= 3) {
            return "running";
        }
        if (random > 3 && random <= 6) {
            return "unhealthy";
        }
        return "removed";
    }

    @AiFunction(value = "获取机器的详情信息")
    public MachineInfo getMachineDetails(MachineFuncReq machineFuncReq) {
        MachineInfo machineInfo = new MachineInfo("ai-service-1-sdsds-vdfg", "k8s-01-hangzhou", "ai-service-1", "cpu四核,使用率:80%", "内存8G使用率80%", "磁盘使用率20%", "");
        System.out.println("get getMachineDetails machineId=" + machineFuncReq.machineId);
        return machineInfo;
    }

    @AiFunction(value = "获取当前K8scontainer信息,必须获取机器详细信息才能拿到k8sNamespace，可以获取到当前container的replicas")
    public String getK8sContainerInfo(K8sInfoReq k8sInfoReq) {
        if (k8sInfoReq.k8sNamespace.equals("k8s-01-hangzhou")) {
            System.out.println("获取当前k8scontainer信息,k8sInfoReq=" + k8sInfoReq);
            return "当前container的数据:4台机器,每台机器的配置是4core 8G";
        }
        return "error on get k8s container info error namespace or container";
    }


    @AiFunction("提交命令到工单系统中。等待人工确认")
    public boolean submitProcess(SubmitProcessReq submitProcessReq) {
        System.out.println("提交执行命令到工单系统中,等人工确认后即可执行，命令为:" + submitProcessReq.executeCommand);
        return true;
    }


}
