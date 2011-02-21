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

package com.jsmadja.wall.projectwall.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jsmadja.wall.projectwall.builder.HudsonUrlBuilder;
import com.jsmadja.wall.projectwall.builder.TestResultBuilder;
import com.jsmadja.wall.projectwall.domain.HudsonBuild;
import com.jsmadja.wall.projectwall.domain.HudsonProject;
import com.jsmadja.wall.projectwall.generated.hudson.hudsonmodel.HudsonModelHudson;
import com.jsmadja.wall.projectwall.generated.hudson.mavenmoduleset.HudsonMavenMavenModuleSet;
import com.jsmadja.wall.projectwall.generated.hudson.mavenmoduleset.HudsonModelJob;
import com.jsmadja.wall.projectwall.generated.hudson.mavenmoduleset.HudsonModelRun;
import com.jsmadja.wall.projectwall.generated.hudson.mavenmodulesetbuild.HudsonMavenMavenModuleSetBuild;
import com.jsmadja.wall.projectwall.generated.hudson.mavenmodulesetbuild.HudsonModelUser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

public class HudsonService {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonService.class);

    private HudsonUrlBuilder hudsonUrlBuilder;
    private TestResultBuilder hudsonTestService = new TestResultBuilder();

    private Client client;

    public HudsonService(String hudsonUrl) {
        hudsonUrlBuilder = new HudsonUrlBuilder(hudsonUrl);
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses();
        client = buildJerseyClient(clientConfig);

        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize hudson with url "+hudsonUrl);
        }
    }

    /**
     * @return List of all available projects on Hudson
     */
    public final List<HudsonProject> findAllProjects() {
        String projectsUrl = hudsonUrlBuilder.getAllProjectsUrl();
        if (LOG.isInfoEnabled()) {
            LOG.info("All project url : "+projectsUrl);
        }
        WebResource hudsonResource = client.resource(projectsUrl);
        HudsonModelHudson hudson = hudsonResource.get(HudsonModelHudson.class);
        List<HudsonProject> projects = new ArrayList<HudsonProject>();
        List<Object> hudsonProjects = hudson.getJob();
        for(Object project:hudsonProjects) {
            ElementNSImpl element = (ElementNSImpl) project;
            String name = getProjectName(element);
            HudsonProject hudsonProject = findProject(name);
            projects.add(hudsonProject);
        }
        return projects;
    }

    /**
     * @param projectName
     * @param buildNumber
     * @return HudsonBuild found in Hudson with its project name and build number
     */
    public final HudsonBuild findBuild(String projectName, int buildNumber) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Find build with project name ["+projectName+"] and buildNumber ["+buildNumber+"]");
        }

        String buildUrl = hudsonUrlBuilder.getJobUrl(projectName, buildNumber);
        if (LOG.isInfoEnabled()) {
            LOG.info("Build url : "+buildUrl);
        }
        WebResource jobResource = client.resource(buildUrl);
        HudsonMavenMavenModuleSetBuild setBuild = jobResource.get(HudsonMavenMavenModuleSetBuild.class);

        HudsonBuild hudsonBuild = new HudsonBuild();
        hudsonBuild.setDuration(setBuild.getDuration());
        hudsonBuild.setStartTime(new Date(setBuild.getTimestamp()));
        hudsonBuild.setSuccessful(getJobStatus(setBuild));
        hudsonBuild.setCommiters(getCommiters(setBuild));

        String testResultUrl = hudsonUrlBuilder.getTestResultUrl(projectName, buildNumber);
        if (LOG.isInfoEnabled()) {
            LOG.info("Test result : "+testResultUrl);
        }
        WebResource testResultResource = client.resource(testResultUrl);
        hudsonBuild.setTestResult(hudsonTestService.build(testResultResource));

        return hudsonBuild;
    }

    /**
     * @param projectName
     * @return HudsonProject found with its name
     */
    public final HudsonProject findProject(String projectName) {
        String projectUrl = hudsonUrlBuilder.getProjectUrl(projectName);
        if (LOG.isInfoEnabled()) {
            LOG.info("Job url : "+projectUrl);
        }
        WebResource  projectResource = client.resource(projectUrl);
        return createHudsonProject(projectResource);
    }

    /**
     * @param projectName
     * @return Average build duration time computed with old successful jobs
     */
    public final long getAverageBuildDurationTime(String projectName) {
        HudsonProject hudsonProject = findProject(projectName);
        float sumBuildDurationTime = 0;

        int[] successfulBuilds = getSuccessfulBuildNumbers(hudsonProject);
        for (int buildNumber:successfulBuilds) {
            HudsonBuild successfulBuild = findBuild(projectName, buildNumber);
            sumBuildDurationTime += successfulBuild.getDuration();
        }
        long averageTime = (long) (sumBuildDurationTime/successfulBuilds.length);
        if (LOG.isInfoEnabled()) {
            LOG.info("Average build time of "+projectName+" is "+averageTime+" ms");
        }
        return averageTime;
    }

    private String[] getCommiters(HudsonMavenMavenModuleSetBuild setBuild) {
        List<HudsonModelUser> users = setBuild.getCulprit();
        String[] commiters = new String[users.size()];
        for (int i=0; i<users.size(); i++) {
            commiters[i] = users.get(i).getFullName();
        }
        return commiters;
    }

    private boolean getIsBuilding(HudsonModelJob modelJob) {
        String color = modelJob.getColor().value();
        return color.endsWith("_anime");
    }

    private HudsonProject createHudsonProject(WebResource projectResource) {
        HudsonMavenMavenModuleSet modelJob = projectResource.get(HudsonMavenMavenModuleSet.class);

        HudsonProject hudsonProject = new HudsonProject();
        hudsonProject.setName(modelJob.getName());
        hudsonProject.setDescription(modelJob.getDescription());
        hudsonProject.setLastBuildNumber(modelJob.getLastBuild().getNumber());
        hudsonProject.setBuilding(getIsBuilding(modelJob));
        hudsonProject.setArtifactId(modelJob.getModule().get(0).getName());
        hudsonProject.setBuildNumbers(getBuildNumbers(modelJob));
        hudsonProject.setLastBuild(findBuild(hudsonProject.getName(), hudsonProject.getLastBuildNumber()));

        return hudsonProject;
    }

    /**
     * @param hudsonJob
     * @return An array of successful build numbers
     */
    public final int[] getSuccessfulBuildNumbers(HudsonProject hudsonJob) {
        List<Integer> successfulBuildNumbers = new ArrayList<Integer>();
        for (Integer buildNumber:hudsonJob.getBuildNumbers()) {
            HudsonBuild build = findBuild(hudsonJob.getName(), buildNumber);
            if(build.isSuccessful()) {
                successfulBuildNumbers.add(buildNumber);
            }
        }
        int[] successfulBuilds = new int[successfulBuildNumbers.size()];
        for (int i=0; i < successfulBuildNumbers.size(); i++) {
            successfulBuilds[i] = successfulBuildNumbers.get(i);
        }
        return successfulBuilds;
    }

    private int[] getBuildNumbers(HudsonModelJob modelJob) {
        List<HudsonModelRun> builds = modelJob.getBuild();
        int[] buildNumbers = new int[builds.size()];
        for (int i=0; i<builds.size(); i++) {
            buildNumbers[i] = builds.get(i).getNumber();
        }
        return buildNumbers;
    }

    private boolean getJobStatus(HudsonMavenMavenModuleSetBuild job) {
        ElementNSImpl element = (ElementNSImpl) job.getResult();
        return "SUCCESS".equals(element.getFirstChild().getNodeValue());
    }

    private String getProjectName(ElementNSImpl element) {
        return element.getFirstChild().getFirstChild().getNodeValue();
    }

    /**
     * @param projectName
     * @return Date which we think the project will finish to build
     */
    public final Date getEstimatedFinishTime(String projectName) {
        HudsonProject project = findProject(projectName);
        HudsonBuild lastBuild = project.getLastBuild();
        Date startTime = lastBuild.getStartTime();
        long averageBuildDurationTime = getAverageBuildDurationTime(projectName);
        DateTime estimatedFinishTime = new DateTime(startTime.getTime()).plus(averageBuildDurationTime);

        if (LOG.isInfoEnabled()) {
            LOG.info("Estimated finish time of project "+projectName+" is "+estimatedFinishTime+" ms");
        }

        return estimatedFinishTime.toDate();
    }

    Client buildJerseyClient(ClientConfig clientConfig) {
        return Client.create(clientConfig);
    }

}