/*
 * Copyright (c) JForum Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms,
 * with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor
 * the names of its contributors may be used to endorse
 * or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 *
 * Created on Jan 17, 2005 8:21:32 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_attach_extensions")
@Component
@PrototypeScoped
public class AttachmentExtension {
	@Id
	@Column(name = "extension_id")
	@GeneratedValue
	private int id;

	@ManyToMany
	@JoinColumn(name = "extension_group_id")
	private AttachmentExtensionGroup extensionGroup;

	@Column(name = "allow")
	private boolean allow;

	@Transient
	private boolean unknown;

	@Column(name = "description")
	private String description;

	@Column(name = "extension")
	private String extension;

	/**
	 * @return Returns the allow.
	 */
	public boolean isAllowed() {
		return this.allow;
	}

	/**
	 * @param allow The allow to set.
	 */
	public void setAllowed(boolean allow) {
		this.allow = allow;
	}

	/**
	 * @return Returns the comment.
	 */
	public String getComment() {
		return this.description;
	}

	/**
	 * @param comment The comment to set.
	 */
	public void setComment(String comment) {
		this.description = comment;
	}

	/**
	 * @return Returns the extension.
	 */
	public String getExtension() {
		return this.extension;
	}

	/**
	 * @param extension The extension to set.
	 */
	public void setExtension(String extension) {
		if (extension != null) {
			this.extension = extension.toLowerCase();
		}
	}

	/**
	 * @return Returns the extensionGroupId.
	 */
	public AttachmentExtensionGroup getExtensionGroup() {
		return this.extensionGroup;
	}

	/**
	 * @param extensionGroupId The extensionGroupId to set.
	 */
	public void setExtensionGroup(AttachmentExtensionGroup extensionGroup) {
		this.extensionGroup = extensionGroup;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return Returns the unknown.
	 */
	public boolean isUnknown() {
		return this.unknown;
	}

	/**
	 * @param unknown The unknown to set.
	 */
	public void setUnknown(boolean unknown) {
		this.unknown = unknown;
	}
}
