package com.mysite.service;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;

@JsonIncludeProperties({"content", "totalElements", "totalPages"})
public interface Page<T> extends org.springframework.data.domain.Page<T> {

}
