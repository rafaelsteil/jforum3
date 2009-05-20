INSERT INTO jforum_topics (topic_id, forum_id, topic_subject, user_id, topic_date, TOPIC_STATUS, TOPIC_REPLIES, TOPIC_VIEWS, TOPIC_TYPE, TOPIC_VOTE_ID, need_moderate, topic_moved_id, has_attachment) VALUES (7, 1, 't1.7', 1, CURRENT_TIMESTAMP, 0, 0, 0, 0, null, 1, 0,false)

INSERT INTO jforum_posts (post_id, topic_id, user_id, post_date, need_moderate, enable_bbcode, post_edit_count, attach, enable_html, enable_sig, enable_smilies) VALUES (10, 7, 1, '2008-06-11 14:54:32', 0, 0, 0, 0, 0, 0, 0)

update jforum_topics set topic_first_post_id = 1, topic_last_post_id = 1 where topic_id = 1;
update jforum_topics set topic_first_post_id = 4, topic_last_post_id = 4 where topic_id = 2;
update jforum_topics set topic_first_post_id = 6, topic_last_post_id = 6 where topic_id = 3;
update jforum_topics set topic_first_post_id = 10, topic_last_post_id = 10 where topic_id = 7;