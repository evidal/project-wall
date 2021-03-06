/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com> - Arnaud LEMAIRE
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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.20 at 04:00:50 PM CET 
//


package net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for hudson.tasks.test.AbstractTestResultAction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="hudson.tasks.test.AbstractTestResultAction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="failCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="skipCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="totalCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="urlName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hudson.tasks.test.AbstractTestResultAction", propOrder = {
    "failCount",
    "skipCount",
    "totalCount",
    "urlName"
})
@XmlSeeAlso({
    HudsonTasksTestAggregatedTestResultAction.class
})
public class HudsonTasksTestAbstractTestResultAction {

    protected int failCount;
    protected int skipCount;
    protected int totalCount;
    protected String urlName;

    /**
     * Gets the value of the failCount property.
     * 
     */
    public int getFailCount() {
        return failCount;
    }

    /**
     * Sets the value of the failCount property.
     * 
     */
    public void setFailCount(int value) {
        this.failCount = value;
    }

    /**
     * Gets the value of the skipCount property.
     * 
     */
    public int getSkipCount() {
        return skipCount;
    }

    /**
     * Sets the value of the skipCount property.
     * 
     */
    public void setSkipCount(int value) {
        this.skipCount = value;
    }

    /**
     * Gets the value of the totalCount property.
     * 
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * Sets the value of the totalCount property.
     * 
     */
    public void setTotalCount(int value) {
        this.totalCount = value;
    }

    /**
     * Gets the value of the urlName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlName() {
        return urlName;
    }

    /**
     * Sets the value of the urlName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlName(String value) {
        this.urlName = value;
    }

}
