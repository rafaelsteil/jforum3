-- 
--  Groups
-- 
INSERT INTO jforum_groups (group_id, group_name, group_description ) VALUES (1, 'General', 'General Users');
INSERT INTO jforum_groups (group_id, group_name, group_description ) VALUES (2, 'Administration', 'Admin Users');

-- 
--  Users
-- 
INSERT INTO jforum_users (user_id, username, user_password, user_regdate ) VALUES (1, 'Anonymous', 'nopass', NOW());
INSERT INTO jforum_users (user_id, username, user_password, user_regdate, user_posts ) VALUES (2, 'Admin', '21232f297a57a5a743894a0e4a801fc3', NOW(), 1);

-- 
--  User Groups
-- 
INSERT INTO jforum_user_groups (group_id, user_id) VALUES (1, 1);
INSERT INTO jforum_user_groups (group_id, user_id) VALUES (2, 2);

-- 
--  Smilies
--
INSERT INTO jforum_smilies (code, disk_name) VALUES (':)', '3b63d1616c5dfcf29f8a7a031aaa7cad.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':-)', '3b63d1616c5dfcf29f8a7a031aaa7cad.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':D', '283a16da79f3aa23fe1025c96295f04f.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':-D', '283a16da79f3aa23fe1025c96295f04f.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':(', '9d71f0541cff0a302a0309c5079e8dee.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':mrgreen:', 'ed515dbff23a0ee3241dcc0a601c9ed6.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':-o', '47941865eb7bbc2a777305b46cc059a2.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':shock:', '385970365b8ed7503b4294502a458efa.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':?:', '0a4d7238daa496a758252d0a2b1a1384.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES ('8)', 'b2eb59423fbf5fa39342041237025880.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':lol:', '97ada74b88049a6d50a6ed40898a03d7.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':x', '1069449046bcd664c21db15b1dfedaee.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':P', '69934afc394145350659cd7add244ca9.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':-P', '69934afc394145350659cd7add244ca9.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':oops:', '499fd50bc713bfcdf2ab5a23c00c2d62.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':cry:', 'c30b4198e0907b23b8246bdd52aa1c3c.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':evil:', '2e207fad049d4d292f60607f80f05768.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':twisted:', '908627bbe5e9f6a080977db8c365caff.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':roll:', '2786c5c8e1a8be796fb2f726cca5a0fe.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':wink:', '8a80c6485cd926be453217d59a84a888.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (';)', '8a80c6485cd926be453217d59a84a888.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (';-)', '8a80c6485cd926be453217d59a84a888.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':!:', '9293feeb0183c67ea1ea8c52f0dbaf8c.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':?', '136dd33cba83140c7ce38db096d05aed.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':idea:', '8f7fb9dd46fb8ef86f81154a4feaada9.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':arrow:', 'd6741711aa045b812616853b5507fd2a.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':hunf:', '0320a00cb4bb5629ab9fc2bc1fcc4e9e.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':-(', '9d71f0541cff0a302a0309c5079e8dee.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':XD:', '49869fe8223507d7223db3451e5321aa.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':thumbup:', 'e8a506dc4ad763aca51bec4ca7dc8560.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':thumbdown:', 'e78feac27fa924c4d0ad6cf5819f3554.gif');
INSERT INTO jforum_smilies (code, disk_name) VALUES (':|', '1cfd6e2a9a2c0cf8e74b49b35e2e46c7.gif');
--
-- Demonstration Forum
--
-- INSERT INTO jforum_categories VALUES (1,'Category Test',1,0);
-- INSERT INTO jforum_forums VALUES (1,1,'Test Forum','This is a test forum',1,1,1,0);
-- INSERT INTO jforum_topics VALUES (1,1,'Welcome to JForum',2,current_timestamp,1,0,0,0,0,1,1,0,0);
-- INSERT INTO jforum_posts VALUES (1,1,1,2,current_timestamp,'127.0.0.1',1,0,1,1,null,0,1,0,0);
-- INSERT INTO jforum_posts_text VALUES (1,'[b][color=blue][size=18]Congratulations :!: [/size][/color][/b]\nYou have completed the installation, and JForum is up and running. \n\nTo start administering the board, login as [i]Admin / <the password you supplied in the installer>[/i] and access the [b][url=/admBase/login.page]Admin Control Panel[/url][/b] using the link that shows up in the bottom of the page. There you will be able to create Categories, Forums and much more  :D  \n\nFor more information and support, please refer to the following pages:\n\n:arrow: Community forum: http://www.jforum.net/community.jsp\n:arrow: Documentation: http://www.jforum.net/doc\n\nThank you for choosing JForum.\n\n[url=http://www.jforum.net/doc/Team]The JForum Team[/url]\n\n','Welcome to JForum');

-- 
--  Admin Roles
-- 
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'administrator');

--
-- Update sequences
--
SELECT SETVAL('jforum_posts_seq', MAX(post_id)) FROM jforum_posts;
SELECT SETVAL('jforum_categories_seq', MAX(category_id)) FROM jforum_categories;
SELECT SETVAL('jforum_forums_seq', MAX(forum_id)) FROM jforum_forums;
SELECT SETVAL('jforum_topics_seq', MAX(topic_id)) FROM jforum_topics;
SELECT SETVAL('jforum_users_seq', MAX(user_id)) FROM jforum_users;
SELECT SETVAL('jforum_groups_seq', MAX(group_id)) FROM jforum_groups;
SELECT SETVAL('jforum_smilies_seq', MAX(smilie_id)) FROM jforum_smilies;
SELECT SETVAL('jforum_roles_seq', MAX(role_id)) FROM jforum_roles;
