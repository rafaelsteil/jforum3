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
package net.jforum.plugins.tagging;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.jforum.core.hibernate.HibernateGenericDAO;
import net.jforum.entities.Forum;
import net.jforum.entities.Topic;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * @author Bill
 *
 */
public class TagDAO extends HibernateGenericDAO<Tag> implements TagRepository {

	public TagDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Tag> getTags(Topic topic) {
		return this.session().createCriteria(this.persistClass)
		.add(Restrictions.eq("topic", topic))
		.setCacheRegion("tagDAO")
		.setComment("tagDAO.getTags")
		.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> getAll() {
		Query query = getAllTagQuery();
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> getAll(int start, int count) {
		Query query = getAllTagQuery();
		query.setFirstResult(start);
		query.setMaxResults(count);
		return query.list();
	}

	/**
	 * get the Hot tags and its count
	 * order by the tag name
	 * @deprecated
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public Map<String,Long> getHotTags(int limit) {
		List<Object[]> _list = this.session().createQuery("select name,count(*) from Tag group by name order by name")
		  .setMaxResults(limit)
		  .setComment("tagDAO.getHotTags(int)")
		  .list();

		return this.transferListMap(_list);
	}

	/**
	 * @see net.jforum.plugins.tagging.TagRepository#getHotTags(java.util.List, int)
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Long> getHotTags(List<Topic> topics, int limit) {
		if(topics.size()>0){
			List<Object[]> _list =  this.session().createQuery("select name,count(*) from Tag where topic in (:topics) group by name order by name")
			  .setParameterList("topics", topics)
			  .setMaxResults(limit)
			  .setComment("tagDAO.getHotTags(int)")
			  .list();
			return this.transferListMap(_list);
		}

		return new LinkedHashMap<String,Long>();
	}

	/**
	 * @see net.jforum.plugins.tagging.TagRepository#getHotTags(net.jforum.entities.Forum, int)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Long> getHotTags(Forum forum, int limit) {
		/*int count = forum.getTotalTopics();
		List<Topic> topics = forum.getTopics(0, count);*/

		//just need the id of these topics, no need load all the propertise
		List<Topic> topics = this.session().createQuery("select new Topic(topic.id) from Topic as topic where topic.forum = :forum")
							.setParameter("forum", forum).list();
		return this.getHotTags(topics, limit);
	}

	/**
	 * @see net.jforum.plugins.tagging.TagRepository#getAccessableHotTags(java.util.List, int)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Long> getAccessableHotTags(List<Forum> forums, int limit) {
		if(forums == null || forums.size() == 0) {
			return new LinkedHashMap<String,Long>();
		}

		List<Topic> topics = this.session().createQuery("select new Topic(topic.id) from Topic as topic where topic.forum in (:forums)")
		.setParameterList("forums", forums).list();
		return this.getHotTags(topics, limit);
	}

	/**
	 * remove all the tags that name is "tag".
	 */
	public void remove(String tag) {
		this.session().createQuery("delete from Tag as tag where tag.name = :tagName")
					  .setString("tagName", tag)
					  .setComment("tagDAO.remove(Stirng)")
					  .executeUpdate();
	}

	public void update(String oldTag, String newTag) {
		this.session().createQuery("update Tag set name = :newTag where name = :oldTag")
					  .setString("oldTag", oldTag)
					  .setString("newTag", newTag)
					  .setComment("tagDAO.update(String)")
					  .executeUpdate();
	}

	public int count(String name) {
		return  ((Number)this.session().createQuery("select count(*) as c from Tag where name=:name")
			  .setString("name", name)
			  .setComment("tagDAO.count(String)")
			  .uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Topic> getTopics(String tag) {
		return this.session().createQuery("select tag.topic from Tag as tag where tag.name = :name")
		.setString("name", tag)
		.setComment("tagDAo.getTopics(String)")
		.list();
	}

	private Query getAllTagQuery (){
		return this.session().createQuery("select distinct(name) from Tag order by name");
	}

	private Map<String,Long> transferListMap(List<Object[]> maps){
		Map<String,Long>  result = new LinkedHashMap<String,Long> ();
		for(Object[] item : maps){
			String name = (String) item[0];
			Long count = (Long)item[1];
			result.put(name, count);
		}

		return result;
	}

}
