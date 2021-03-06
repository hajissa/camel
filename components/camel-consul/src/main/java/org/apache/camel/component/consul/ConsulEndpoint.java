/**
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
package org.apache.camel.component.consul;

import com.orbitz.consul.Consul;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.util.ObjectHelper;

/**
 * The camel consul component allows you to work with <a href="https://www.consul.io/">Consul</a>, a distributed, highly available, datacenter-aware, service discovery and configuration system.
 */
@UriEndpoint(firstVersion = "2.18.0", scheme = "consul", title = "Consul", syntax = "consul:apiEndpoint", label = "api,cloud")
public class ConsulEndpoint extends DefaultEndpoint {

    @UriParam(description = "The consul configuration")
    @Metadata
    private final ConsulConfiguration configuration;

    @UriPath(description = "The API endpoint")
    @Metadata(required = "true")
    private final String apiEndpoint;

    private final ProducerFactory producerFactory;
    private final ConsumerFactory consumerFactory;

    private Consul consul;

    public ConsulEndpoint(
            String apiEndpoint,
            String uri,
            ConsulComponent component,
            ConsulConfiguration configuration,
            ProducerFactory producerFactory,
            ConsumerFactory consumerFactory) {

        super(uri, component);

        this.configuration = ObjectHelper.notNull(configuration, "configuration");
        this.apiEndpoint = ObjectHelper.notNull(apiEndpoint, "apiEndpoint");
        this.producerFactory = producerFactory;
        this.consumerFactory = consumerFactory;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Producer createProducer() throws Exception {
        if (producerFactory == null) {
            throw new IllegalArgumentException("No producer for " + apiEndpoint);
        }

        return producerFactory.create(this, configuration);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        if (consumerFactory == null) {
            throw new IllegalArgumentException("No consumer for " + apiEndpoint);
        }

        return consumerFactory.create(this, configuration, processor);
    }

    // *************************************************************************
    //
    // *************************************************************************

    public ConsulConfiguration getConfiguration() {
        return this.configuration;
    }

    public String getApiEndpoint() {
        return this.apiEndpoint;
    }

    public synchronized Consul getConsul() throws Exception {
        if (consul == null) {
            consul = configuration.createConsulClient();
        }

        return consul;
    }

    // *************************************************************************
    //
    // *************************************************************************

    @FunctionalInterface
    public interface ProducerFactory {
        Producer create(ConsulEndpoint endpoint, ConsulConfiguration configuration) throws Exception;
    }

    @FunctionalInterface
    public interface ConsumerFactory {
        Consumer create(ConsulEndpoint endpoint, ConsulConfiguration configuration, Processor processor) throws Exception;
    }
}
