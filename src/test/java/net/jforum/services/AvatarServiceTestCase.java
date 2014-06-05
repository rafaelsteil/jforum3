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
import net.jforum.entities.Avatar;
import net.jforum.entities.AvatarType;
import net.jforum.repository.AvatarRepository;
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
public class AvatarServiceTestCase {
	
	@Mock private AvatarRepository repository;
	@Mock private JForumConfig config;
	@InjectMocks private AvatarService service;

	@Test(expected = NullPointerException.class)
	public void addNullExpectException() {
		service.add(null, null);
	}

	@Test(expected = ValidationException.class)
	public void addWithIdExpectException() {
		Avatar avatar = new Avatar();
		avatar.setId(1);

		when(config.getBoolean(ConfigKeys.AVATAR_ALLOW_GALLERY)).thenReturn(true);
		when(config.getBoolean(ConfigKeys.AVATAR_ALLOW_UPLOAD)).thenReturn(true);

		service.add(avatar);
	}

	@Test(expected = ValidationException.class)
	public void updateWithoutIdExpectException() {
		Avatar avatar = new Avatar();
		avatar.setId(0);

		when(config.getBoolean(ConfigKeys.AVATAR_ALLOW_GALLERY)).thenReturn(true);
		when(config.getBoolean(ConfigKeys.AVATAR_ALLOW_UPLOAD)).thenReturn(true);

		service.update(avatar, null);
	}

	@Test
	public void addExpectSuccess() throws IOException {
		final Avatar avatar = new Avatar();
		File tempFile = File.createTempFile("jforum", "tests");
		tempFile.deleteOnExit();
		final String tempDir = tempFile.getParent();
		File file = new File(this.getClass().getResource("/smilies/smilie.gif").getFile());
		TestCaseUtils.copyFile(file, tempFile);
		UploadedFile uploadedFile = new DefaultUploadedFile(new FileInputStream(file), file.getAbsolutePath(), "");
		
		when(config.getApplicationPath()).thenReturn(tempDir);
		when(config.getValue(ConfigKeys.AVATAR_GALLERY_DIR)).thenReturn("");
		when(config.getBoolean(ConfigKeys.AVATAR_ALLOW_GALLERY)).thenReturn(true);
		when(config.getBoolean(ConfigKeys.AVATAR_ALLOW_UPLOAD)).thenReturn(true);
		when(config.getLong(ConfigKeys.AVATAR_MAX_SIZE)).thenReturn(10000l);
		when(config.getInt(ConfigKeys.AVATAR_MAX_WIDTH)).thenReturn(800);
		when(config.getInt(ConfigKeys.AVATAR_MAX_HEIGHT)).thenReturn(600);
		when(config.getInt(ConfigKeys.AVATAR_MIN_WIDTH)).thenReturn(1);
		when(config.getInt(ConfigKeys.AVATAR_MIN_HEIGHT)).thenReturn(1);
		
		service.add(avatar, uploadedFile);
		
		verify(repository).add(avatar);
		Assert.assertNotNull(avatar.getFileName());
		File expectedFile = new File(String.format("%s/%s/%s", tempDir, "", avatar.getFileName()));
		expectedFile.deleteOnExit();
		Assert.assertTrue(expectedFile.exists());
	}

	@Test(expected = NullPointerException.class)
	public void updateNullExpectException() {
		service.update(null, null);
	}

	@Test
	public void updateImageShouldDeleteOldImage() throws IOException {
		final File currentFile = File.createTempFile("avatar", "tests");
		currentFile.deleteOnExit();

		final Avatar currentAvatar = new Avatar();
		currentAvatar.setId(1);
		currentAvatar.setAvatarType(AvatarType.AVATAR_GALLERY);
		currentAvatar.setFileName(currentFile.getName());
		
		when(repository.get(1)).thenReturn(currentAvatar);
		when(config.getApplicationPath()).thenReturn(currentFile.getParent());
		when(config.getValue(ConfigKeys.AVATAR_GALLERY_DIR)).thenReturn("");
		when(config.getBoolean(ConfigKeys.AVATAR_ALLOW_GALLERY)).thenReturn(true);
		when(config.getBoolean(ConfigKeys.AVATAR_ALLOW_UPLOAD)).thenReturn(true);
		when(config.getLong(ConfigKeys.AVATAR_MAX_SIZE)).thenReturn(10000l);
		when(config.getInt(ConfigKeys.AVATAR_MAX_WIDTH)).thenReturn(800);
		when(config.getInt(ConfigKeys.AVATAR_MAX_HEIGHT)).thenReturn(600);
		when(config.getInt(ConfigKeys.AVATAR_MIN_WIDTH)).thenReturn(1);
		when(config.getInt(ConfigKeys.AVATAR_MIN_HEIGHT)).thenReturn(1);
			
		File originalFile = new File(this.getClass().getResource("/smilies/smilie.gif").getFile());
		File newFile = File.createTempFile("jforum", "tests");
		TestCaseUtils.copyFile(originalFile, newFile);

		UploadedFile uploadedFile = new DefaultUploadedFile(new FileInputStream(newFile), newFile.getAbsolutePath(), "");
		String oldDiskName = currentAvatar.getFileName();
		Avatar newAvatar = new Avatar();
		newAvatar.setId(1);
		newAvatar.setAvatarType(AvatarType.AVATAR_GALLERY);
		service.update(newAvatar, uploadedFile);
		
		verify(repository).update(currentAvatar);
		Assert.assertEquals(newAvatar.getAvatarType(), currentAvatar.getAvatarType());
		Assert.assertFalse(currentFile.exists());
		Assert.assertFalse(currentAvatar.getFileName().equals(oldDiskName));

		new File(String.format("%s/%s", currentFile.getParent(), currentAvatar.getFileName())).delete();
	}

	@Test
	public void deleteUsingNullShouldIgnore() {
		
			
		Avatar avatar = null;
		service.delete(avatar);
	}

	@Test
	public void deleteExpectSuccess() {
		
		Avatar s1 = new Avatar();
		s1.setId(1);
		s1.setFileName(Long.toString(System.currentTimeMillis()));
		Avatar s2 = new Avatar();
		s2.setId(2);
		s2.setFileName(Long.toString(System.currentTimeMillis()));
		String applicationPath = new File(this.getClass().getResource("").getFile()).getParent();
		when(config.getApplicationPath()).thenReturn(applicationPath);
		when(config.getValue(ConfigKeys.AVATAR_GALLERY_DIR)).thenReturn("");
		when(repository.get(1)).thenReturn(s1);
		when(repository.get(2)).thenReturn(s2);

		service.delete(1, 2);
		
		verify(repository).remove(s1);
		verify(repository).remove(s2);
	}
}
