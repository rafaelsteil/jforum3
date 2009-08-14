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
 * Created on 24/09/2006 23:04:29
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.mail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jforum.entities.Topic;
import net.jforum.util.MD5;

/**
 * Represents the In-Reply-To and Message-ID mail header.
 *
 * @author Rafael Steil
 */
public class MessageId {
	private int topicId;

	/**
	 * Returns the topic id this header holds.
	 *
	 * @return the topic id represented by this instance
	 */
	public int getTopicId() {
		return this.topicId;
	}

	/**
	 * Constructs the Message-ID header. The form is "&lt;postId.topicId.forumId.randomNumber@jforum&gt;".
	 *
	 * @param postId the post id of this message
	 * @param topicId the topic id of this message
	 * @param forumId the forum id of this message
	 * @return the Message-ID header
	 */
	public static String buildMessageId(int postId, int topicId, int forumId) {
		return new StringBuffer()
			.append('<')
			.append(postId)
			.append('.')
			.append(topicId)
			.append('.')
			.append(forumId)
			.append('.')
			.append(System.currentTimeMillis())
			.append(MD5.hash(Long.toString(System.currentTimeMillis())))
			.append("@jforum>").toString();
	}

	/**
	 * Constructs the In-Reply-To header.
	 * The form is "&lt;topicFirstPostId.topicId.forumId.randomNumber@jforum&gt;".
	 *
	 * @param topic The topic we're replying to. It should have at least the
	 * values for {@link Topic#getFirstPostId()}, {@link Topic#getId()} and {@link Topic#getForumId()}
	 * @return the In-Reply-To header
	 */
	public static String buildInReplyTo(Topic topic) {
		return buildMessageId(topic.getFirstPost().getId(), topic.getId(), topic.getForum().getId());
	}

	/**
	 * Parses the header, extracting the information it holds
	 *
	 * @param header the header's contents to parse
	 * @return the header information parsed
	 */
	public static MessageId parse(String header) {
		MessageId messageId = new MessageId();

		if (header != null) {
			// <postId.topicId.forumId.randomNumber@host>
			Matcher matcher = Pattern.compile("<(.*?)\\.(.*?)\\.(.*?)\\.(.*?)@.*>").matcher(header);

			if (matcher.matches()) {
				String s = matcher.group(2);

				try {
					messageId.topicId = Integer.parseInt(s);
				}
				catch (Exception e) { }
			}
		}

		return messageId;
	}
}
