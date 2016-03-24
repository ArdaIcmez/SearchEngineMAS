<%@page import="ontologies.*"%>
<%@page import="java.util.*"%>  
<body bgcolor="#E6E6FA"> 
<h2>Result Contents</h2>
<%List<UserBean> userList = (List<UserBean>)session.getAttribute("userList"); 
  SearchBean searchBean = (SearchBean)session.getAttribute("bean");
  String term = searchBean.getWord();
  %>
<form action="ControllerServlet" method="post">
<table border=1 frame=void rules=rows>
<% for(UserBean user:userList) {  
%>
	<tr>
		<td>
			Username :
		</td>
		<td <%if(term.compareTo(user.getNickname())==0){out.print("bgcolor=\"#FF6969\"");} %>>
			<% out.print(user.getNickname()); %>
		</td>
	</tr>
	<tr >
		<td>
			Name :
		</td>
		<td <%if(term.compareTo(user.getName())==0){out.print("bgcolor=\"#FF6969\"");} %>>
			<% out.print(user.getName()); %>
		</td>
	</tr>
	<tr >
		<td>
			Surname :
		</td>
		<td <%if(term.compareTo(user.getSurname())==0){out.print("bgcolor=\"#FF6969\"");} %>>
			<% out.print(user.getSurname()); %>
		</td>
	</tr>
	<tr >
		<td>
			Job :
		</td>
		<td <%if(term.compareTo(user.getJob())==0){out.print("bgcolor=\"#FF6969\"");} %>>
			<% out.print(user.getJob()); %>
		</td>
	</tr>
	<tr >
		<td>
			Role : 
		</td>
		<td <%if(term.compareTo(user.getRole())==0){out.print("bgcolor=\"#FF6969\"");} %>>
			<% out.print(user.getRole()); %>
		</td>
	</tr>
	<tr>
		<td>
			Interests : 
		</td>
		<td>
			<% Iterator myIter = user.getInterests().iterator(); 
			while (myIter.hasNext()) {
				String curInterest = myIter.next().toString();
				if (term.compareTo(curInterest)==0){
					out.print("<p style=\"background-color: #FF6969 ; display:inline\">"+curInterest+"</p><br>");
				}
				else {
					out.print(curInterest+"<br>");	
				}
				}%>
		</td>
	</tr>
	<tr>
		<td>
			Browse History : 
		</td>
		<td>
			<% myIter = user.getBrowseHistory().iterator();
			while (myIter.hasNext()) {
				String curHistory = myIter.next().toString();
				if (term.compareTo(curHistory)==0){
					out.print("<p style=\"background-color: #FF6969 ; display:inline\">"+curHistory+"</p><br>");
				}
				else {
					out.print(curHistory+"<br>");	
				}
				}%>
		</td>
	</tr>
	<tr><td height="40"></td></tr>
	<% } %>
</table>
<br><br><br>
<input type="submit" name="todo" value="Back to Search">
</form>