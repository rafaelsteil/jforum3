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

import java.io.File;
import java.io.IOException;

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Smilie;
import net.jforum.repository.SmilieRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;
import org.vraptor.interceptor.BasicUploadedFileInformation;
import org.vraptor.interceptor.UploadedFileInformation;

/**
 * @author Rafael Steil
 */
public class SmilieServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private SmilieRepository repository = context.mock(SmilieRepository.class);
	private JForumConfig config = context.mock(JForumConfig.class);
	private SmilieService service = new SmilieService(repository, config);

	@Test(expected = NullPointerException.class)
	public void addNullExpectException() {
		service.add(null, null);
	}

	@Test(expected = ValidationException.class)
	public void addUsingEmptyCodeExpectException() {
		Smilie s = new Smilie(); s.setCode("");
		service.add(s, null);
	}

	@Test(expected = ValidationException.class)
	public void addUsingNullCodeExpectException() {
		Smilie s = new Smilie(); s.setCode(null);
		service.add(s, null);
	}

	@Test(expected = ValidationException.class)
	public void addUsingIdBiggerThanZeroExpectException() {
		Smilie s = new Smilie(); s.setCode("x"); s.setId(1);
		service.add(s, null);
	}

	@Test(expected = NullPointerException.class)
	public void addUsingNullImageExpectsException() {
		Smilie s = new Smilie(); s.setCode(":)");
		service.add(s, null);
	}

	@Test
	public void addExpectSuccess() throws IOException {
		final Smilie smilie = new Smilie(); smilie.setCode(":)");
		File tempFile = File.createTempFile("jforum", "tests");
		tempFile.deleteOnExit();
		final String tempDir = tempFile.getParent();

		File file = new File(this.getClass().getResource("/smilies/smilie.gif").getFile());
		UploadedFileInformation uploadedFile = new BasicUploadedFileInformation(file,
			file.getAbsolutePath(), file.getName());

		context.checking(new Expectations() {{
			one(config).getApplicationPath(); will(returnValue(tempDir));
			one(config).getValue(ConfigKeys.SMILIE_IMAGE_DIR); will(returnValue(""));
			one(repository).add(smilie);
		}});

		service.add(smilie, uploadedFile);
		context.assertIsSatisfied();
		Assert.assertNotNull(smilie.getDiskName());
		Assert.assertTrue(smilie.getDiskName().endsWith(".gif"));

		File expectedFile = new File(String.format("%s/%s/%s", tempDir, "", smilie.getDiskName()));
		expectedFile.deleteOnExit();

		Assert.assertTrue(expectedFile.exists());
	}

	@Test(expected = NullPointerException.class)
	public void updateNullExpectException() {
		service.update(null, null);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingEmptyCodeExpectException() {
		Smilie s = new Smilie(); s.setCode(""); s.setId(1);
		service.update(s, null);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingNullCodeExpectException() {
		Smilie s = new Smilie(); s.setCode(null); s.setId(1);
		service.update(s, null);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingIdZeroExpectException() {
		Smilie s = new Smilie(); s.setCode("x"); s.setId(0);
		service.update(s, null);
	}

	@Test
	public void updateAllPropertiesShouldDeleteOldImage() throws IOException {
		final File currentFile = File.createTempFile("jforum", "tests");
		currentFile.deleteOnExit();

		final Smilie currentSmilie = new Smilie(); currentSmilie.setId(1); currentSmilie.setCode(":)");
		currentSmilie.setDiskName(currentFile.getName());

		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(currentSmilie));
			atLeast(1).of(config).getApplicationPath(); will(returnValue(currentFile.getParent()));
			atLeast(1).of(config).getValue(ConfigKeys.SMILIE_IMAGE_DIR); will(returnValue(""));
			one(repository).update(currentSmilie);
		}});

		File newFile = File.createTempFile("jforum", "tests");
		newFile.deleteOnExit();

		UploadedFileInformation uploadedFile = new BasicUploadedFileInformation(newFile,
			newFile.getAbsolutePath(), newFile.getName());

		String oldDiskName = currentSmilie.getDiskName();

		Smilie newSmilie = new Smilie(); newSmilie.setId(1); newSmilie.setCode(":D");
		service.update(newSmilie, uploadedFile);
		context.assertIsSatisfied();

		Assert.assertEquals(newSmilie.getCode(), currentSmilie.getCode());
		Assert.assertFalse(currentFile.exists());
		Assert.assertFalse(currentSmilie.getDiskName().equals(oldDiskName));

		new File(String.format("%s/%s", currentFile.getParent(), currentSmilie.getDiskName())).delete();
	}

	@Test
	public void updateOnlyCodeExpectsSuccess() {
		final Smilie currentSmilie = new Smilie(); currentSmilie.setCode(":)"); currentSmilie.setId(1);

		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(currentSmilie));
			one(repository).update(currentSmilie);
		}});

		Smilie newSmilie = new Smilie(); newSmilie.setId(1); newSmilie.setCode(":D");
		service.update(newSmilie, null);
		context.assertIsSatisfied();
		Assert.assertEquals(newSmilie.getCode(), currentSmilie.getCode());
	}

	@Test
	public void deleteUsingNullShouldIgnore() {
		context.checking(new Expectations() {{ }});
		service.delete(null);
	}

	@Test
	public void deleteExpectSuccess() {
		context.checking(new Expectations() {{
			Smilie s1 = new Smilie(); s1.setId(1); s1.setDiskName(Long.toString(System.currentTimeMillis()));
			Smilie s2 = new Smilie(); s2.setId(2); s2.setDiskName(Long.toString(System.currentTimeMillis()));

			String applicationPath = new File(this.getClass().getResource("").getFile()).getParent();

			atLeast(1).of(config).getApplicationPath(); will(returnValue(applicationPath));
			atLeast(1).of(config).getValue(ConfigKeys.SMILIE_IMAGE_DIR); will(returnValue(""));

			one(repository).get(1); will(returnValue(s1));
			one(repository).remove(s1);

			one(repository).get(2); will(returnValue(s2));
			one(repository).remove(s2);

		}});

		service.delete(1, 2);
		context.assertIsSatisfied();
	}
}

