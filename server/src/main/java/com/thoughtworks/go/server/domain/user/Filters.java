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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.config.CaseInsensitiveString;

import java.util.*;

import static com.thoughtworks.go.server.domain.user.Marshaling.*;

public class Filters {
    private static final Gson GSON = new GsonBuilder().
            registerTypeAdapter(Filters.class, new FiltersDeserializer()).
            registerTypeAdapter(Filters.class, new FiltersSerializer()).
            registerTypeAdapter(DashboardFilter.class, new DashboardFilterDeserializer()).
            registerTypeAdapter(DashboardFilter.class, new DashboardFilterSerializer()).
            registerTypeAdapter(CaseInsensitiveString.class, new CaseInsensitiveStringDeserializer()).
            registerTypeAdapter(CaseInsensitiveString.class, new CaseInsensitiveStringSerializer()).
            create();

    public static final DashboardFilter WILDCARD_FILTER = new BlacklistFilter(null, Collections.emptyList()) {
        @Override
        public boolean isPipelineVisible(CaseInsensitiveString pipeline) {
            return true; // optimization
        }
    };

    public static Filters fromJson(String json) {
        final Filters filters = GSON.fromJson(json, Filters.class);
        filters.updateIndex();
        return filters;
    }

    public static String toJson(Filters filters) {
        return GSON.toJson(filters);
    }

    public static Filters single(DashboardFilter filter) {
        return new Filters(Collections.singletonList(filter));
    }

    public static Filters defaults() {
        return single(WILDCARD_FILTER);
    }

    private List<DashboardFilter> filters;
    private Map<String, DashboardFilter> filterMap; // optimize for find by name

    public Filters(List<DashboardFilter> filters) {
        this.filters = filters;
        updateIndex();
    }

    public DashboardFilter named(String name) {
        if (null == name) name = "";
        return this.filterMap.getOrDefault(name, WILDCARD_FILTER);
    }

    public List<DashboardFilter> filters() {
        return Collections.unmodifiableList(filters);
    }

    private void updateIndex() {
        this.filterMap = new HashMap<>();
        this.filters.forEach((f) -> filterMap.put(null != f.name() ? f.name() : "", f));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filters that = (Filters) o;
        return Objects.equals(filters, that.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filters);
    }
}
