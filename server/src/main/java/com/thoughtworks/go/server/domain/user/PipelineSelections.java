/*
 * Copyright 2018 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thoughtworks.go.server.domain.user;

import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.PipelineConfig;
import com.thoughtworks.go.config.PipelineConfigs;
import com.thoughtworks.go.domain.PersistentObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

public class PipelineSelections extends PersistentObject implements Serializable {
    public static final int CURRENT_SCHEMA_VERSION = 1;

    public static final PipelineSelections ALL = new PipelineSelections(Filters.defaults(), null, null) {
        @Override
        public boolean includesGroup(PipelineConfigs group) {
            return true;
        }

        @Override
        public boolean includesPipeline(CaseInsensitiveString pipelineName) {
            return true;
        }
    };

    private Long userId;
    private Date lastUpdate;
    private Filters viewFilters = Filters.defaults();
    private int version;

    public PipelineSelections() {
        this(Filters.defaults(), null, null);
    }

    public PipelineSelections(Filters filters, Date date, Long userId) {
        update(filters, date, userId);
    }

    public String getFilters() {
        return Filters.toJson(this.viewFilters);
    }

    public void setFilters(String filters) {
        this.viewFilters = Filters.fromJson(filters);
    }

    public Filters viewFilters() {
        return viewFilters;
    }

    public int version() {
        return version;
    }

    public Date lastUpdated() {
        return lastUpdate;
    }

    public void update(Filters filters, Date date, Long userId) {
        this.userId = userId;
        this.lastUpdate = date;
        this.viewFilters = null == filters ? Filters.defaults() : filters;
        this.version = CURRENT_SCHEMA_VERSION;
    }

    @Deprecated // TODO: remove when removing old dashboard
    public boolean includesGroup(PipelineConfigs group) {
        for (PipelineConfig pipelineConfig : group) {
            if (!includesPipeline(pipelineConfig.name())) {
                return false;
            }
        }
        return true;
    }

    @Deprecated // TODO: remove when removing old dashboard
    public boolean includesPipeline(CaseInsensitiveString pipelineName) {
        return namedFilter(null).isPipelineVisible(pipelineName);
    }

    public DashboardFilter namedFilter(String name) {
        return viewFilters.named(name);
    }

    public Long userId() {
        return userId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Allows pipeline to be visible to entire filter set; generally used as an
     * after-hook on pipeline creation.
     *
     * @param pipelineToAdd - the name of the pipeline
     * @return true if any filters were modified, false if all filters are unchanged
     */
    public boolean ensurePipelineVisible(CaseInsensitiveString pipelineToAdd) {
        boolean modified = false;

        for (DashboardFilter f : viewFilters.filters()) {
            modified = modified || f.allowPipeline(pipelineToAdd);
        }

        return modified;
    }
}
