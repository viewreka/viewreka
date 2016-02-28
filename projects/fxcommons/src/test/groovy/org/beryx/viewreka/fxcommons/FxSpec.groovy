/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.viewreka.fxcommons

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import org.testfx.framework.junit.ApplicationTest
import spock.lang.Specification

abstract class FxSpec extends Specification {
    ApplicationTest fx

    void setupStage(Closure<Parent> rootNodeFactory) {
        fx = new GuiTestMixin(rootNodeFactory)
        fx.internalBefore()
    }

    static class GuiTestMixin extends ApplicationTest {
        final Closure<Parent> rootNodeFactory

        def GuiTestMixin(Closure<Parent> rootNodeFactory) {
            this.rootNodeFactory = rootNodeFactory
        }

        protected Parent getRootNode(stage) {
            return rootNodeFactory.call(stage) as Parent
        }

        @Override
        public void start(Stage stage) {
            Scene scene = new Scene(getRootNode(stage))
            stage.scene = scene
            stage.show()
        }
    }
}
