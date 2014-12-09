package com.winbook.layer.controller;

import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.routing.TemplateRoute;

public class WinbookComponent extends Component {
	
	public WinbookComponent() 
	{
			
		this.getServers().add(Protocol.HTTP);
		this.getClients().add(Protocol.FILE);
		
		TemplateRoute imagesDirRoute = this.getDefaultHost().attach("/images/", new Directory(getContext().createChildContext(), "war:///images"));
		TemplateRoute stylesDirRoute = this.getDefaultHost().attach("/styles/", new Directory(getContext().createChildContext(), "war:///styles"));
		TemplateRoute scriptsDirRoute = this.getDefaultHost().attach("/scripts/", new Directory(getContext().createChildContext(), "war:///scripts"));
		
		
		this.getDefaultHost().attachDefault(new WinbookApplication());
		
	}

}
