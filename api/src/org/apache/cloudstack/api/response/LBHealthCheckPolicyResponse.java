// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package org.apache.cloudstack.api.response;

import com.cloud.network.rules.HealthCheckPolicy;
import com.cloud.network.rules.StickinessPolicy;
import com.cloud.serializer.Param;
import com.cloud.utils.Pair;
import com.google.gson.annotations.SerializedName;
import org.apache.cloudstack.api.BaseResponse;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class LBHealthCheckPolicyResponse extends BaseResponse {
    @SerializedName("id")
    @Param(description = "the LB HealthCheck policy ID")
    private String id;

    /*@SerializedName("name")
    @Param(description = "the name of the Stickiness policy")
    private String name;

    @SerializedName("methodname")
    @Param(description = "the method name of the Stickiness policy")
    private String methodName;

    @SerializedName("description")
    @Param(description = "the description of the Stickiness policy")
    private String description;;

    @SerializedName("state")
    @Param(description = "the state of the policy")
    private String state;
    
    @SerializedName("responsetime")
    @Param(description = "response time of the healthcheck")
    private int responseTime;
    
    @SerializedName("healthcheckinterval")
    @Param(description = "healthcheck interval response time of the healthcheck")
    private double healthcheckInterval;
    
    @SerializedName("healthcheckthresshold")
    @Param(description = "healthcheck interval response time of the healthcheck")
    private double healthcheckthresshold;
    
    @SerializedName("unhealthcheckthresshold")
    @Param(description = "Unhealthcheck interval response time of the healthcheck")
    private double unhealthcheckthresshold;
*/


    public void setId(String id) {
        this.id = id;
    }

/*    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
*/
    public LBHealthCheckPolicyResponse(HealthCheckPolicy healthcheckpolicy) {
        /*this.description = healthcheckpolicy.getDescription();
        if (healthcheckpolicy.isRevoke()) {
            this.setState("Revoked");
        }*/
        if (healthcheckpolicy.getUuid() != null )
            setId(healthcheckpolicy.getUuid());

        /*this.healthcheckInterval = healthcheckpolicy.getHealthcheckInterval();
        this.responseTime  = healthcheckpolicy.getResponseTime();
        this.healthcheckthresshold = healthcheckpolicy.getHealthcheckThresshold(); 
        this.unhealthcheckthresshold = healthcheckpolicy.getUnhealthThresshold(); */
        setObjectName("healthcheckpolicy");
    }
}
