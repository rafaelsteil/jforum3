INSERT INTO jforum_categories (category_id, category_title, category_order, category_moderated) VALUES (1, 'c1', 0, 0)
INSERT INTO jforum_forums (forum_id, category_id, forum_name, forum_order, forum_moderated) VALUES (1, 1, 'f1.1', 0, 0)
INSERT INTO jforum_forums (forum_id, category_id, forum_name, forum_order, forum_moderated) VALUES (2, 1, 'f1.2', 0, 0)
INSERT INTO jforum_users (user_id, username, user_password, user_email, user_active, USER_ATTACHSIG, USER_ALLOWAVATAR, USER_ALLOWBBCODE, USER_ALLOWHTML, DELETED, USER_NOTIFY_ALWAYS, USER_NOTIFY_PM, USER_ALLOW_PM, RANK_ID, USER_ALLOWSMILIES, USER_POSTS, USER_VIEWEMAIL, USER_ALLOW_VIEWONLINE, USER_NOTIFY_TEXT, user_notify) VALUES (1, 'u1', 'x', 'y', 0, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0, 0)

INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id,has_attachment) VALUES (1, 1, 't1.1', 1, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 0, 0,false)
INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id,has_attachment) VALUES (2, 1, 't1.2', 1, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 0, 0,false)
INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id,has_attachment) VALUES (3, 1, 't1.3', 1, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 0, 0,false)
INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id,has_attachment) VALUES (4, 1, 't1.4', 1, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 0, 0,false)
INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id,has_attachment) VALUES (5, 2, 't2.1', 1, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 0, 0,false)
INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id,has_attachment) VALUES (6, 1, 't1.5', 1, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 0, 0,false)

INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (1, 1, 1, '2008-06-11 14:51:32', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (2, 2, 1, '2008-06-11 14:52:33', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (3, 3, 1, '2008-06-11 14:53:34', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (4, 4, 1, '2008-06-11 14:54:35', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (5, 5, 1, '2008-06-11 14:59:19', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (6, 6, 1, '2008-06-10 09:49:11', 0, 0, 0, 0, 0, 0, 0) 

UPDATE jforum_topics SET topic_last_post_id = 1 WHERE topic_id = 1
UPDATE jforum_topics SET topic_last_post_id = 2 WHERE topic_id = 2
UPDATE jforum_topics SET topic_last_post_id = 3 WHERE topic_id = 3
UPDATE jforum_topics SET topic_last_post_id = 4 WHERE topic_id = 4
UPDATE jforum_topics SET topic_last_post_id = 5 WHERE topic_id = 5
UPDATE jforum_topics SET topic_last_post_id = 6 WHERE topic_id = 6
