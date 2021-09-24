package com.mysite.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

abstract class AbstractValidationTest {

	@Autowired
	private MockMvc mockMvc;

	protected ObjectMapper mapper = new ObjectMapper();
	
	protected void assertBadRequest(final MockHttpServletRequestBuilder req) throws Exception {
		this.mockMvc.perform(req).andExpect(status().isBadRequest());
	}
	
}
