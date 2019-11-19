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
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

/**
 * Servlet implementation class RegisterSrv
 */
//@WebServlet("/RegisterSrv")
public class RegisterSrv extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Get all url request parameters
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		User newUser = new User(username, email, password);
		
		PrintWriter out = response.getWriter();
		
		//first check if data is valid
		if(!checkDataValidity(request, response)) {
			out.println("Invalid Data<br>");
			out.println("<br><form action=\"register.html\">" + 
					"<input type=\"submit\" value=\"Try Again\" />" + 
					"</form></body></html>");
			return;
		} //From now on data is valid
		
		
		//Hibernate config
		Configuration cfg = new Configuration();
		cfg.configure("hibernate.cfg.xml");
		
		Session session = cfg.buildSessionFactory().openSession();
		Transaction tr = session.beginTransaction();
		
//		System.out.println("trying to save new user"); //DEBUG
		session.save(newUser);
		
		tr.commit();;
		session.close();
		
		System.out.println(newUser + "saved succesfully!"); //DEBUG
		
		// Redirect to welcome
		HttpSession httpsess = request.getSession();
		httpsess.setAttribute("user", newUser);
		
		response.sendRedirect("welcome.jsp");
	}


	private boolean checkDataValidity(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		// Get all url request parameters
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		//empty data problem
		if(username.isEmpty() || email.isEmpty() || password.isEmpty()) {
			//You failed Try again (refresh page)
			out.println("<html><body>Please fill ALL the form to register. Try again!<br>");
			return false;
		}
		
		//already exist username problem
		Configuration cfg = new Configuration();
		cfg.configure("hibernate.cfg.xml");
		Session session = cfg.buildSessionFactory().openSession();
		
		Query<User> query = session.createQuery("from User where username = '"+username+"' OR email = '"+email+"'");
		List<User> dupUsers = query.list();
        
		if(!dupUsers.isEmpty()) {
			out.println("<html><body>Username or email already exists. Try another one.<br>");
			session.close();
			return false;
		}
		
		session.close();
		return true;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
