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
 * Created on Jan 18, 2005 4:06:08 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.jforum.core.exceptions.ForumException;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;

/**
 * @author Rafael Steil
 */
public class UploadUtils {
	private UploadedFile uploadedFile;

	public UploadUtils(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public String getExtension() {
		String fileName = this.uploadedFile.getFileName();
		return fileName.substring(fileName.lastIndexOf('.') + 1);
	}

	public void saveUploadedFile(String filename) {
		BufferedInputStream inputStream = null;
		FileOutputStream outputStream = null;

		try {
			inputStream = new BufferedInputStream(this.uploadedFile.getFile());
			outputStream = new FileOutputStream(filename);

			int c;
			byte[] b = new byte[4096];
			while ((c = inputStream.read(b)) != -1) {
				outputStream.write(b, 0, c);
			}
		}
		catch (IOException e) {
			throw new ForumException(e);
		}
		finally {
			try {
				outputStream.flush();
				outputStream.close();

				inputStream.close();
			}
			catch (Exception e) {
			}
		}
	}
}
