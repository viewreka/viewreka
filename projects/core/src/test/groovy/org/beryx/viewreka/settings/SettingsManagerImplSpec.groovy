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
