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

package io.gatling.core.javaapi.internal.loop;

import io.gatling.commons.validation.Validation;
import io.gatling.core.javaapi.ChainBuilder;
import io.gatling.core.javaapi.Session;
import io.gatling.core.javaapi.internal.StructureBuilder;
import scala.Function1;

import java.util.UUID;
import java.util.function.Function;

import static io.gatling.core.javaapi.internal.ScalaHelpers.*;

public interface AsLongAs<T extends StructureBuilder<T, W>, W extends io.gatling.core.structure.StructureBuilder<W>> {

  T make(Function<W, W> f);

  // Gatling EL condition
  default Loop<T> asLongAs(String condition) {
    return asLongAs(condition, null);
  }

  default Loop<T> asLongAs(String condition, String counterName) {
    return asLongAs(condition, counterName, false);
  }

  default Loop<T> asLongAs(String condition, boolean exitASAP) {
    return asLongAs(condition, null, exitASAP);
  }

  default Loop<T> asLongAs(String condition, String counterName, boolean exitASAP) {
    return new Loop<>(this, toBooleanExpression(condition), counterName, exitASAP);
  }

  // Function condition
  default Loop<T> asLongAs(Function<Session, Boolean> condition) {
    return asLongAs(condition, null);
  }

  default Loop<T> asLongAs(Function<Session, Boolean> condition, String counterName) {
    return asLongAs(condition, counterName, false);
  }

  default Loop<T> asLongAs(Function<Session, Boolean> condition, boolean exitASAP) {
    return asLongAs(condition, null, exitASAP);
  }

  default Loop<T> asLongAs(Function<Session, Boolean> condition, String counterName, boolean exitASAP) {
    return new Loop<>(this, toUntypedGatlingSessionFunction(condition), counterName, exitASAP);
  }

  final class Loop<T extends StructureBuilder<T, ?>> {
    private final AsLongAs<T, ?> context;
    private final Function1<io.gatling.core.session.Session, Validation<Object>> condition;
    private final String counterName;
    private final boolean exitASAP;

    Loop(AsLongAs<T, ?> context, Function1<io.gatling.core.session.Session, Validation<Object>> condition, String counterName, boolean exitASAP) {
      this.context = context;
      this.condition = condition;
      this.counterName = counterName == null ? UUID.randomUUID().toString() : counterName;
      this.exitASAP = exitASAP;
    }

    public T loop(ChainBuilder chain) {
      return context.make(wrapped -> wrapped.asLongAs(condition, counterName, exitASAP, chain.wrapped));
    }
  }
}
