/*
 * Copyright (c) JForum Team. All rights reserved.
 *
 * The software in this package is published under the terms of the LGPL
 * license a copy of which has been included with this distribution in the
 * license.txt file.
 *
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.services;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Ranking;
import net.jforum.repository.RankingRepository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class RankingServiceTestCase {

	@Mock private RankingRepository repository;
	@InjectMocks private RankingService service;

	@Test
	public void specialRankingShouldNotHaveMinPass10ShouldForceToZero() {
		Ranking r = new Ranking(); r.setTitle("r1"); r.setSpecial(true); r.setMin(10);
		
		service.add(r);

		verify(repository).add(notNull(Ranking.class));
		Assert.assertEquals(0, r.getMin());
	}

	@Test(expected = ValidationException.class)
	public void minValueShouldNotBeLessThan1ExpectsValidationException() {
		Ranking r = new Ranking(); r.setTitle("r1"); r.setSpecial(false); r.setMin(0);
		service.add(r);
	}

	@Test
	public void deleteUsingNullIdsShouldIgnore() {
		service.delete(null);

		verifyZeroInteractions(repository);
	}

	@Test
	public void delete() {
		when(repository.get(1)).thenReturn(new Ranking());
		when(repository.get(2)).thenReturn(new Ranking());

		service.delete(1, 2);

		verify(repository, times(2)).remove(notNull(Ranking.class));
	}

	@Test
	public void addExpectSuccess() {
		Ranking r = new Ranking(); r.setTitle("r1"); r.setMin(1);

		service.add(r);

		verify(repository).add(notNull(Ranking.class));
	}

	@Test(expected = ValidationException.class)
	public void addWithIdBiggerThanZeroExpectsValidationException() {
		Ranking r = new Ranking(); r.setTitle("r1"); r.setId(1); r.setMin(1);
		service.add(r);
	}

	@Test(expected = ValidationException.class)
	public void updateWithoutAnIdExpectsException() {
		Ranking r = new Ranking(); r.setTitle("r1"); r.setMin(1); r.setId(0);
		service.update(r);
	}

	@Test
	public void updateExpectSuccess() {
		Ranking r = new Ranking(); r.setId(1); r.setTitle("r1"); r.setMin(1);

		service.update(r);

		verify(repository).update(notNull(Ranking.class));
	}

	@Test(expected = NullPointerException.class)
	public void addUsingNullExpectsNPE() {
		service.add(null);
	}

	@Test(expected = ValidationException.class)
	public void addWithoutTitleExpectsValidationException() {
		service.add(new Ranking());
	}

	@Test(expected = NullPointerException.class)
	public void updateUsingNullExpectsNPE() {
		service.update(null);
	}

	@Test(expected = ValidationException.class)
	public void updateWithoutTitleExpectsValidationException() {
		Ranking r = new Ranking(); r.setTitle(null); r.setMin(1); r.setId(1);
		service.add(r);
	}
}
