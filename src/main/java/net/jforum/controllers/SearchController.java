package net.jforum.controllers;

import net.jforum.actions.helpers.Actions;
import net.jforum.actions.helpers.Domain;
import net.jforum.entities.UserSession;
import net.jforum.entities.util.Pagination;
import net.jforum.entities.util.SearchParams;
import net.jforum.entities.util.SearchResult;
import net.jforum.repository.CategoryRepository;
import net.jforum.repository.SearchRepository;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.ParseException;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

/**
 * @author Filipe Sabella
 * @author Rafael Steil
 */
@Resource
@Path(Domain.SEARCH)
public class SearchController {
	private JForumConfig config;
	private SearchRepository searchRepository;
	private CategoryRepository categoryRepository;
	private UserSession userSession;
	private final Result result;

	public SearchController(CategoryRepository categoryRepository,
			JForumConfig config, SearchRepository searchRepository,
			UserSession userSession, Result result) {
		this.categoryRepository = categoryRepository;
		this.config = config;
		this.searchRepository = searchRepository;
		this.userSession = userSession;
		this.result = result;
	}

	/**
	 * Shows the page to start a new search
	 */
	public void filters() {
		this.result.include("categories",
				this.categoryRepository.getAllCategories());
	}

	public void execute(SearchParams params) {
		if (StringUtils.isEmpty(params.getQuery())
				&& StringUtils.isEmpty(params.getUser())) {
			this.result.redirectTo(Actions.FILTERS);
		} else {
			try {
				params.setMaxResults(this.config
						.getInt(ConfigKeys.TOPICS_PER_PAGE));

				SearchResult result = this.searchRepository.search(params)
						.filter(this.userSession.getRoleManager());

				Pagination pagination = new Pagination(this.config,
						params.getStart()).forSearch(result.getTotalRecords());

				this.result.include("results", result.getResults());
				this.result.include("searchParams", params);
				this.result.include("pagination", pagination);
				this.result.include("categories",
						this.categoryRepository.getAllCategories());
			} catch (ParseException e) {
				this.result.include("parseError", true);
				this.result.include("parseErrorMessage", e.toString());
			}
		}
	}
}
