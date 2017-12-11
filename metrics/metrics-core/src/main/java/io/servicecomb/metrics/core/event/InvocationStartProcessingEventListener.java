/*
 * Copyright 2017 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.servicecomb.metrics.core.event;

import io.servicecomb.core.metrics.InvocationStartProcessingEvent;
import io.servicecomb.foundation.metrics.event.MetricsEvent;
import io.servicecomb.foundation.metrics.event.MetricsEventListener;
import io.servicecomb.metrics.core.EmbeddedMetricsName;
import io.servicecomb.metrics.core.metric.Metric;
import io.servicecomb.metrics.core.metric.MetricFactory;
import io.servicecomb.metrics.core.registry.MetricsRegistry;

public class InvocationStartProcessingEventListener implements MetricsEventListener {
  private final MetricsRegistry registry;

  private final MetricFactory factory;

  public InvocationStartProcessingEventListener(MetricsRegistry registry,
      MetricFactory factory) {
    this.registry = registry;
    this.factory = factory;
  }

  @Override
  public Class<? extends MetricsEvent> getConcernedEvent() {
    return InvocationStartProcessingEvent.class;
  }

  @Override
  public void process(MetricsEvent data) {
    InvocationStartProcessingEvent event = (InvocationStartProcessingEvent) data;

    String countInQueueName = String.format(EmbeddedMetricsName.QUEUE_COUNT_IN_QUEUE, event.getOperationName());
    String lifeTimeInQueueName = String.format(EmbeddedMetricsName.QUEUE_LIFE_TIME_IN_QUEUE, event.getOperationName());

    Metric metric = registry.getMetric(countInQueueName);
    if (metric == null) {
      metric = registry.getOrCreateMetric(factory.createCounter(countInQueueName));
    }
    String instanceCountInQueueName = String.format(EmbeddedMetricsName.QUEUE_COUNT_IN_QUEUE, "instance");
    Metric instanceMetric = registry.getMetric(instanceCountInQueueName);
    metric.update(-1);
    instanceMetric.update(-1);

    metric = registry.getMetric(lifeTimeInQueueName);
    if (metric == null) {
      metric = registry.getOrCreateMetric(factory.createTimer(lifeTimeInQueueName));
    }
    String instanceLifeTimeInQueueName = String.format(EmbeddedMetricsName.QUEUE_LIFE_TIME_IN_QUEUE, "instance");
    instanceMetric = registry.getMetric(instanceLifeTimeInQueueName);

    metric.update(event.getInQueueNanoTime());
    instanceMetric.update(event.getInQueueNanoTime());
  }
}
