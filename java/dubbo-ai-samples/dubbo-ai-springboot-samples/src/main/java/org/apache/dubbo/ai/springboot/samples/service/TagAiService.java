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

@DubboAiService(providerConfigs = "m1")
public interface TagAiService {
    
    
    @Prompt("""
            我们现在有以下几个标签，1.游戏 2.交友 3.引流 4.吃喝 5.其他
            其中，交友和引流的区别是，交友是单纯的一起聊天，引流是某个话题不说完整，吸引用户加好友或者私聊。
            游戏标签一般会提到某个游戏或者游戏平台，比如说王者荣耀，和平精英，steam，epic等。
            请你根据上述标签给下面的文本打标，请你只返回标签对应的数字，只返回一个int的数字
            下面是文本：
            {msg}
            """)
    Integer aiTag(@Val("msg") String msg);
    
    
    
    
}
