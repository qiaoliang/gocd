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

public interface DashboardFilter {
    String name();

    boolean isPipelineVisible(CaseInsensitiveString pipeline);

    /**
     * Idempotent operation on filter to allow a specified pipeline to be visible
     *
     * @param pipeline - the name of the pipeline
     * @return true if the filter was modified, false if unchanged
     */
    boolean allowPipeline(CaseInsensitiveString pipeline);
}
