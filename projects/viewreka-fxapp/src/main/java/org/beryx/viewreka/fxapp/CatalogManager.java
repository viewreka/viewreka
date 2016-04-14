/**
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
package org.beryx.viewreka.fxapp;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.beryx.viewreka.bundle.repo.CatalogCache;
import org.beryx.viewreka.bundle.repo.CatalogRepo;
import org.beryx.viewreka.bundle.repo.H2CatalogCache;
import org.beryx.viewreka.fxui.settings.GuiSettings;
import org.beryx.viewreka.sql.embedded.H2RelocatedDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CatalogManager {
    public static final String DEFAULT_CATALOG_CACHE = System.getProperty("user.home") + "/.viewreka/cache/catalog";
    public static final String PROP_CATALOG_CACHE = "repo.catalog.cache";
    public static final String PROP_CATALOG_URLS = "repo.catalog.urls";
    public static final String DEFAULT_CATALOG_URL  = "http://viewreka-bundles.beryx.org/json";

    private static final Logger log = LoggerFactory.getLogger(CatalogManager.class);


    private final GuiSettings settings;

    public CatalogManager(GuiSettings settings) {
        this.settings = settings;
    }

    public CatalogCache getCatalogCache() {
        String cacheDbPath = settings.getProperty(PROP_CATALOG_CACHE, DEFAULT_CATALOG_CACHE, false);
        return new H2CatalogCache(new H2RelocatedDB(cacheDbPath, "", "").withDefaultCreateAndDeleteStrategy());
    }

    public Collection<String> getCatalogUrls() {
        String[] urlArray = settings.getProperty(PROP_CATALOG_URLS, new String[]{DEFAULT_CATALOG_URL}, false);
        Set<String> catalogUrls = new HashSet<>(Arrays.asList(urlArray));
        settings.setProperty(PROP_CATALOG_URLS, catalogUrls.toArray(new String[catalogUrls.size()]));
        return catalogUrls;
    }

    private class CacheUpdateService extends ScheduledService<Void> {
        private final Duration normalUpdatePeriod;
        private final Duration postFailurePeriod;
        private Duration nextPeriod;

        public CacheUpdateService(Duration normalUpdatePeriod, Duration postFailurePeriod) {
            this.normalUpdatePeriod = normalUpdatePeriod;
            this.postFailurePeriod = postFailurePeriod;
            this.nextPeriod = normalUpdatePeriod;
            setOnSucceeded(ev -> setPeriod(nextPeriod));
        }

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() {
                    try {
                        refreshUrls();
                        nextPeriod = normalUpdatePeriod;
                    } catch (Exception e) {
                        log.error("CacheUpdateService failed.", e);
                        nextPeriod = postFailurePeriod;
                    }
                    log.info("Next refresh in {}.", nextPeriod);
                    return null;
                }
            };
        }

        private void refreshUrls() throws Exception {
            for(String url : getCatalogUrls()) {
                CatalogRepo repo = new CatalogRepo(url, getCatalogCache());
                repo.requestRefresh();
                repo.getEntries();
            };
            log.info("CacheUpdateService successfully refreshed {} URLs.", getCatalogUrls().size());
        }
    }
    public ScheduledService<Void> startCacheUpdateThread(Duration normalUpdatePeriod, Duration postFailurePeriod) {
        CacheUpdateService svc = new CacheUpdateService(normalUpdatePeriod, postFailurePeriod);
        svc.start();
        return svc;
    }
}
