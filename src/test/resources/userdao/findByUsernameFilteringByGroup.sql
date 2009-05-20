INSERT INTO jforum_groups (group_id, group_name) values (1, 'group1')
INSERT INTO jforum_groups (group_id, group_name) values (2, 'group2')
INSERT INTO jforum_groups (group_id, group_name) values (3, 'group3')

INSERT INTO jforum_users (user_id, username, user_password, user_email, user_active, USER_ATTACHSIG, USER_ALLOWAVATAR, USER_ALLOWBBCODE, USER_ALLOWHTML, DELETED, USER_NOTIFY_ALWAYS, USER_NOTIFY_PM, USER_ALLOW_PM, RANK_ID, USER_ALLOWSMILIES, USER_POSTS, USER_VIEWEMAIL, USER_ALLOW_VIEWONLINE, USER_NOTIFY_TEXT, user_notify) VALUES (1, 'USER1', 'x', 'y', 0, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_users (user_id, username, user_password, user_email, user_active, USER_ATTACHSIG, USER_ALLOWAVATAR, USER_ALLOWBBCODE, USER_ALLOWHTML, DELETED, USER_NOTIFY_ALWAYS, USER_NOTIFY_PM, USER_ALLOW_PM, RANK_ID, USER_ALLOWSMILIES, USER_POSTS, USER_VIEWEMAIL, USER_ALLOW_VIEWONLINE, USER_NOTIFY_TEXT, user_notify) VALUES (2, 'user2', 'x', 'y', 0, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0, 0)
INSERT INTO jforum_users (user_id, username, user_password, user_email, user_active, USER_ATTACHSIG, USER_ALLOWAVATAR, USER_ALLOWBBCODE, USER_ALLOWHTML, DELETED, USER_NOTIFY_ALWAYS, USER_NOTIFY_PM, USER_ALLOW_PM, RANK_ID, USER_ALLOWSMILIES, USER_POSTS, USER_VIEWEMAIL, USER_ALLOW_VIEWONLINE, USER_NOTIFY_TEXT, user_notify) VALUES (3, 'user3', 'x', 'y', 0, 0, 0, 0, 0, 0, 0, 0, 0, null, 0, 0, 0, 0, 0, 0)

INSERT INTO jforum_user_groups (group_id, user_id) VALUES (1, 1)
INSERT INTO jforum_user_groups (group_id, user_id) VALUES (2, 1)
INSERT INTO jforum_user_groups (group_id, user_id) VALUES (2, 2)
INSERT INTO jforum_user_groups (group_id, user_id) VALUES (3, 3)