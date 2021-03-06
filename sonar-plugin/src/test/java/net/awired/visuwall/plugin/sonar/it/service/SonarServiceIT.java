/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.plugin.sonar.it.service;

import static net.awired.visuwall.plugin.sonar.it.IntegrationTestData.SONAR_URL;
import static net.awired.visuwall.plugin.sonar.it.IntegrationTestData.STRUTS_ARTIFACT_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map.Entry;
import java.util.Set;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.plugin.sonar.service.SonarService;

import org.junit.BeforeClass;
import org.junit.Test;


public class SonarServiceIT {

    private static SonarService sonarService;

    @BeforeClass
    public static void init() {
        sonarService = new SonarService();
        sonarService.setUrl(SONAR_URL);
        sonarService.init();
    }

    @Test
    public void should_populate_quality() {
        Project project = new Project();
        project.setId(STRUTS_ARTIFACT_ID);
        QualityResult quality = new QualityResult();
        sonarService.populateQuality(project, quality, "violations_density");
        QualityMeasure measure = quality.getMeasure("violations_density");
        assertEquals("77.2%", measure.getFormattedValue());
        assertEquals(77.2, measure.getValue(), 0);
    }

    @Test
    public void should_have_a_lot_of_quality_metrics() {
        Project project = new Project();
        project.setId(STRUTS_ARTIFACT_ID);
        QualityResult quality = new QualityResult();
        sonarService.populateQuality(project, quality);
        Set<Entry<String, QualityMeasure>> measures = quality.getMeasures();
        for (Entry<String, QualityMeasure> measure : measures) {
            assertNotNull(measure.getValue().getValue());
        }
    }

    @Test
    public void should_not_fail_if_measure_does_not_exist() throws ProjectNotFoundException {
        Project project = new Project();
        project.setId(STRUTS_ARTIFACT_ID);
        QualityResult quality = new QualityResult();
        sonarService.populateQuality(project, quality, "inexistant_measure");
    }
}
