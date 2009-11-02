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
import net.jforum.entities.Avatar;
import net.jforum.entities.AvatarType;
import net.jforum.repository.AvatarRepository;
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
public class AvatarServiceTestCase {
	private Mockery context = TestCaseUtils.newMockery();
	private AvatarRepository repository = context.mock(AvatarRepository.class);
	private JForumConfig config = context.mock(JForumConfig.class);
	private AvatarService service = new AvatarService(config,repository);

	@Test(expected = NullPointerException.class)
	public void addNullExpectException() {
		service.add(null, null);
	}

	@Test(expected = ValidationException.class)
	public void addWithIdExpectException() {
		Avatar avatar = new Avatar();
		avatar.setId(1);

		context.checking(new Expectations() {{
			one(config).getBoolean(ConfigKeys.AVATAR_ALLOW_GALLERY); will(returnValue(true));
			one(config).getBoolean(ConfigKeys.AVATAR_ALLOW_UPLOAD); will(returnValue(true));
		}});

		service.add(avatar);
	}

	@Test(expected = ValidationException.class)
	public void updateWithoutIdExpectException() {
		Avatar avatar = new Avatar();
		avatar.setId(0);

		context.checking(new Expectations() {{
			one(config).getBoolean(ConfigKeys.AVATAR_ALLOW_GALLERY); will(returnValue(true));
			one(config).getBoolean(ConfigKeys.AVATAR_ALLOW_UPLOAD); will(returnValue(true));
		}});

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

		UploadedFileInformation uploadedFile = new BasicUploadedFileInformation(tempFile,
				tempFile.getAbsolutePath(), tempFile.getName());

		context.checking(new Expectations() {{
			one(config).getApplicationPath(); will(returnValue(tempDir));
			one(config).getValue(ConfigKeys.AVATAR_GALLERY_DIR); will(returnValue(""));
			one(config).getBoolean(ConfigKeys.AVATAR_ALLOW_GALLERY); will(returnValue(true));
			one(config).getBoolean(ConfigKeys.AVATAR_ALLOW_UPLOAD); will(returnValue(true));
			one(config).getLong(ConfigKeys.AVATAR_MAX_SIZE); will(returnValue(10000l));
			one(config).getInt(ConfigKeys.AVATAR_MAX_WIDTH); will(returnValue(800));
			one(config).getInt(ConfigKeys.AVATAR_MAX_HEIGHT); will(returnValue(600));
			one(config).getInt(ConfigKeys.AVATAR_MIN_WIDTH); will(returnValue(1));
			one(config).getInt(ConfigKeys.AVATAR_MIN_HEIGHT); will(returnValue(1));
			one(repository).add(avatar);
		}});

		service.add(avatar, uploadedFile);
		context.assertIsSatisfied();
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

		context.checking(new Expectations() {{
			one(repository).get(1); will(returnValue(currentAvatar));
			atLeast(1).of(config).getApplicationPath(); will(returnValue(currentFile.getParent()));
			atLeast(1).of(config).getValue(ConfigKeys.AVATAR_GALLERY_DIR); will(returnValue(""));
			one(config).getBoolean(ConfigKeys.AVATAR_ALLOW_GALLERY); will(returnValue(true));
			one(config).getBoolean(ConfigKeys.AVATAR_ALLOW_UPLOAD); will(returnValue(true));
			one(config).getLong(ConfigKeys.AVATAR_MAX_SIZE); will(returnValue(10000l));
			one(config).getInt(ConfigKeys.AVATAR_MAX_WIDTH); will(returnValue(800));
			one(config).getInt(ConfigKeys.AVATAR_MAX_HEIGHT); will(returnValue(600));
			one(config).getInt(ConfigKeys.AVATAR_MIN_WIDTH); will(returnValue(1));
			one(config).getInt(ConfigKeys.AVATAR_MIN_HEIGHT); will(returnValue(1));
			one(repository).update(currentAvatar);
		}});

		File originalFile = new File(this.getClass().getResource("/smilies/smilie.gif").getFile());
		File newFile = File.createTempFile("jforum", "tests");
		TestCaseUtils.copyFile(originalFile, newFile);

		UploadedFileInformation uploadedFile = new BasicUploadedFileInformation(newFile,
			newFile.getAbsolutePath(), newFile.getName());

		String oldDiskName = currentAvatar.getFileName();

		Avatar newAvatar = new Avatar();
		newAvatar.setId(1);
		newAvatar.setAvatarType(AvatarType.AVATAR_GALLERY);
		service.update(newAvatar, uploadedFile);
		context.assertIsSatisfied();

		Assert.assertEquals(newAvatar.getAvatarType(), currentAvatar.getAvatarType());
		Assert.assertFalse(currentFile.exists());
		Assert.assertFalse(currentAvatar.getFileName().equals(oldDiskName));

		new File(String.format("%s/%s", currentFile.getParent(), currentAvatar.getFileName())).delete();
	}

	@Test
	public void deleteUsingNullShouldIgnore() {
		context.checking(new Expectations() {{ }});
		Avatar avatar = null;
		service.delete(avatar);
	}

	@Test
	public void deleteExpectSuccess() {
		context.checking(new Expectations() {{
			Avatar s1 = new Avatar(); s1.setId(1); s1.setFileName(Long.toString(System.currentTimeMillis()));
			Avatar s2 = new Avatar(); s2.setId(2); s2.setFileName(Long.toString(System.currentTimeMillis()));

			String applicationPath = new File(this.getClass().getResource("").getFile()).getParent();

			atLeast(1).of(config).getApplicationPath(); will(returnValue(applicationPath));
			atLeast(1).of(config).getValue(ConfigKeys.AVATAR_GALLERY_DIR); will(returnValue(""));

			one(repository).get(1); will(returnValue(s1));
			one(repository).remove(s1);

			one(repository).get(2); will(returnValue(s2));
			one(repository).remove(s2);

		}});

		service.delete(1, 2);
		context.assertIsSatisfied();
	}
}

