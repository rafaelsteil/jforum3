package net.jforum.actions;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.core.support.vraptor.ViewPropertyBag;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.Pagination;
import net.jforum.entities.util.SearchParams;
import net.jforum.entities.util.SearchResult;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.SearchRepository;
import net.jforum.services.ViewService;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.ParseException;
import org.vraptor.annotations.Component;
import org.vraptor.annotations.Parameter;

/**
 * @author Filipe Sabella
 * @author Rafael Steil
 */
@Component(Domain.SEARCH)
public class SearchActions {
	private JForumConfig config;
	private ViewPropertyBag propertyBag;
	private SearchRepository searchRepository;
	private CategoryRepository categoryRepository;
	private UserSession userSession;
	private ViewService viewService;

	public SearchActions(ViewPropertyBag propertyBag, CategoryRepository categoryRepository,
		JForumConfig config, SearchRepository searchRepository, UserSession userSession,
		ViewService viewService) {
		this.propertyBag = propertyBag;
		this.categoryRepository = categoryRepository;
		this.config = config;
		this.searchRepository = searchRepository;
		this.userSession = userSession;
		this.viewService = viewService;
	}

	/**
	 * Shows the page to start a new search
	 */
	public void filters() {
		this.propertyBag.put("categories", this.categoryRepository.getAllCategories());
	}

	public void execute(@Parameter(key = "params") SearchParams params) {
		if (StringUtils.isEmpty(params.getQuery()) && StringUtils.isEmpty(params.getUser())) {
			this.viewService.redirectToAction(Actions.FILTERS);
		}
		else {
			try {
				params.setMaxResults(this.config.getInt(ConfigKeys.TOPICS_PER_PAGE));

				SearchResult result = this.searchRepository.search(params)
					.filter(this.userSession.getRoleManager());

				Pagination pagination = new Pagination(this.config, params.getStart()).forSearch(result.getTotalRecords());

				this.propertyBag.put("results", result.getResults());
				this.propertyBag.put("searchParams", params);
				this.propertyBag.put("pagination", pagination);
				this.propertyBag.put("categories", this.categoryRepository.getAllCategories());
			}
			catch (ParseException e) {
				this.propertyBag.put("parseError", true);
				this.propertyBag.put("parseErrorMessage", e.toString());
			}
		}
	}
}
