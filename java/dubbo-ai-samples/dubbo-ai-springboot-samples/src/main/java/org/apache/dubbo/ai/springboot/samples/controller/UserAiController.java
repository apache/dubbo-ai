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
package org.apache.dubbo.ai.springboot.samples.controller;

import org.apache.dubbo.ai.springboot.samples.pojo.UserInfo;
import org.apache.dubbo.ai.springboot.samples.service.UserStructAiService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAiController {
    
    
    @DubboReference
    private UserStructAiService userStructAiService;
    
    @RequestMapping("/api/user/generate_user")
    public UserInfo getUserInfo(@RequestParam("userText") String userText) {
        return userStructAiService.aiStructMsg(userText);
    }
}
