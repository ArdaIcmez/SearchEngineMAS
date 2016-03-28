<%@page import="ontologies.profile.*"%>  
<body bgcolor="#E6E6FA"> 
<%UserBean user = (UserBean)session.getAttribute("curUser");  %>
<form action="ControllerServlet" method="post">
<table>
	<colgroup>
       <col span="1" style="width: 10%;">
       <col span="1" style="width: 60%;">
       <col span="1" style="width: 10%;">
       <col span="1" style="width: 10%;">
       <col span="1" style="width: 10%;">
    </colgroup>
  <tr>
    <td><%if(user== null){out.print("<input type=\"submit\" name=\"todo\" value=\"Login\">");}%> </td>		
    <td><%if(user== null){out.print("<input type=\"submit\" name=\"todo\" value=\"Create New Account\">");}%></td>
    <td><%if(user!= null){out.print("Hello, "+user.getNickname());} %><td>
    <td><%if(user!= null){out.print("<input type=\"submit\" name=\"todo\" value=\"Delete Profile\"><input type=\"submit\" name=\"todo\" value=\"Edit Profile\">");}%><td>
    <td><%if(user!= null){out.print("<input type=\"submit\" name=\"todo\" value=\"Logout\">");}%><td>
  </tr>
</table> 
<h2>Aaardaa</h2>
<i>Search Engine v0.2</i>
<br>  
<br>
<br>
<input type="text" name="searchword">

<input type="submit" name="todo" value="Search">  
</form>  