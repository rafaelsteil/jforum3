INSERT INTO jforum_categories (category_id, category_title, category_order, category_moderated) VALUES (1, 'c1', 0, 0);
INSERT INTO jforum_categories (category_id, category_title, category_order, category_moderated) VALUES (2, 'c2', 0, 0);

insert  into `jforum_shoutbox`(`id`,`category_id`,`shout_length`,`allow_anonymous`,`disabled`) values (1,1,200,1,0),(2,2,200,1,0);
insert  into `jforum_shouts`(`shout_id`,`shout_box_id`,`user_id`,`shouter_name`,`shout_text`,`shouter_ip`,`shout_time`) 
values (1,1,1,'Guest','222','127.0.0.1','2009-09-08 00:00:00'),(2,1,1,'Guest','333','127.0.0.1','2009-09-08 00:00:00'),
(3,2,2,'Guest','444','127.0.0.1','2009-09-08 00:00:00');
