insert into jforum_groups (group_id, group_name) values (1, 'Group 1')
insert into jforum_groups (group_id, group_name) values (2, 'Group 2')
	
insert into jforum_roles (role_id, name, group_id) values (1, 'some_role', 1)
insert into jforum_roles (role_id, name, group_id) values (2, 'some_other_role', 1)
insert into jforum_roles (role_id, name, group_id) values (3, 'moderate_forum', 1)
insert into jforum_roles (role_id, name, group_id) values (4, 'moderate_forum', 2)
	
insert into jforum_role_values (role_id, role_value) values (2, 1)
insert into jforum_role_values (role_id, role_value) values (3, 1)
insert into jforum_role_values (role_id, role_value) values (3, 2)
insert into jforum_role_values (role_id, role_value) values (4, 1)