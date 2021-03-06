package ch.ethz.inf.dbproject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.ethz.inf.dbproject.model.DatastoreInterface;
import ch.ethz.inf.dbproject.model.Project;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;

/**
 * Servlet implementation class Search
 */
@WebServlet(description = "The search page for projects", urlPatterns = { "/Search" })
public final class SearchServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();
		
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		final HttpSession session = request.getSession(true);
		
		/*******************************************************
		 * Construct a table to present all our search results
		 *******************************************************/
		final BeanTableHelper<Project> table = new BeanTableHelper<Project>(
				"projects" 		/* The table html id property */,
				"projectsTable" /* The table html class property */,
				Project.class 	/* The class of the objects (rows) that will bedisplayed */
		);

		// Add columns to the new table

		/*
		 * Column 1: The name of the item (This will probably have to be changed)
		 */
		table.addBeanColumn("Project Name", "title");
		table.addBeanColumn("Description", "description");
		table.addBeanColumn("Base Goal", "baseGoal");
		table.addBeanColumn("Creator", "username");
		table.addBeanColumn("Start Date", "startDate");
		table.addBeanColumn("End Date", "endDate");
		table.addBeanColumn("City", "city");
		table.addBeanColumn("Category", "category");


		/*
		 * Column 4: This is a special column. It adds a link to view the
		 * Project. We need to pass the project identifier to the url.
		 */
		table.addLinkColumn(""	/* The header. We will leave it empty */,
				"View Project" 	/* What should be displayed in every row */,
				"Project?id=" 	/* This is the base url. The final url will be composed from the concatenation of this and the parameter below */, 
				"id" 			/* For every project displayed, the ID will be retrieved and will be attached to the url base above */);

		// The filter parameter defines what to show on the projects page
		final String filter = request.getParameter("filter");

		if (filter != null) {
			if(filter.equals("name")) {
				final String name = request.getParameter("name");
				table.addObjects(this.dbInterface.searchByName(name));
			} else if (filter.equals("category")) {
				final String name = request.getParameter("category");
				table.addObjects(this.dbInterface.searchByCategory(name));
			} else if (filter.equals("city")) {
				final String name = request.getParameter("city");
				table.addObjects(this.dbInterface.searchByCity(name));
			}			
		}
		
		// Pass the table to the session. This will allow the respective jsp page to display the table.
		session.setAttribute("results", table);

		// Finally, proceed to the Seaech.jsp page which will render the search results
        this.getServletContext().getRequestDispatcher("/Search.jsp").forward(request, response);	        
	}
}