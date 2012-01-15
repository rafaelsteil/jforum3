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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import net.jforum.core.exceptions.ForumException;
import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Avatar;
import net.jforum.entities.AvatarType;
import net.jforum.repository.AvatarRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.ImageInfo;
import net.jforum.util.JForumConfig;
import net.jforum.util.MD5;
import net.jforum.util.UploadUtils;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Bill
 */
@Component
public class AvatarService {
	private AvatarRepository repository;
	private JForumConfig config;

	public AvatarService(JForumConfig config, AvatarRepository repository) {
		this.config = config;
		this.repository = repository;
	}

	public List<Avatar> getAvatarGallery() {
		boolean allowGallery = this.config.getBoolean(ConfigKeys.AVATAR_ALLOW_GALLERY);

		if (allowGallery) {
			return this.repository.getGalleryAvatar();
		}

		return new ArrayList<Avatar>();
	}

	/**
	 * Adds a new avatar
	 *
	 * @param avatar
	 * @param image
	 */
	public void add(Avatar avatar, UploadedFile uploadedFile) {
		if (uploadedFile == null) {
			this.add(avatar);
			return;
		}

		this.isAllowed(avatar);

		if (avatar.getId() > 0) {
			throw new ValidationException("Cannot add an existing (id > 0) avatar");
		}

		String imgName = this.processImageUpload(avatar, uploadedFile);

		if (imgName != null) {
			this.repository.add(avatar);
		}
	}

	/**
	 * Add avatar without upload the image no operation on Image
	 *
	 * @param avatar
	 */
	public void add(Avatar avatar) {
		this.isAllowed(avatar);

		if (avatar.getId() > 0) {
			throw new ValidationException("Cannot add an existing (id > 0) avatar");
		}

		this.checkImageSize(avatar);
		this.repository.add(avatar);
	}

	/**
	 * Updates a existing avatar
	 *
	 * @param avatar
	 * @param image
	 */
	public void update(Avatar avatar, UploadedFile uploadedFile) {
		this.isAllowed(avatar);

		if (avatar.getId() == 0) {
			throw new ValidationException("update() expects a avatar with an existing id");
		}

		Avatar current = this.repository.get(avatar.getId());
		avatar.setAvatarType(current.getAvatarType());

		// upload the img and get the upload img info
		String imageDiskName = this.processImageUpload(avatar, uploadedFile);

		if (imageDiskName != null) {
			this.deleteImage(current);

			current.setFileName(imageDiskName);
			current.setHeight(avatar.getHeight());
			current.setWidth(avatar.getWidth());
		}

		this.repository.update(current);
	}

	/**
	 * Delete Avatars
	 *
	 * @param avatarId
	 */
	public void delete(int... avatarId) {
		if (avatarId != null) {
			for (int id : avatarId) {
				Avatar avatar = this.repository.get(id);
				this.delete(avatar);
			}
		}
	}

	/**
	 * remove Avatar and del its image
	 *
	 * @param avatar
	 */
	public void delete(Avatar avatar) {
		if (avatar != null) {
			this.repository.remove(avatar);
			this.deleteImage(avatar);
		}
	}

	private void deleteImage(Avatar avatar) {
		File img = this.getAvatarImageFile(avatar);

		if (img != null) {
			img.delete();
		}
	}

	/**
	 * get the key in config that for give avatar to find the path
	 *
	 * @param avatar
	 * @return
	 */
	private String getAvatarPathConfigKey(Avatar avatar) {
		String avatarConfigKey = null;

		if (avatar == null) {
			return avatarConfigKey;
		}

		if (avatar.getAvatarType() == AvatarType.AVATAR_UPLOAD) {
			avatarConfigKey = ConfigKeys.AVATAR_UPLOAD_DIR;
		}
		else if (avatar.getAvatarType() == AvatarType.AVATAR_GALLERY) {
			avatarConfigKey = ConfigKeys.AVATAR_GALLERY_DIR;
		}

		return avatarConfigKey;
	}

	/**
	 * check the user can do this operation according to config
	 *
	 * @param avatar
	 */
	private void isAllowed(Avatar avatar) {
		this.applyCommonConstraints(avatar);

		boolean allowGallery = this.config.getBoolean(ConfigKeys.AVATAR_ALLOW_GALLERY);
		boolean allowUpload = this.config.getBoolean(ConfigKeys.AVATAR_ALLOW_UPLOAD);

		if (avatar.getAvatarType() == AvatarType.AVATAR_UPLOAD && !allowUpload
				|| avatar.getAvatarType() == AvatarType.AVATAR_GALLERY && !allowGallery) {
			throw new ValidationException(avatar.getAvatarType() + "is not allowed!");
		}
	}

	/**
	 * upload avatar image and resize the images if need
	 *
	 * @param avatar
	 * @param uploadedFile
	 * @return file name
	 */
	private String processImageUpload(Avatar avatar, UploadedFile uploadedFile) {
		File file = this.saveImage(avatar, uploadedFile);

		if (file == null) {
			return null;
		}

		avatar.setFileName(file.getName());

		try {
			long size = file.length();
			long maxSize = this.config.getLong(ConfigKeys.AVATAR_MAX_SIZE);

			if (size > maxSize) {
				throw new ValidationException("File size too big");
			}

			ImageInfo ii = new ImageInfo();

			try {
				ii.setInput(new FileInputStream(file));
			}
			catch (FileNotFoundException e) {
				throw new ForumException(e);
			}

			if (!ii.check()) {
				throw new ValidationException("Not a supported image file format.");
			}

			avatar.setHeight(ii.getHeight());
			avatar.setWidth(ii.getWidth());

			// check the image size
			this.checkImageSize(avatar);
		}
		catch (ValidationException e) {
			file.delete();
			throw e;
		}

		return file.getName();
	}

	/**
	 * Save image to avatar dir
	 *
	 * @param avatar
	 * @param uploadedFile
	 * @return
	 */
	private File saveImage(Avatar avatar, UploadedFile uploadedFile) {
		String configKey = getAvatarPathConfigKey(avatar);

		if (configKey != null && uploadedFile != null) {
			UploadUtils upload = new UploadUtils(uploadedFile);

			String imageName = String.format("%s.%s", MD5.hash(uploadedFile.getFileName() + System.currentTimeMillis()),
				upload.getExtension());

			String filePath = String.format("%s/%s/%s", this.config.getApplicationPath(), this.config.getValue(configKey), imageName);

			upload.saveUploadedFile(filePath);

			return new File(filePath);
		}

		return null;
	}

	/**
	 * fix the size of the image true, chenged the image size false, not change
	 *
	 * @param avatar
	 */
	private void checkImageSize(Avatar avatar) {
		int maxWidth = this.config.getInt(ConfigKeys.AVATAR_MAX_WIDTH);
		int maxHeight = this.config.getInt(ConfigKeys.AVATAR_MAX_HEIGHT);
		int minWidth = this.config.getInt(ConfigKeys.AVATAR_MIN_WIDTH);
		int minHeight = this.config.getInt(ConfigKeys.AVATAR_MIN_HEIGHT);

		int height = avatar.getHeight();
		int width = avatar.getWidth();

		if (height < minHeight || height > maxHeight || width < minWidth || width > maxWidth) {
			throw new ValidationException("This image size is not allowed!");
		}
	}

	private File getAvatarImageFile(Avatar avatar) {
		String avatarConfigKey = getAvatarPathConfigKey(avatar);

		if (avatarConfigKey == null) {
			return null;
		}
		else {
			String imageName = avatar.getFileName();
			String imageFilePath = String.format("%s/%s/%s", this.config.getApplicationPath(),
				this.config.getValue(avatarConfigKey), imageName);

			return new File(imageFilePath);
		}

	}

	private void applyCommonConstraints(Avatar avatar) {
		if (avatar == null) {
			throw new NullPointerException("Cannot savel a null avatar");
		}
	}
}
