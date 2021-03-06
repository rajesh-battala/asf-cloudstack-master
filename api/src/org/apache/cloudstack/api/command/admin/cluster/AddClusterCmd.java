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

package org.apache.cloudstack.api.command.admin.cluster;

import java.util.ArrayList;
import java.util.List;

import com.cloud.exception.InvalidParameterValueException;
import org.apache.cloudstack.api.*;
import org.apache.log4j.Logger;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.BaseCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.api.response.ClusterResponse;
import org.apache.cloudstack.api.response.ListResponse;
import org.apache.cloudstack.api.response.PodResponse;
import org.apache.cloudstack.api.response.ZoneResponse;
import org.apache.log4j.Logger;

import com.cloud.exception.DiscoveryException;
import com.cloud.exception.ResourceInUseException;
import com.cloud.org.Cluster;
import com.cloud.user.Account;

@APICommand(name = "addCluster", description="Adds a new cluster", responseObject=ClusterResponse.class)
public class AddClusterCmd extends BaseCmd {
    public static final Logger s_logger = Logger.getLogger(AddClusterCmd.class.getName());

    private static final String s_name = "addclusterresponse";

    @Parameter(name=ApiConstants.CLUSTER_NAME, type=CommandType.STRING, required=true, description="the cluster name")
    private String clusterName;

    @Parameter(name=ApiConstants.PASSWORD, type=CommandType.STRING, required=false, description="the password for the host")
    private String password;

    @Parameter(name=ApiConstants.POD_ID, type=CommandType.UUID, entityType=PodResponse.class,
            required=true, description="the Pod ID for the host")
    private Long podId;

    @Parameter(name=ApiConstants.URL, type=CommandType.STRING, required=false, description="the URL")
    private String url;

    @Parameter(name=ApiConstants.USERNAME, type=CommandType.STRING, required=false, description="the username for the cluster")
    private String username;

    @Parameter(name=ApiConstants.ZONE_ID, type=CommandType.UUID, entityType=ZoneResponse.class,
            required=true, description="the Zone ID for the cluster")
    private Long zoneId;

    @Parameter(name=ApiConstants.HYPERVISOR, type=CommandType.STRING, required=true, description="hypervisor type of the cluster: XenServer,KVM,VMware,Hyperv,BareMetal,Simulator")
    private String hypervisor;

    @Parameter(name=ApiConstants.CLUSTER_TYPE, type=CommandType.STRING, required=true, description="type of the cluster: CloudManaged, ExternalManaged")
    private String clusterType;

    @Parameter(name=ApiConstants.ALLOCATION_STATE, type=CommandType.STRING, description="Allocation state of this cluster for allocation of new resources")
    private String allocationState;

    @Parameter(name = ApiConstants.VSM_USERNAME, type = CommandType.STRING, required = false, description = "the username for the VSM associated with this cluster")
    private String vsmusername;

    @Parameter(name = ApiConstants.VSM_PASSWORD, type = CommandType.STRING, required = false, description = "the password for the VSM associated with this cluster")
    private String vsmpassword;

    @Parameter(name = ApiConstants.VSM_IPADDRESS, type = CommandType.STRING, required = false, description = "the ipaddress of the VSM associated with this cluster")
    private String vsmipaddress;

    @Parameter (name=ApiConstants.CPU_OVERCOMMIT_RATIO, type = CommandType.STRING, required = false , description = "value of the cpu overcommit ratio, defaults to 1")
    private String  cpuovercommitRatio;

    @Parameter(name = ApiConstants.MEMORY_OVERCOMMIT_RATIO, type = CommandType.STRING, required = false ,description = "value of the default ram overcommit ratio, defaults to 1")
    private String  memoryovercommitratio;

    public String getVSMIpaddress() {
        return vsmipaddress;
    }

    public String getVSMPassword() {
        return vsmpassword;
    }

    public String getVSMUsername() {
        return vsmusername;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getPassword() {
        return password;
    }

    public Long getPodId() {
        return podId;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public String getHypervisor() {
        return hypervisor;
    }

    @Override
    public String getCommandName() {
        return s_name;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String type) {
        this.clusterType = type;
    }

    @Override
    public long getEntityOwnerId() {
        return Account.ACCOUNT_ID_SYSTEM;
    }

    public String getAllocationState() {
        return allocationState;
    }

    public void setAllocationState(String allocationState) {
        this.allocationState = allocationState;
    }

    public Float getCpuOvercommitRatio (){
        if(cpuovercommitRatio != null){
           return Float.parseFloat(cpuovercommitRatio);
        }
        return 1.0f;
    }

    public Float getMemoryOvercommitRaito (){
        if (memoryovercommitratio != null){
            return Float.parseFloat(memoryovercommitratio);
        }
        return 1.0f;
    }

    @Override
    public void execute(){
        try {
            if ((getMemoryOvercommitRaito().compareTo(1f) < 0) | (getCpuOvercommitRatio().compareTo(1f) < 0)) {
                throw new InvalidParameterValueException("Cpu and ram overcommit ratios  should not be less than 1");
            }
            List<? extends Cluster> result = _resourceService.discoverCluster(this);
            ListResponse<ClusterResponse> response = new ListResponse<ClusterResponse>();
            List<ClusterResponse> clusterResponses = new ArrayList<ClusterResponse>();
            if (result != null && result.size() > 0) {
                for (Cluster cluster : result) {
                    ClusterResponse clusterResponse = _responseGenerator.createClusterResponse(cluster, false);
                    clusterResponses.add(clusterResponse);
                }
            } else {
                throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to add cluster");
            }

            response.setResponses(clusterResponses);
            response.setResponseName(getCommandName());

            this.setResponseObject(response);
        } catch (DiscoveryException ex) {
            s_logger.warn("Exception: ", ex);
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, ex.getMessage());
        } catch (ResourceInUseException ex) {
            s_logger.warn("Exception: ", ex);
            ServerApiException e = new ServerApiException(ApiErrorCode.INTERNAL_ERROR, ex.getMessage());
            for (String proxyObj : ex.getIdProxyList()) {
                e.addProxyObject(proxyObj);
            }
            throw e;
        }
    }
}
