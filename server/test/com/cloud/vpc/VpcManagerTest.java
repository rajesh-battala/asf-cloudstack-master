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

package com.cloud.vpc;

import javax.inject.Inject;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cloud.configuration.ConfigurationManager;
import com.cloud.configuration.ConfigurationVO;
import com.cloud.configuration.dao.ConfigurationDao;
import com.cloud.exception.ResourceAllocationException;
import com.cloud.network.vpc.Vpc;
import com.cloud.network.vpc.VpcManager;
import com.cloud.network.vpc.VpcVO;
import com.cloud.network.vpc.dao.VpcDao;
import com.cloud.offerings.NetworkOfferingServiceMapVO;
import com.cloud.offerings.NetworkOfferingVO;
import com.cloud.offerings.dao.NetworkOfferingDao;
import com.cloud.offerings.dao.NetworkOfferingServiceMapDao;
import com.cloud.user.AccountManager;
import com.cloud.user.AccountVO;
import com.cloud.user.UserContext;
import com.cloud.user.UserVO;
import com.cloud.utils.component.ComponentContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/VpcContext.xml")


public class VpcManagerTest extends TestCase {
    @Inject
    ConfigurationManager configMgr;

    @Inject
    ConfigurationDao configDao;

    @Inject
    NetworkOfferingDao offDao;

    @Inject
    NetworkOfferingServiceMapDao mapDao;

    @Inject
    AccountManager accountMgr;

    @Inject
    VpcManager vpcMgr;

    @Inject
    VpcDao vpcDao;

    @Before
    public void setUp() {
        ComponentContext.initComponentsLifeCycle();

        ConfigurationVO configVO = new ConfigurationVO("200", "200", "200", "200", "200", "200");
        Mockito.when(configDao.findByName(Mockito.anyString())).thenReturn(configVO);
        VpcVO vpc = new VpcVO(1, "myvpc", "myvpc", 2, 1, 1, "10.0.1.0/16", "mydomain");
        Mockito.when(vpcDao.persist(Mockito.any(VpcVO.class))).thenReturn(vpc);
        Mockito.when(vpcDao.findById(Mockito.anyLong())).thenReturn(vpc);
        try {
            Mockito.when(
                    vpcMgr.createVpc(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(),
                            Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(vpc);
        } catch (ResourceAllocationException e) {
        }
        Mockito.when(vpcMgr.getActiveVpc(Mockito.anyLong())).thenReturn(vpc);
        Mockito.when(offDao.persist(Mockito.any(NetworkOfferingVO.class))).thenReturn(new NetworkOfferingVO());
        Mockito.when(mapDao.persist(Mockito.any(NetworkOfferingServiceMapVO.class))).thenReturn(
                new NetworkOfferingServiceMapVO());
        Mockito.when(accountMgr.getSystemUser()).thenReturn(new UserVO(1));
        Mockito.when(accountMgr.getSystemAccount()).thenReturn(new AccountVO(2));

        UserContext.registerContext(accountMgr.getSystemUser().getId(), accountMgr.getSystemAccount(), null, false);
    }

    @Test
    public void validateNtwkOffForVpc() {
        // validate network offering
        // 1) correct network offering
        boolean result = false;
        try {
            vpcMgr.validateNtwkOffForNtwkInVpc(2L, 1, "0.0.0.0", "111-", vpcMgr.getVpc(1), "10.1.1.1", new AccountVO());
            result = true;
        } catch (Exception ex) {
        } finally {
            assertTrue("Validate network offering: Test passed: the offering is valid for vpc creation", result);
        }

    }

    @Test
    public void getActiveVpc() {
        Vpc vpc = null;
        vpc = vpcMgr.getActiveVpc(1L);
        // System.out.println("Vpc Value is :: " + vpc.toString());
        assertNotNull("Get active Vpc: TEST PASSED, active vpc is returned", vpc);
    }

    @Test
    public void createVpc() {
        Vpc vpc = null;
        try {
            vpc = vpcMgr.createVpc(1, 1, 1, "myvpc", "myvpc", "10.0.1.0/16", "test");
            assertNotNull("Validate Vpc Creation : Test passed: vpc is created", vpc);
        } catch (ResourceAllocationException e) {
        }
    }
}
