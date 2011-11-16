/**
 *
 */
package net.jforum.plugins.tagging;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.jforum.entities.Forum;
import net.jforum.entities.Topic;
import net.jforum.repository.ForumRepository;
import net.jforum.security.RoleManager;

import org.apache.commons.lang.StringUtils;

/**
 * @author Bill
 */

public class TagService {
	private final TagRepository tagRepository;
	private final ForumRepository forumRepository;

	public TagService(TagRepository tagRepository, ForumRepository forumRepository) {
		this.tagRepository = tagRepository;
		this.forumRepository = forumRepository;
	}

	/**
	 * add a string as tags. use comma to split the tag
	 *
	 * @param tagStr
	 * @param topic
	 */
	public void addTag(String tagStr, Topic topic) {
		if (StringUtils.isEmpty(tagStr) || topic == null) {
			return;
		}

		String[] tags = tagStr.split(",");
		for (String tag : tags) {
			this.addOneTag(tag, topic);
		}
	}

	/**
	 * get all the tag of a give topic
	 *
	 * @param topic
	 * @return
	 */
	public List<Tag> getTag(Topic topic) {
		return this.tagRepository.getTags(topic);
	}

	/**
	 * get tag String of a topic
	 *
	 * @param topic
	 * @return
	 */
	public String getTagString(Topic topic) {
		List<Tag> tags = this.getTag(topic);

		if (tags == null || tags.size() == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (Tag tag : tags) {
			sb.append(tag.getName()).append(",");
		}

		sb.deleteCharAt(sb.length() - 1); // del the last comma

		return sb.toString();
	}

	/**
	 * count how times does this tag was ued.
	 *
	 * @param name
	 * @return
	 */
	public int count(String name) {
		if (StringUtils.isNotEmpty(name)) {
			return this.tagRepository.count(name);
		}

		return 0;
	}

	/**
	 * remove a collection of tag
	 *
	 * @param tags
	 */
	public void remove(List<Tag> tags) {
		if (tags == null) {
			return;
		}

		for (Tag tag : tags) {
			this.tagRepository.remove(tag);
		}
	}

	/**
	 * find the topics who use this tag
	 *
	 * @param tag
	 * @return
	 */
	public List<Topic> search(String tag, RoleManager roleManager) {
		List<Topic> l = new ArrayList<Topic>();

		if (tag != null) {
			List<Topic> topics = this.tagRepository.getTopics(tag);
			l = new TagSearchResult(topics, topics.size()).filter(roleManager).getResults();
		}

		return l;
	}

	/**
	 * get "limit" hot tags according to hot rate, group them into "group" groups return <tagName,groupNumber> order by tag name
	 *
	 * @param itmes count
	 * @return
	 * @deprecated replaced by <code>getHotTags(int limit,int group,RoleManager roleManager)</code>
	 */
	@Deprecated
	public Map<String, Integer> getHotTags(int limit, int group) {
		Map<String, Long> hotTagsWithCount = this.tagRepository.getHotTags(limit);
		return this.devideIntoGroup(hotTagsWithCount, group);

	}

	/**
	 * get "limit" hot tags according to hot rate, group them into "group" groups filter the some tag that the user can't access return
	 * <tagName,groupNumber> order by tag name
	 *
	 * @param itmes count
	 * @return
	 */
	public Map<String, Integer> getHotTags(int limit, int group, RoleManager roleManager) {
		List<Forum> forums = getAccessableForum(roleManager);
		Map<String, Long> hotTagsWithCount = this.tagRepository.getAccessableHotTags(forums, limit);
		return this.devideIntoGroup(hotTagsWithCount, group);
	}

	public Map<String, Integer> getHotTags(Forum forum, int limit, int group) {
		Map<String, Long> hotTagsWithCount = this.tagRepository.getHotTags(forum, limit);
		return this.devideIntoGroup(hotTagsWithCount, group);
	}

	/**
	 * according to hot rate, group them into "group" groups
	 *
	 * @param hotTagsWithCount
	 * @param group
	 * @return
	 */
	private Map<String, Integer> devideIntoGroup(Map<String, Long> hotTagsWithCount, int group) {
		Map<String, Integer> hotTagsWithGroudIndex = new LinkedHashMap<String, Integer>();

		if (hotTagsWithCount.size() == 0) {
			return hotTagsWithGroudIndex;
		}

		TreeSet<Long> sortedUniqueValues = new TreeSet<Long>(hotTagsWithCount.values());

		for (String name : hotTagsWithCount.keySet()) {
			Long count = hotTagsWithCount.get(name);
			hotTagsWithGroudIndex.put(name, this.getGroupNo(count, sortedUniqueValues, group));
		}
		return hotTagsWithGroudIndex;
	}

	/**
	 * return Group Index
	 *
	 * @param value
	 * @param sortedUniqueValues
	 * @param group
	 * @return
	 */
	private Integer getGroupNo(Long value, TreeSet<Long> sortedUniqueValues, int group) {
		int index = 0;
		for (Long i : sortedUniqueValues) {
			if (i.equals(value)) {
				break;
			}
			index++;
		}
		return (int) ((index * group) / sortedUniqueValues.size());
	}

	/**
	 * get all the accessable forum
	 *
	 * @param roleManager
	 * @return
	 */
	// TODO: it is better put this function in ForumService
	private List<Forum> getAccessableForum(RoleManager roleManager) {
		List<Forum> accessableForum = new ArrayList<Forum>();
		List<Forum> allForum = this.forumRepository.findAll();
		for (Forum forum : allForum) {
			if (roleManager.isForumAllowed(forum.getId())) {
				accessableForum.add(forum);
			}
		}
		return accessableForum;
	}

	private void addOneTag(String tagStr, Topic topic) {
		tagStr = tagStr.trim();

		if (StringUtils.isEmpty(tagStr)) {
			return;
			// TODO: bad word checking
		}

		Tag tag = new Tag();
		tag.setName(tagStr);
		tag.setTopic(topic);

		this.tagRepository.add(tag);
	}
}
