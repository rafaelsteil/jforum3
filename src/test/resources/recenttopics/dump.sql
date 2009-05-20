INSERT INTO jforum_categories (category_id, category_title, category_order, category_moderated) VALUES (1, 'c1', 0, 0)
INSERT INTO jforum_categories (category_id, category_title, category_order, category_moderated) VALUES (2, 'c2', 0, 0)

INSERT INTO jforum_forums (forum_id, category_id, forum_name, forum_order, forum_moderated, forum_allow_anonymous_posts) VALUES (1, 1, 'f1.1', 0, 0, 1)
INSERT INTO jforum_forums (forum_id, category_id, forum_name, forum_order, forum_moderated, forum_allow_anonymous_posts) VALUES (2, 1, 'f1.2', 0, 0, 1)
INSERT INTO jforum_forums (forum_id, category_id, forum_name, forum_order, forum_moderated, forum_allow_anonymous_posts) VALUES (3, 2, 'f2.1', 0, 0, 1)
                                                                                                                                                                                                                                                                                
INSERT INTO jforum_users (user_id, username, user_password, user_email, user_active, USER_ATTACHSIG, USER_ALLOWAVATAR, USER_ALLOWBBCODE, USER_ALLOWHTML, DELETED, USER_NOTIFY_ALWAYS, USER_NOTIFY_PM, USER_ALLOW_PM, RANK_ID, USER_ALLOWSMILIES, USER_POSTS, USER_VIEWEMAIL, USER_ALLOW_VIEWONLINE, USER_NOTIFY_TEXT, user_notify) VALUES (1, 'u1', 'x', 'y', 0, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_users (user_id, username, user_password, user_email, user_active, USER_ATTACHSIG, USER_ALLOWAVATAR, USER_ALLOWBBCODE, USER_ALLOWHTML, DELETED, USER_NOTIFY_ALWAYS, USER_NOTIFY_PM, USER_ALLOW_PM, RANK_ID, USER_ALLOWSMILIES, USER_POSTS, USER_VIEWEMAIL, USER_ALLOW_VIEWONLINE, USER_NOTIFY_TEXT, user_notify) VALUES (2, 'u2', 'x', 'y', 0, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_users (user_id, username, user_password, user_email, user_active, USER_ATTACHSIG, USER_ALLOWAVATAR, USER_ALLOWBBCODE, USER_ALLOWHTML, DELETED, USER_NOTIFY_ALWAYS, USER_NOTIFY_PM, USER_ALLOW_PM, RANK_ID, USER_ALLOWSMILIES, USER_POSTS, USER_VIEWEMAIL, USER_ALLOW_VIEWONLINE, USER_NOTIFY_TEXT, user_notify) VALUES (3, 'u3', 'x', 'y', 0, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0, 0)
                                                                                                                                                                                                                                                                                

INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id,has_attachment) VALUES (1, 1, 't1.1', 1, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 0, 0,false)
INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id,has_attachment) VALUES (2, 1, 't1.2', 2, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 0, 0,false)
INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id,has_attachment) VALUES (3, 2, 't2.1', 2, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 0, 0,false)
INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id,has_attachment) VALUES (4, 3, 't3.1', 1, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 0, 0,false)
INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id,has_attachment) VALUES (5, 3, 't3.2', 1, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 0, 0,false)
INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id,has_attachment) VALUES (6, 1, 't1.3', 1, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 0, 0,false)
INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id,has_attachment) VALUES (7, 2, 't2.2', 2, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 0, 0,false)
INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id,has_attachment) VALUES (8, 3, 't3.3', 1, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 0, 0,false)

INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (1, 1, 1, '2008-06-11 14:54:32', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (2, 2, 1, '2008-06-11 14:54:33', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (3, 3, 2, '2008-06-11 14:54:34', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (4, 4, 2, '2008-06-11 14:54:35', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (5, 5, 1, '2008-06-11 14:54:36', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (6, 6, 1, '2008-06-11 14:54:37', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (7, 7, 2, '2008-06-11 14:54:38', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (8, 8, 1, '2008-06-11 14:54:39', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (9, 5, 2, '2008-06-11 14:54:42', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (10, 6, 1, '2008-06-11 14:54:43', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (11, 7, 2, '2008-06-11 14:54:44', 0, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (12, 8, 2, '2008-06-11 14:54:45', 0, 0, 0, 0, 0, 0, 0)

UPDATE jforum_topics SET topic_first_post_id = 1, topic_last_post_id = 9 WHERE topic_id = 5
UPDATE jforum_topics SET topic_first_post_id = 2, topic_last_post_id = 10 WHERE topic_id = 6
UPDATE jforum_topics SET topic_first_post_id = 3, topic_last_post_id = 11 WHERE topic_id = 7
UPDATE jforum_topics SET topic_first_post_id = 4, topic_last_post_id = 12 WHERE topic_id = 8
UPDATE jforum_topics SET topic_first_post_id = 5, topic_last_post_id = 5 WHERE topic_id = 1
UPDATE jforum_topics SET topic_first_post_id = 6, topic_last_post_id = 6 WHERE topic_id = 2
UPDATE jforum_topics SET topic_first_post_id = 7, topic_last_post_id = 7 WHERE topic_id = 3
UPDATE jforum_topics SET topic_first_post_id = 8, topic_last_post_id = 8 WHERE topic_id = 4