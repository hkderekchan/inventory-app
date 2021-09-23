package com.mysite.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
abstract class AbstractJpaTest {

	@PersistenceContext
	protected EntityManager em;

	protected void expectConstraint(final ConstraintViolationException ex,
			final String field, final String constraint) {
		final Stream<ConstraintViolation<?>> violations = ex
				.getConstraintViolations().stream().filter(c -> {
					return c.getPropertyPath().toString().contains(field)
							&& c.getMessageTemplate().contains(constraint);
				});
		assertEquals(1, violations.count());
	}

	protected void flushAndClear() {
		this.em.flush();
		this.em.clear();
	}
}
