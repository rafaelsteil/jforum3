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
package net.jforum.core.support.hibernate;

import java.util.List;

import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.ModerationLog;
import net.jforum.services.ModerationService;
import net.jforum.actions.helpers.ApproveInfo;

/**
 * @author Rafael Steil
 */
public class AOPTestModerationService extends ModerationService {

    @Override
    public void deleteTopics(List<Topic> topics, ModerationLog moderationLog) {
    }


    @Override
    public void doApproval(int forumId, List<ApproveInfo> infos) {

    }

    @Override
    public void moveTopics(int toForumId, ModerationLog moderationLog, int... topicIds) {

    }

}
