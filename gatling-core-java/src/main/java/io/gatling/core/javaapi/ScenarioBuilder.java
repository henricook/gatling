/*
 * Copyright 2011-2021 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gatling.core.javaapi;

import io.gatling.core.action.builder.ActionBuilder;
import io.gatling.core.controller.inject.closed.ClosedInjectionSupport$;
import io.gatling.core.controller.inject.open.OpenInjectionSupport$;
import io.gatling.core.javaapi.internal.StructureBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.gatling.core.javaapi.internal.ScalaHelpers.toScalaSeq;

public final class ScenarioBuilder extends StructureBuilder<ScenarioBuilder, io.gatling.core.structure.ScenarioBuilder> {

  public ScenarioBuilder(String name) {
    this(io.gatling.core.Predef.scenario(name));
  }

  protected ScenarioBuilder(io.gatling.core.structure.ScenarioBuilder wrapped) {
    super(wrapped);
  }

  @Override
  public ScenarioBuilder chain(List<ActionBuilder> newActionBuilders) {
    return new ScenarioBuilder(wrapped.chain(toScalaSeq(newActionBuilders)));
  }

  public PopulationBuilder injectOpen(OpenInjectionStep... steps) {
    List<io.gatling.core.controller.inject.open.OpenInjectionStep> scalaSteps = Arrays.stream(steps).map(OpenInjectionStep::asScala).collect(Collectors.toList());
    return new PopulationBuilder(wrapped.inject(OpenInjectionSupport$.MODULE$.OpenInjectionProfileFactory().profile(toScalaSeq(scalaSteps))));
  }

  public PopulationBuilder injectClosed(ClosedInjectionStep... steps) {
    List<io.gatling.core.controller.inject.closed.ClosedInjectionStep> scalaSteps = Arrays.stream(steps).map(ClosedInjectionStep::asScala).collect(Collectors.toList());
    return new PopulationBuilder(wrapped.inject(ClosedInjectionSupport$.MODULE$.ClosedInjectionProfileFactory().profile(toScalaSeq(scalaSteps))));
  }

  @Override
  public ScenarioBuilder make(Function<io.gatling.core.structure.ScenarioBuilder, io.gatling.core.structure.ScenarioBuilder> f) {
    return new ScenarioBuilder(f.apply(wrapped));
  }
}
