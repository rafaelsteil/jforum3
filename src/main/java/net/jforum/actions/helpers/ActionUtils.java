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
package net.jforum.actions.helpers;

import net.jforum.entities.Post;
import net.jforum.entities.PrivateMessage;

/**
 * General utilities methods to be used by the components
 * @author Rafael Steil
 */
public class ActionUtils {
	/**
	 * Given a {@link PostFormOptions}, copy it to the respective {@link Post}
	 * @param post the post to be updated
	 * @param options the options to be set
	 */
	public static void definePostOptions(Post post, PostFormOptions options) {
		if (options != null) {
			post.setHtmlEnabled(options.isHtmlEnabled());
			post.setBbCodeEnabled(options.isBbCodeEnabled());
			post.setSmiliesEnabled(options.isSmiliesEnabled());
			post.setSignatureEnabled(options.getAppendSignature());
			post.setNotifyReplies(options.getNotifyReplies());
		}
	}

	/**
	 * Given a {@link PostFormOptions}, copy it to the respective {@link PrivateMessage}
	 * @param pm the private message to be updated
	 * @param options the options to be set
	 */
	public static void definePrivateMessageOptions(PrivateMessage pm, PostFormOptions options) {
		if (options != null) {
			pm.setHtmlEnabled(options.isHtmlEnabled());
			pm.setBbCodeEnabled(options.isBbCodeEnabled());
			pm.setSmiliesEnabled(options.isSmiliesEnabled());
			pm.setSignatureEnabled(options.getAppendSignature());
		}
	}
}
