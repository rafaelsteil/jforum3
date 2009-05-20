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

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Ranking;
import net.jforum.repository.RankingRepository;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class RankingServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private RankingRepository repository = context.mock(RankingRepository.class);
	private RankingService service = new RankingService(repository);

	@Test
	public void specialRankingShouldNotHaveMinPass10ShouldForceToZero() {
		context.checking(new Expectations() {{
			one(repository).add(with(aNonNull(Ranking.class)));
		}});

		Ranking r = new Ranking(); r.setTitle("r1"); r.setSpecial(true); r.setMin(10);
		service.add(r);
		context.assertIsSatisfied();
		Assert.assertEquals(0, r.getMin());
	}

	@Test(expected = ValidationException.class)
	public void minValueShouldNotBeLessThan1ExpectsValidationException() {
		Ranking r = new Ranking(); r.setTitle("r1"); r.setSpecial(false); r.setMin(0);
		service.add(r);
	}

	@Test
	public void deleteUsingNullIdsShouldIgnore() {
		context.checking(new Expectations() {{

		}});

		service.delete(null);
		context.assertIsSatisfied();
	}

	@Test
	public void delete() {
		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(new Ranking()));
			one(repository).get(2); will(returnValue(new Ranking()));

			exactly(2).of(repository).remove(with(aNonNull(Ranking.class)));
		}});

		service.delete(1, 2);
		context.assertIsSatisfied();
	}

	@Test
	public void addExpectSuccess() {
		context.checking(new Expectations() {{
			one(repository).add(with(aNonNull(Ranking.class)));
		}});

		Ranking r = new Ranking(); r.setTitle("r1"); r.setMin(1);

		service.add(r);

		context.assertIsSatisfied();
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
		context.checking(new Expectations() {{
			one(repository).update(with(aNonNull(Ranking.class)));
		}});

		Ranking r = new Ranking(); r.setId(1); r.setTitle("r1"); r.setMin(1);

		service.update(r);

		context.assertIsSatisfied();
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
