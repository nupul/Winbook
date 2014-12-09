package com.winbook.test;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;

import com.winbook.domainobjects.WinCondition;
import com.winbook.exceptions.NoSuchProjectExistsException;
import com.winbook.exceptions.NoWallForProjectExistsException;
import com.winbook.layer.Resources.WallPage;
import com.winbook.layer.Resources.WallPageResource;

public class TestWallPageResource {
	
	@Test
	public void testWallContentExists() throws Exception
	{
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, 8182);
		component.getInternalRouter().attach("/{project}/{wallName}", WallPageResource.class);
		
		Client client = component.getContext().getClientDispatcher();
		
		Request request = new Request(Method.GET, new Reference("riap://component/ezbay/wall"));
		
		
		component.start();
		
		Response response = client.handle(request);
		
		if(response.isEntityAvailable())
		{			
			response.getEntity().write(System.out);
			System.out.println("");
			response.getEntity().release();
		}
			
		
		assertTrue("Request to resource failed", response.getStatus().isSuccess());
		
		component.stop();
		
	}
	
	@Test
	public void testWallPageData() throws NoSuchProjectExistsException, NoWallForProjectExistsException
	{
		WallPage winWall = new WallPage("ezbay","wall");
		
		assertEquals("Winbook: Ezbay/Wall", winWall.getPageTitle());
		
		List<WinCondition> lWinConditions = winWall.getWinConditions();
		
		assertNotNull(lWinConditions);
	
		
		
	}
	
	
	
	

}
