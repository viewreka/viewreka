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
package org.beryx.viewreka.settings

import spock.lang.Specification

class SettingsManagerImplSpec extends Specification{
    static class SettingsMap extends HashMap implements Settings {}
    def createManager(File file) {
        return new SettingsManagerImpl<SettingsMap>(file.parent, file.name) {
            @Override
            protected SettingsMap createNewSettings() {
                return [:]
            }
        }
    }

    def "should save and retrieve settings"() {
        given:
        def tmpFile = File.createTempFile("settings",".xml")
        tmpFile.deleteOnExit()
        tmpFile.delete()
        def currDate = new Date()

        when:
        def manager1 = createManager(tmpFile)
        def settings1 = manager1.settings
        settings1.myInt = 555
        settings1.myList = [5.0d, 'abc', null, currDate]
        settings1.myMap = [a:1, b:2, c:3]
        manager1.saveSettings()
        settings1.clear()

        def manager2 = createManager(tmpFile)
        def settings2 = manager2.settings

        then:
        settings1.isEmpty()
        settings2.myInt == 555
        settings2.myList == [5.0, 'abc', null, currDate]
        settings2.myMap == [a:1, b:2, c:3]

        when:
        settings2.clear()
        manager2.saveSettings()
        def manager3 = createManager(tmpFile)
        def settings3 = manager3.settings

        then:
        settings2.isEmpty()
        settings3.isEmpty()
    }
}
