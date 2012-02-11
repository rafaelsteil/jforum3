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

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Smilie;
import net.jforum.repository.SmilieRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;
import net.jforum.util.MD5;
import net.jforum.util.UploadUtils;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class SmilieService {
	private SmilieRepository repository;
	private JForumConfig config;

	public SmilieService(SmilieRepository repository, JForumConfig config) {
		this.repository = repository;
		this.config = config;
	}

	/**
	 * Adds a new smilie
	 *
	 * @param smilie
	 */
	public void add(Smilie smilie, UploadedFile uploadedFile) {
		this.applyCommonConstraints(smilie);

		if (smilie.getId() > 0) {
			throw new ValidationException(
					"Cannot add an existing (id > 0) smilie");
		}

		String imageDiskName = this.saveImage(uploadedFile);

		if (imageDiskName == null) {
			throw new NullPointerException(
					"Could not find the smile file to save");
		}

		smilie.setDiskName(imageDiskName);

		this.repository.add(smilie);
	}

	/**
	 * Updates a existing smilie
	 *
	 * @param smilie
	 * @param file
	 */
	public void update(Smilie smilie, UploadedFile uploadedFile) {
		this.applyCommonConstraints(smilie);

		if (smilie.getId() == 0) {
			throw new ValidationException(
					"update() expects a smilie with an existing id");
		}

		String imageDiskName = this.saveImage(uploadedFile);

		Smilie current = this.repository.get(smilie.getId());
		current.setCode(smilie.getCode());

		if (imageDiskName != null) {
			this.deleteImage(current);
			current.setDiskName(imageDiskName);
		}

		this.repository.update(current);
	}

	/**
	 * Delete smilies
	 *
	 * @param smiliesId
	 */
	public void delete(int... smiliesId) {
		if (smiliesId != null) {
			for (int id : smiliesId) {
				Smilie s = this.repository.get(id);
				this.repository.remove(s);

				this.deleteImage(s);
			}
		}
	}

	private void deleteImage(Smilie smilie) {
		String filename = String.format("%s/%s/%s",
				this.config.getApplicationPath(),
				this.config.getValue(ConfigKeys.SMILIE_IMAGE_DIR),
				smilie.getDiskName());

		new File(filename).delete();
	}

	private String saveImage(UploadedFile uploadedFile) {
		if (uploadedFile != null) {
			UploadUtils upload = new UploadUtils(uploadedFile);

			String imageName = String.format(
					"%s.%s",
					MD5.hash(uploadedFile.getFileName()
							+ System.currentTimeMillis()),
					upload.getExtension());

			upload.saveUploadedFile(String.format("%s/%s/%s",
					this.config.getApplicationPath(),
					this.config.getValue(ConfigKeys.SMILIE_IMAGE_DIR),
					imageName));

			return imageName;
		}

		return null;
	}

	private void applyCommonConstraints(Smilie smilie) {
		if (smilie == null) {
			throw new NullPointerException("Cannot save a null smilie");
		}

		if (StringUtils.isEmpty(smilie.getCode())) {
			throw new ValidationException("Smilie code cannot be empty");
		}
	}
}
