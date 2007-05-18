/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.googlecode.jsonplugin.smd;

import java.util.ArrayList;
import java.util.List;

public class SMDMethod {
    private String name;
    private List<SMDMethodParameter> parameters = new ArrayList<SMDMethodParameter>();

    public SMDMethod(String name) {
        this.name = name;
    }

    public void addSMDMethodParameter(SMDMethodParameter parameter) {
        this.parameters.add(parameter);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SMDMethodParameter> getParameters() {
        return parameters;
    }
}