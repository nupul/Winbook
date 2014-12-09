package com.winbook.layer.controller;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;

import com.winbook.layer.Resources.AgreementResource;
import com.winbook.layer.Resources.AnalyticsResource;
import com.winbook.layer.Resources.CategoryResource;
import com.winbook.layer.Resources.CriterionResource;
import com.winbook.layer.Resources.CriterionScoreResource;
import com.winbook.layer.Resources.GoalResource;
import com.winbook.layer.Resources.IssueResource;
import com.winbook.layer.Resources.OptionResource;
import com.winbook.layer.Resources.PrioritizationResource;
import com.winbook.layer.Resources.PriorityResource;
import com.winbook.layer.Resources.ProjectResource;
import com.winbook.layer.Resources.ProjectsResource;
import com.winbook.layer.Resources.UserMembership;
import com.winbook.layer.Resources.UserResource;
import com.winbook.layer.Resources.WallPageResource;
import com.winbook.layer.Resources.WinConditionCategorizerResource;
import com.winbook.layer.Resources.WinConditionResource;
import com.winbook.layer.accessControl.CookieAuthenticator;
import com.winbook.layer.accessControl.LoginHandler;
import com.winbook.layer.accessControl.LogoutHandler;
import com.winbook.layer.accessControl.MyChallengeAuthenticator;
import com.winbook.layer.accessControl.UserVerifier;
import com.winbook.layer.accessControl.WinbookEnroler;
import com.winbook.representations.RepresentationFactory;

public class WinbookApplication extends Application {

	/**
	 * 
	 */
	public WinbookApplication() {
		super();
		getRoles().add(WinbookEnroler.MEMBER);
		getRoles().add(WinbookEnroler.NON_MEMBER);
		getRoles().add(WinbookEnroler.SHAPER);
	}
	
	@Override
	public Restlet createInboundRoot() {

		Router router = new Router(getContext());
		router.attach("/User",UserResource.class);
		router.attach("/User/Membership",UserMembership.class);
		router.attach("/Login", LoginHandler.class);
		router.attach("/Logout", LogoutHandler.class);
		//router.attach("/projects", ProjectsResource.class);
		router.attach("/projects",createProjectRoutes());
		router.attachDefault(LoginHandler.class);

		return router;
	
	}
	
	private Restlet createProjectRoutes()
	{
		Router projectRouter = new Router(getContext());
		
		//ChallengeAuthenticator guard = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "winbook");
		//guard.setEnroler(new WinbookEnroler());
		CookieAuthenticator guard = new CookieAuthenticator(getContext(), "winbook");
		guard.setVerifier(new UserVerifier()); 
		guard.setNext(projectRouter);
		
		projectRouter.attachDefault(ProjectsResource.class);
		projectRouter.attach("/{project}", ProjectResource.class);
		projectRouter.attach("/{project}/{wallName}/WinConditions", WinConditionResource.class);
		projectRouter.attach("/{project}/{wallName}", ProjectResource.class);
		projectRouter.attach("/{project}/{wallName}/WinConditions/{id}", WinConditionResource.class); 
		projectRouter.attach("/{project}/{wallName}/WinConditions/{wcid}/Issues/{id}", IssueResource.class);
		projectRouter.attach("/{project}/{wallName}/WinConditions/{wcid}/Issues", IssueResource.class);
		projectRouter.attach("/{project}/{wallName}/WinConditions/{wcid}/Issues/{issueid}/Options", OptionResource.class);
		projectRouter.attach("/{project}/{wallName}/WinConditions/{wcid}/Issues/{issueid}/Options/{optionid}",OptionResource.class);
		projectRouter.attach("/{project}/{wallName}/Categories",CategoryResource.class);
		projectRouter.attach("/{project}/{wallName}/Categories/{id}",CategoryResource.class);
		projectRouter.attach("/{project}/{wallName}/WinConditionCategorization",WinConditionCategorizerResource.class);
		projectRouter.attach("/{project}/{wallName}/WinConditions/{wcid}/Agreements",AgreementResource.class);
		projectRouter.attach("/{project}/{wallName}/WinConditions/{wcid}/Issues/{issueid}/Options/{optionid}/Agreements",AgreementResource.class);
		projectRouter.attach("/{project}/{wallName}/WinConditions/{wcid}/Agreements/{id}",AgreementResource.class);
		projectRouter.attach("/{project}/{wallName}/WinConditions/{wcid}/Issues/{issueid}/Options/{optionid}/Agreements/{id}",AgreementResource.class);
		
		projectRouter.attach("/{project}/{wallName}/Benefits", BenefitsResource.class);
		projectRouter.attach("/{project}/{wallName}/Items/{itemType}/{itemId}/GoalScores/{id}",CriterionScoreResource.class);
		projectRouter.attach("/{project}/{wallName}/Prioritization/Criteria/{id}", CriterionResource.class);
		projectRouter.attach("/{project}/{wallName}/Prioritization/Criteria", PrioritizationResource.class);
		projectRouter.attach("/{project}/{wallName}/Criteria/Business",GoalResource.class);
		projectRouter.attach("/{project}/{wallName}/Criteria/Business/{id}",GoalResource.class);
		projectRouter.attach("/{project}/{wallName}/Items/{itemType}/{itemId}/Priority", PriorityResource.class);
		
		projectRouter.attach("/{project}/{wallName}/Analytics", AnalyticsResource.class);
		
		return guard;
	}
	
	

}
