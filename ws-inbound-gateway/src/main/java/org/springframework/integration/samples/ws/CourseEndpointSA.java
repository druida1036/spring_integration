/*
 * Copyright 2002-2010 the original author or authors.
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

package org.springframework.integration.samples.ws;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.xml.source.DomSourceFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import xpadro.spring.ws.courses.service.CourseService;
import xpadro.spring.ws.courses.service.exception.CourseNotFoundException;
import xpadro.spring.ws.courses.types.Course;
import xpadro.spring.ws.courses.types.GetCourseRequest;
import xpadro.spring.ws.courses.types.GetCourseResponse;

public class CourseEndpointSA {
	@Autowired
	private Jaxb2Marshaller jaxb2Marshaller;
	@Autowired
	private CourseService service;	
	

	public Source issueResponseFor(DOMSource request) {
		GetCourseRequest request1 = (GetCourseRequest) jaxb2Marshaller.unmarshal(request);
		Course course = service.getCourse(request1.getCourseId());
		if (course == null) {
			throw new CourseNotFoundException("course [" + request1.getCourseId() + "] does not exist");
		}
		
		GetCourseResponse response = new GetCourseResponse();
		response.setCourseId(course.getCourseId());
		response.setDescription(course.getDescription());
		response.setName(course.getName());
		response.setSubscriptors(course.getSubscriptors());
		StreamResult  result = new StreamResult(new ByteArrayOutputStream());
		jaxb2Marshaller.marshal(response, result);
		
		return new DomSourceFactory().createSource(result.getOutputStream().toString());
	}
}
