package layer.com.jpa;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

/**
 * Servlet implementation class LoginSrv
 */
//@WebServlet("/LoginSrv")
public class LoginSrv extends HttpServlet {       

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		User loginUser = null;
		
		// Get all url request parameters
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		//Hibernate config
		Configuration cfg = new Configuration();
		cfg.configure("hibernate.cfg.xml");
		
		Session session = cfg.buildSessionFactory().openSession();
		
		System.out.println("trying to login user: "+username+ " "+password); //DEBUG
		Query<User> query = session.createQuery("from User where username = '"+username+"'");
		
		List<User> users = query.list();
        for(User u : users) {
//        	System.out.println(u); //DEBUG
        	if(u.getPassword().compareTo(password) == 0) { //both username and password match!
        		loginUser = u;
        		System.out.println(loginUser + " user login succesful!"); //DEBUG
        		
        		// Redirect to welcome
        		HttpSession httpsess = request.getSession();
        		httpsess.setAttribute("user", loginUser);
        		
        		response.sendRedirect("welcome.jsp");
			} else {
				//You failed Try again (refresh page)
				out.println("<html><body>Username or Password wrong.");
				out.println("<br><br><br><form action=\"login.html\">" + 
						"<input type=\"submit\" value=\"Try Again\" />" + 
						"</form>");
			}
        }
		session.close();
		
		if(users.isEmpty()) {
			//You failed Try again (refresh page)
			out.println("<html><body>Username or Password wrong.");
			out.println("<br><br><br><form action=\"login.html\">" + 
					"<input type=\"submit\" value=\"Try Again\" />" + 
					"</form>");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}