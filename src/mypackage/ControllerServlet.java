package mypackage;
import java.io.IOException;  

import javax.servlet.RequestDispatcher;  
import javax.servlet.ServletException;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jade.core.Profile;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;
import jade.wrapper.gateway.*;
import ontologies.profile.*;
import ontologies.search.*;

public class ControllerServlet extends HttpServlet implements ProfileVocabulary,SearchVocabulary {  
    /**
	 * Register -> User already exists?
	 */
	private static final long serialVersionUID = 1L;
	private ProfileOperation profileOperation;
	private	SearchOperation searchOperation;
	protected void doPost(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {  
        response.setContentType("text/html");  
        HttpSession session = request.getSession();
        String todo = request.getParameter("todo");
        
        switch(todo)
        {
	        case "Search": 
	        {
	        	String searchword = request.getParameter("searchword");
	        	UserBean curUser = (UserBean)session.getAttribute("curUser");
	        	//Set up the operation for MAS
	        	searchOperation = new SearchOperation();
	        	SearchBean myBean = new SearchBean();
	        	myBean.setWord(searchword);
	        	if(curUser != null){
	        		searchOperation.setNickname(curUser.getNickname());
	        	}
	            searchOperation.setSearch(myBean);
	            searchOperation.setType(SEARCH_KEYWORD);
	            try {
					JadeGateway.execute(searchOperation);
				} catch (ControllerException | InterruptedException e) {
					e.printStackTrace();
				}
	            myBean = (SearchBean) searchOperation.getSearch();
	            jade.util.leap.Iterator asd= myBean.getResultList().iterator();
	            while(asd.hasNext()){
	            	System.out.println("SERVLET ALDIGI: " + asd.next().toString());
	            }
	            session.setAttribute("bean",myBean);
	           
	            if (curUser != null) {
	            	searchOperation.setNickname(curUser.getNickname());
	            	searchOperation.setType(ADD_SEARCH_HISTORY);
	            	try {
						JadeGateway.execute(searchOperation);
					} catch (ControllerException | InterruptedException e) {
						e.printStackTrace();
					}
	            }
	            RequestDispatcher rd=request.getRequestDispatcher("searchresults.jsp");  
	            rd.forward(request, response); 
	        	break;
	        }
	        case "Login": 
	        {
	        	
	            RequestDispatcher rd=request.getRequestDispatcher("Login.jsp");  
	            rd.forward(request, response); 
	        	break;
	        }
	        case "Create New Account": 
	        {
	            RequestDispatcher rd=request.getRequestDispatcher("Register.jsp");  
	            rd.forward(request, response); 
	        	break;
	        }
	        case "Authenticate" : 
	        {
	        	String nick = (String)request.getParameter("uname");
	        	String pass = (String)request.getParameter("upass");
	        	UserBean usr = new UserBean();
	        	usr.setNickname(nick);
	        	usr.setPassword(pass);
	        	profileOperation = new ProfileOperation();
	        	profileOperation.setUser(usr);
	        	profileOperation.setType(AUTHENTICATE_USER);
	        	try    {
	        		JadeGateway.execute(profileOperation);
	            } catch(Exception e) { e.printStackTrace(); } 
	        	usr = profileOperation.getUser();
	        	if(usr.getAuthentication()) {
	        		session.setAttribute("curUser", usr);
	        		RequestDispatcher rd=request.getRequestDispatcher("index.jsp");
		            rd.forward(request, response); 
	        	}
	        	else {
	        		RequestDispatcher rd=request.getRequestDispatcher("FailedLogin.jsp");  
		            rd.forward(request, response); 
	        	}
	        	profileOperation = null;
	        	break;
	        }
	        case "Logout" :
	        {
	        	session.setAttribute("curUser", null);
	        	RequestDispatcher rd=request.getRequestDispatcher("index.jsp");  
	            rd.forward(request, response);
	            break;
	        }
	        case "Back to Search" :
	        {
	        	RequestDispatcher rd=request.getRequestDispatcher("index.jsp");  
	            rd.forward(request, response);
	            break;
	        }
	        case "Register" :
	        {
	        	profileOperation = new ProfileOperation();
	        	UserBean usr = new UserBean();
	        	usr.setNickname(request.getParameter("regname"));
	        	usr.setPassword(request.getParameter("regpass"));
	        	profileOperation.setUser(usr);
	        	profileOperation.setType(REGISTER_USER);
	        	try    {
	        		JadeGateway.execute(profileOperation);
	            } catch(Exception e) { e.printStackTrace(); }
	        	usr = null;
	        	profileOperation = null;
	        	RequestDispatcher rd=request.getRequestDispatcher("index.jsp");  
	            rd.forward(request, response);
	            break;
	        }
	        case "Edit Profile" :
	        {
	        	profileOperation = new ProfileOperation();
	        	UserBean usr = (UserBean)session.getAttribute("curUser");
	        	profileOperation.setUser(usr);
        		profileOperation.setType(POPULATE_PROFILE);
        		try {
					JadeGateway.execute(profileOperation);
				} catch (ControllerException | InterruptedException e) {
					e.printStackTrace();
				}
        		usr = (UserBean) profileOperation.getUser();
        		session.setAttribute("curUser", usr);
	        	RequestDispatcher rd=request.getRequestDispatcher("EditProfile.jsp");  
	            rd.forward(request, response);
	            profileOperation = null;
	            break;
	        }
	        
	        case "modifyProfile" :
	        {
	        	//----------------------- Modification of the user Profile
	        	
	        	//Getting the user data from the webpage
	        	UserBean user = (UserBean)session.getAttribute("curUser");
	        	UserBean modifyUser = new UserBean();
	        	modifyUser.setNickname(user.getNickname());
	        	modifyUser.setName(request.getParameter("namearea"));
	        	modifyUser.setPassword(request.getParameter("passarea"));
	        	modifyUser.setSurname(request.getParameter("surnamearea"));
	        	modifyUser.setRole(request.getParameter("rolearea"));
	        	modifyUser.setJob(request.getParameter("jobarea"));
	        	String inter = request.getParameter("interarea");
	        	String hist = request.getParameter("histarea");
	        	inter = inter.replace("\r", "");
	    		hist = hist.replace("\r", "");
	    		String[] interests = inter.split("\n");
	    		String[] histories = hist.split("\n");
	    		jade.util.leap.List myList = new jade.util.leap.ArrayList();
	    		jade.util.leap.List myList2 = new jade.util.leap.ArrayList();
	    		for(String intr : interests) {
	    			myList.add(intr);
	    		}
	    		for(String histr : histories) {
	    			myList2.add(histr);
	    		}
	        	modifyUser.setBrowseHistory(myList2);
	        	modifyUser.setInterests(myList);
	        	myList = null;
	        	myList2 = null;
	        	
	        	profileOperation = new ProfileOperation();
	        	profileOperation.setUser(user);
	        	profileOperation.setModifyUser(modifyUser);
	        	profileOperation.setType(MODIFY_PROFILE);
	        	
	        	try {
	        		JadeGateway.execute(profileOperation);
	        	}
	        	catch(Exception e) {
	        		e.printStackTrace();
	        	}
	        	
	        	user = profileOperation.getUser();
	        	session.setAttribute("curUser", user);
	        	RequestDispatcher rd=request.getRequestDispatcher("EditProfile.jsp");  
	            rd.forward(request, response);
	        	break;
	        }
	        
	        case "Delete Profile" :
	        {
	        	UserBean usr = (UserBean)session.getAttribute("curUser");
	        	profileOperation = new ProfileOperation();
	        	profileOperation.setUser(usr);
	        	profileOperation.setType(DELETE_USER);
	        	try {
	        		JadeGateway.execute(profileOperation);
	        	}
	        	catch(Exception e) {
	        		e.printStackTrace();
	        	}
	        	session.setAttribute("curUser", null);
	        	RequestDispatcher rd=request.getRequestDispatcher("index.jsp");  
	            rd.forward(request, response);
	            profileOperation = null;
	        	break;
	        }
	        
	        case "clk" :
	        {
	        	String urlRedirect = request.getParameter("page");
	        	searchOperation = new SearchOperation();
	        	searchOperation.setUrl(urlRedirect);
	        	searchOperation.setType(LOAD_PAGE);
	        	try {
	        		JadeGateway.execute(searchOperation);
	        	}
	        	catch(Exception e) {
	        		e.printStackTrace();
	        	}
	        	jade.util.leap.List myList = searchOperation.getResultList();
	        	session.setAttribute("contentList", myList);
	        	session.setAttribute("currentUrl", urlRedirect);
	        	RequestDispatcher rd=request.getRequestDispatcher("PageResult.jsp");  
	            rd.forward(request, response);
	            searchOperation = null;
	        	break;
	        }
	        case "modifyPage" :
	        {
	        	String urlRedirect = (String)session.getAttribute("currentUrl");
	        	//here needs to be a loop to get all boxes
	        	
	        	searchOperation = new SearchOperation();
	        	searchOperation.setUrl(urlRedirect);
	        	searchOperation.setType(MODIFY_PAGE);
	        	try {
	        		JadeGateway.execute(searchOperation);
	        	}
	        	catch(Exception e) {
	        		e.printStackTrace();
	        	}
	        	jade.util.leap.List myList = searchOperation.getResultList();
	        	session.setAttribute("contentList", myList);
	        	RequestDispatcher rd=request.getRequestDispatcher("PageResult.jsp");  
	            rd.forward(request, response);
	            searchOperation = null;
	        	break;
	        }
	        default :
	        {
	        	RequestDispatcher rd=request.getRequestDispatcher("index.jsp");
	        	rd.forward(request, response); 
	        	break;
	        }
        }    
    }  

    @Override  
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)  
            throws ServletException, IOException {  
        doPost(req, resp);  
    }  
    
    public void init() throws ServletException {
    	Properties p = new Properties();
    	p.setProperty(Profile.DETECT_MAIN,"true");
    	p.setProperty(Profile.PLATFORM_ID, "*");
    	p.setProperty(Profile.CONTAINER_NAME, "MyGatewayAgent");
    	JadeGateway.init("myagents.MyGatewayAgent", p);  
    	
    } 
}  