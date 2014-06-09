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

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Smilie;
import net.jforum.repository.SmilieRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.TestCaseUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.interceptor.multipart.DefaultUploadedFile;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class SmilieServiceTestCase {

	@Mock private SmilieRepository repository;
	@Mock private JForumConfig config;
	@InjectMocks private SmilieService service;

	@Test(expected = NullPointerException.class)
	public void addNullExpectException() {
		service.add(null, null);
	}

	@Test(expected = ValidationException.class)
	public void addUsingEmptyCodeExpectException() {
		Smilie s = new Smilie();
		s.setCode("");
		service.add(s, null);
	}

	@Test(expected = ValidationException.class)
	public void addUsingNullCodeExpectException() {
		Smilie s = new Smilie();
		s.setCode(null);
		service.add(s, null);
	}

	@Test(expected = ValidationException.class)
	public void addUsingIdBiggerThanZeroExpectException() {
		Smilie s = new Smilie();
		s.setCode("x");
		s.setId(1);
		service.add(s, null);
	}

	@Test(expected = NullPointerException.class)
	public void addUsingNullImageExpectsException() {
		Smilie s = new Smilie();
		s.setCode(":)");
		service.add(s, null);
	}

	@Test
	public void addExpectSuccess() throws IOException {
		final Smilie smilie = new Smilie();
		smilie.setCode(":)");
		File tempFile = File.createTempFile("jforum", "tests");
		tempFile.deleteOnExit();
		final String tempDir = tempFile.getParent();

		File file = new File(this.getClass().getResource("/smilies/smilie.gif").getFile());
		TestCaseUtils.copyFile(file, tempFile);

		UploadedFile uploadedFile = new DefaultUploadedFile(
				new FileInputStream(file), file.getAbsolutePath(), "");

		when(config.getApplicationPath()).thenReturn(tempDir);
		when(config.getValue(ConfigKeys.SMILIE_IMAGE_DIR)).thenReturn("");


		service.add(smilie, uploadedFile);

		verify(repository).add(smilie);
		Assert.assertNotNull(smilie.getDiskName());

		File expectedFile = new File(String.format("%s/%s/%s", tempDir, "",
				smilie.getDiskName()));
		expectedFile.deleteOnExit();

		Assert.assertTrue(expectedFile.exists());
	}

	@Test(expected = NullPointerException.class)
	public void updateNullExpectException() {
		service.update(null, null);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingEmptyCodeExpectException() {
		Smilie s = new Smilie();
		s.setCode("");
		s.setId(1);
		service.update(s, null);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingNullCodeExpectException() {
		Smilie s = new Smilie();
		s.setCode(null);
		s.setId(1);
		service.update(s, null);
	}

	@Test(expected = ValidationException.class)
	public void updateUsingIdZeroExpectException() {
		Smilie s = new Smilie();
		s.setCode("x");
		s.setId(0);
		service.update(s, null);
	}

	@Test
	public void updateAllPropertiesShouldDeleteOldImage() throws IOException {
		final File currentFile = File.createTempFile("jforum", "tests");
		currentFile.deleteOnExit();
		final Smilie currentSmilie = new Smilie();
		currentSmilie.setId(1);
		currentSmilie.setCode(":)");
		currentSmilie.setDiskName(currentFile.getName());

		when(repository.get(1)).thenReturn(currentSmilie);
		when(config.getApplicationPath()).thenReturn(currentFile.getParent());
		when(config.getValue(ConfigKeys.SMILIE_IMAGE_DIR)).thenReturn("");

		File newFile = File.createTempFile("jforum", "tests");
		newFile.deleteOnExit();

		UploadedFile uploadedFile = new DefaultUploadedFile(
				new FileInputStream(newFile), newFile.getAbsolutePath(), "");

		String oldDiskName = currentSmilie.getDiskName();

		Smilie newSmilie = new Smilie();
		newSmilie.setId(1);
		newSmilie.setCode(":D");
		service.update(newSmilie, uploadedFile);

		verify(repository).update(currentSmilie);
		Assert.assertEquals(newSmilie.getCode(), currentSmilie.getCode());
		Assert.assertFalse(currentFile.exists());
		Assert.assertFalse(currentSmilie.getDiskName().equals(oldDiskName));

		new File(String.format("%s/%s", currentFile.getParent(),
				currentSmilie.getDiskName())).delete();
	}

	@Test
	public void updateOnlyCodeExpectsSuccess() {
		final Smilie currentSmilie = new Smilie();
		currentSmilie.setCode(":)");
		currentSmilie.setId(1);
		when(repository.get(1)).thenReturn(currentSmilie);
		Smilie newSmilie = new Smilie();
		newSmilie.setId(1);
		newSmilie.setCode(":D");
		service.update(newSmilie, null);

		Assert.assertEquals(newSmilie.getCode(), currentSmilie.getCode());
		
		verify(repository).update(currentSmilie);
	}

	@Test
	public void deleteUsingNullShouldIgnore() {
		service.delete(null);
		
		verifyZeroInteractions(repository);
	}

	@Test
	public void deleteExpectSuccess() {
		Smilie s1 = new Smilie();
		s1.setId(1);
		s1.setDiskName(Long.toString(System.currentTimeMillis()));
		Smilie s2 = new Smilie();
		s2.setId(2);
		s2.setDiskName(Long.toString(System.currentTimeMillis()));
		String applicationPath = new File(this.getClass()
				.getResource("").getFile()).getParent();
		when(config.getApplicationPath()).thenReturn(applicationPath);
		when(config.getValue(ConfigKeys.SMILIE_IMAGE_DIR)).thenReturn("");
		when(repository.get(1)).thenReturn(s1);
		when(repository.get(2)).thenReturn(s2);

		service.delete(1, 2);

		verify(repository).remove(s1);
		verify(repository).remove(s2);
	}
}
