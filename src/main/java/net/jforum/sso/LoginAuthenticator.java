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
 * Created on Aug 2, 2004 by pieter
 *
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.sso;

import java.util.Map;

import net.jforum.entities.User;

/**
 * Validates user's credentials.
 * Implementations of this interface are
 * supposed to check for access rights in some "shared" environment,
 * like calling some external procedure, consulting a different users
 * table, reading from a XML file etc.. It is <b>not</b> SSO, since it
 * still will be JForum that will call the validate login methods. <br>
 * If you want SSO, please check {@link net.jforum.sso.SSO}
 *
 * @author Rafael Steil
 */
public interface LoginAuthenticator {
	/**
	 * Authenticates an user.
	 *
	 * @param username Username
	 * @param password Password
	 * @param extraParams Extra parameters, if any.
	 * @return An instance of a {@link net.jforum.entities.User} or <code>null</code>
	 */
	public User validateLogin(String username, String password, Map<String, Object> extraParams);
}
