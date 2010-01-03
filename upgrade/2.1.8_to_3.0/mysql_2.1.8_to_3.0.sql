delete from jforum_sessions;

# Avatar
drop table if exists jforum_avatar;
create table jforum_avatar (
	id int not null primary key auto_increment,
	filename varchar(255),
	avatar_type varchar(10),
	width int default 0,
	height int default 0
);

# Post Report
drop table if exists jforum_post_report;
create table jforum_post_report (
	report_id int not null primary key auto_increment,
	post_id int not null,
	report_date datetime,
	report_description text,
	user_id int,
	report_status varchar(15)
);

# Forums Limited Time
drop table if exists jforum_forums_limited_time;
create table jforum_forums_limited_time (
	id int not null primary key auto_increment,
	forum_id int not null,
	limited_time bigint
);

# Tags
drop table if exists jforum_topics_tag;
create table jforum_topics_tag (
	tag_id int not null primary key auto_increment,
	topic_id int not null,
	tag_name varchar(100) not null
);

# Polls
alter table jforum_vote_voters add voter_id int not null primary key auto_increment;
alter table jforum_vote_results drop vote_option_id;
alter table jforum_vote_results add vote_option_id int not null primary key auto_increment;

# Users
alter table jforum_users change user_active user_active tinyint(1) default 1;
alter table jforum_users drop user_session_page;
alter table jforum_users drop themes_id;
alter table jforum_users drop user_level;
alter table jforum_users drop user_karma;
alter table jforum_users drop user_new_privmsg;
alter table jforum_users drop user_unread_privmsg;
alter table jforum_users drop user_last_privmsg;
alter table jforum_users change user_timezone user_timezone varchar(10);
alter table jforum_users change user_style user_style int default 0;
alter table jforum_users change user_lang user_lang varchar(10);
alter table jforum_users change user_dateformat user_dateformat varchar(20);
alter table jforum_users drop column user_avatar_type;
alter table jforum_users change rank_id rank_id int;
alter table jforum_users add avatar_id int default null;
alter table jforum_users change deleted deleted tinyint(1) default 0;
alter table jforum_users add user_total_privmsgs int default 0;
update jforum_users set user_active = 1 where user_active is null;
update jforum_users set deleted = 0 where deleted is null;
update jforum_users set rank_id = null where rank_id = 0;

# Forums
alter table jforum_forums change categories_id category_id int not null;
alter table jforum_forums change forum_desc forum_description varchar(255);
alter table jforum_forums drop forum_topics;
alter table jforum_forums change forum_last_post_id forum_last_post_id int default 0;
alter table jforum_forums change moderated forum_moderated tinyint(1) default 0;
alter table jforum_forums add forum_allow_anonymous_posts  tinyint(1) default 0;
create index forums_category on jforum_forums(category_id);
	
# Topics
alter table jforum_topics change topic_title topic_subject varchar(110) not null;
alter table jforum_topics change topic_time topic_date datetime;
alter table jforum_topics change topic_last_post_id topic_last_post_id int default 0;
alter table jforum_topics change topic_first_post_id topic_first_post_id int default 0;
alter table jforum_topics change topic_moved_id topic_moved_id int default 0;
alter table jforum_topics change moderated need_moderate tinyint(1) default 0;
alter table jforum_topics add has_attachment tinyint(1) default 0;
update jforum_topics set topic_vote_id = null where topic_vote_id = 0;

# Topics Watch
create temporary table tmp_twatch select distinct topic_id, user_id, is_read from jforum_topics_watch;
delete from jforum_topics_watch;
alter table jforum_topics_watch add topics_watch_id int not null primary key auto_increment;
insert into jforum_topics_watch (topic_id, user_id, is_read) select topic_id, user_id, is_read from tmp_twatch;
alter table jforum_topics_watch add constraint topic_id unique(topic_id, user_id);
drop table tmp_twatch;

# Posts
alter table jforum_posts change post_time post_date datetime;
alter table jforum_posts change poster_ip poster_ip varchar(50);

alter table jforum_posts add post_text text not null;
alter table jforum_posts add post_subject varchar(255);

create temporary table posts_text_tmp as select post_id, post_subject, post_text from jforum_posts_text;
	
update jforum_posts p, posts_text_tmp t set p.post_subject = t.post_subject, p.post_text = t.post_text where p.post_id = t.post_id;
update jforum_posts set post_edit_count = 0 where post_edit_count is null;

drop table posts_text_tmp;	
drop table jforum_posts_text;

# Attachments
alter table jforum_attach drop privmsgs_id;
alter table jforum_attach add download_count int default 0;
alter table jforum_attach add physical_filename varchar(255);
alter table jforum_attach add real_filename varchar(255);
alter table jforum_attach add description varchar(255);
alter table jforum_attach add mimetype varchar(50);
alter table jforum_attach add upload_date datetime;
alter table jforum_attach add filesize bigint;
alter table jforum_attach add thumb tinyint(1) default 0;
alter table jforum_attach add file_extension varchar(6);

create temporary table attach_desc_tmp as select * from jforum_attach_desc;

update jforum_attach a, attach_desc_tmp t set a.download_count = t.download_count, 
	a.physical_filename = t.physical_filename, a.real_filename = t.real_filename, 
	a.description = t.description, a.mimetype = t.mimetype, a.upload_date = t.upload_time, 
	a.filesize = t.filesize, a.thumb = t.thumb
	where a.attach_id = t.attach_id;

drop table jforum_attach_desc;

# Sessions
alter table jforum_sessions drop session_page;
alter table jforum_sessions drop session_logged_int;
alter table jforum_sessions drop session_id;
alter table jforum_sessions change session_time session_last_accessed datetime;
alter table jforum_sessions add session_last_visit datetime;
alter table jforum_sessions change session_user_id user_id int not null;
alter table jforum_sessions change session_ip session_ip varchar(50);

# Categories
alter table jforum_categories change categories_id category_id int not null auto_increment;
alter table jforum_categories change title category_title varchar(100) not null;
alter table jforum_categories change display_order category_order int default 0;
alter table jforum_categories change moderated category_moderated tinyint(1) default 0;

# Smilies
alter table jforum_smilies drop url;

# Groups
alter table jforum_groups drop parent_id;

# Privmsgs
alter table jforum_privmsgs change privmsgs_ip privmsgs_ip varchar(50);
alter table jforum_privmsgs add privmsgs_text text;
update jforum_privmsgs p set p.privmsgs_text = (select pt.privmsgs_text from jforum_privmsgs_text pt where pt.privmsgs_id = p.privmsgs_id and pt.privmsgs_text is not null);
drop table jforum_privmsgs_text;

# Roles
alter table jforum_roles drop user_id;

# Role Values
alter table jforum_role_values change role_value role_value int not null;

# Permissions
insert into jforum_roles (name, group_id) select 'administrator', group_id from jforum_roles where name = 'perm_administration' and group_id > 0;
delete from jforum_role_values;
delete from jforum_roles where name <> 'administrator';

# Configs
select config_value into @oldTotal from jforum_config where config_name = 'most.users.ever.online';
select config_value into @oldDate from jforum_config where config_name = 'most.users.ever.online.date';
delete from jforum_config where config_name in ('most.users.ever.online', 'most.users.ever.online.date');
insert into jforum_config (config_name, config_value) values ('most.users.ever.online', concat(@oldDate, "/", @oldTotal));
	
# Ranking
update jforum_ranks set rank_special = 0 where rank_special is null;