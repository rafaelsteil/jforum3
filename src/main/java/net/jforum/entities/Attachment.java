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
 * Created on Jan 18, 2005 2:58:22 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_attach")
@Component
@PrototypeScoped
public class Attachment {
	@Id
	@Column(name = "attach_id")
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_attach_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	private int id;

	@ManyToOne
	@JoinColumn(name = "post_id")
	private Post post;

	@Column(name = "download_count")
	private int downloadCount;

	@Column(name = "physical_filename")
	private String physicalFilename;

	@Column(name = "real_filename")
	private String realFilename;

	@Column(name = "description")
	private String description;

	@Column(name = "mimetype")
	private String mimetype;

	@Column(name = "upload_date")
	private Date uploadDate;

	@Column(name = "filesize")
	private long filesize;

	@Column(name = "thumb")
	private boolean hasThumb;

	@Column(name = "file_extension")
	private String fileExtension;

	/**
	 * @return the downloadCount
	 */
	public int getDownloadCount() {
		return this.downloadCount;
	}

	/**
	 * @param downloadCount the downloadCount to set
	 */
	public void setDownloadCount(int downloadCount) {
		this.downloadCount = downloadCount;
	}

	/**
	 * @return the physicalFilename
	 */
	public String getPhysicalFilename() {
		return this.physicalFilename;
	}

	/**
	 * @param physicalFilename the physicalFilename to set
	 */
	public void setPhysicalFilename(String physicalFilename) {
		this.physicalFilename = physicalFilename;
	}

	/**
	 * @return the realFilename
	 */
	public String getRealFilename() {
		return this.realFilename;
	}

	/**
	 * @param realFilename the realFilename to set
	 */
	public void setRealFilename(String realFilename) {
		this.realFilename = realFilename;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the mimetype
	 */
	public String getMimetype() {
		return this.mimetype;
	}

	/**
	 * @param mimetype the mimetype to set
	 */
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	/**
	 * @return the uploadDate
	 */
	public Date getUploadDate() {
		return this.uploadDate;
	}

	/**
	 * @param uploadDate the uploadDate to set
	 */
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	/**
	 * @return the filesize
	 */
	public long getFilesize() {
		return this.filesize;
	}

	/**
	 * @param filesize the filesize to set
	 */
	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}

	/**
	 * @return the hasThumb
	 */
	public boolean isHasThumb() {
		return this.hasThumb;
	}

	/**
	 * @param hasThumb the hasThumb to set
	 */
	public void setHasThumb(boolean hasThumb) {
		this.hasThumb = hasThumb;
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
	 * @param post the post to set
	 */
	public void setPost(Post post) {
		this.post = post;
	}

	/**
	 * @return the post
	 */
	public Post getPost() {
		return post;
	}

	/**
	 * @param fileExtension the fileExtension to set
	 */
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
	}

	public void incrementDownloadCount() {
		this.downloadCount++;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Attachment other = (Attachment) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
	}
}
