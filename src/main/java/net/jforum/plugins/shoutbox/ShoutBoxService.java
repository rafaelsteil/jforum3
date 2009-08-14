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
package net.jforum.plugins.shoutbox;

import java.util.List;

import net.jforum.core.exceptions.ValidationException;
import net.jforum.entities.Category;

/**
 * @author Bill
 *
 */
public class ShoutBoxService {
	private ShoutBoxRepository repository;
	
	public ShoutBoxService(ShoutBoxRepository repository) {
		this.repository = repository;
	}

	@Deprecated
	public List<ShoutBox> getAvalibleBox(boolean isAnonymous){
		return repository.getAvalibleBoxes(isAnonymous);
	}

	public void add(ShoutBox shoutbox) {
		this.applyCommonConstraints(shoutbox);

		if (shoutbox.getId() > 0) {
			throw new ValidationException("This appears to be an existing category (id > 0). Please use update() instead");
		}
		
		if (shoutbox.getCategory() == null) {
			throw new ValidationException("Category cannot be blank or null");
		}
		
		repository.add(shoutbox);
	}
	
	public void update(ShoutBox shoutbox) {
		this.applyCommonConstraints(shoutbox);

		if (shoutbox.getId() == 0) {
			throw new ValidationException("update() expects a category with an existing id");
		}
		
		ShoutBox currentShoutBox = repository.get(shoutbox.getId());
		
		if(currentShoutBox == null)
			return;
		
		currentShoutBox.setAllowAnonymous(shoutbox.isAllowAnonymous());
		currentShoutBox.setDisabled(shoutbox.isDisabled());
		currentShoutBox.setShoutLength(shoutbox.getShoutLength());
		repository.update(currentShoutBox);
	}

	public ShoutBox get(int id) {
		return repository.get(id);
	}

	public ShoutBox getShoutBox(Category category) {
		return repository.getShoutBox(category);
	}
	
	public ShoutBox getShoutBox(int categoryId) {
		Category category = new Category();
		category.setId(categoryId);
		ShoutBox shoutbox = null;
		try{
			shoutbox = repository.getShoutBox(category);
		}catch(Exception e){
			
		}
		return shoutbox;
	}
	
	private void applyCommonConstraints(ShoutBox shoutbox) {
		if (shoutbox == null) {
			throw new NullPointerException("Cannot save a null category");
		}
	}
}
