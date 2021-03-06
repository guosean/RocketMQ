/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.alibaba.rocketmq.tools.command.broker;

import com.alibaba.rocketmq.common.protocol.body.KVTable;
import com.alibaba.rocketmq.remoting.RPCHook;
import com.alibaba.rocketmq.tools.admin.DefaultMQAdminExt;
import com.alibaba.rocketmq.tools.command.SubCommand;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * @author shijia.wxr
 */
public class BrokerStatusSubCommand implements SubCommand {

    @Override
    public String commandName() {
        return "brokerStatus";
    }


    @Override
    public String commandDesc() {
        return "Fetch broker runtime status data";
    }


    @Override
    public Options buildCommandlineOptions(Options options) {
        Option opt = new Option("b", "brokerAddr", true, "Broker address");
        opt.setRequired(true);
        options.addOption(opt);

        return options;
    }


    @Override
    public void execute(CommandLine commandLine, Options options, RPCHook rpcHook) {
        DefaultMQAdminExt defaultMQAdminExt = new DefaultMQAdminExt(rpcHook);

        defaultMQAdminExt.setInstanceName(Long.toString(System.currentTimeMillis()));

        try {
            defaultMQAdminExt.start();

            String brokerAddr = commandLine.getOptionValue('b').trim();

            KVTable kvTable = defaultMQAdminExt.fetchBrokerRuntimeStats(brokerAddr);

            TreeMap<String, String> tmp = new TreeMap<String, String>();
            tmp.putAll(kvTable.getTable());

            Iterator<Entry<String, String>> it = tmp.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, String> next = it.next();
                System.out.printf("%-32s: %s\n", next.getKey(), next.getValue());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            defaultMQAdminExt.shutdown();
        }
    }
}
