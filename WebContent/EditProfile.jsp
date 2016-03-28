<%@page import="jade.util.leap.Iterator"%>
<%@page import="ontologies.profile.*"%>  
<%@page import="jade.util.leap.ArrayList.*"%> 
<body bgcolor="#E6E6FA"> 
<h2>User Profile</h2>
<%UserBean user = (UserBean)session.getAttribute("curUser");  %>
<form action="ControllerServlet" method="post">
<table>
	<tr>
		<td height="40">
			Username :
		</td>
		<td>
			<% out.print(user.getNickname()); %>
		</td>
		<td>
			
		</td>
	</tr>
	<tr>
		<td>
			Password :
		</td>
		<td>
			<textarea name="passarea" rows="1" cols="30"><% out.print(user.getPassword()); %></textarea>
		</td>
	</tr>
	<tr>
		<td>
			Name :
		</td>
		<td>
			<textarea name="namearea" rows="1" cols="30"><% out.print(user.getName()); %></textarea>
		</td>
	</tr>
	<tr>
		<td>
			Surname :
		</td>
		<td>
			<textarea name="surnamearea" rows="1" cols="30"><% out.print(user.getSurname()); %></textarea>
		</td>
	</tr>
	<tr>
		<td>
			Job :
		</td>
		<td>
			<textarea name="jobarea" rows="1" cols="30"><% out.print(user.getJob()); %></textarea>
		</td>
	</tr>
	<tr>
		<td>
			Role : 
		</td>
		<td>
			<textarea name="rolearea" rows="1" cols="30"><% out.print(user.getRole()); %></textarea>
		</td>
	</tr>
	<tr>
		<td>
			Interests : 
		</td>
		<td>
			<textarea name="interarea" rows="5" cols="30"><% jade.util.leap.List list = user.getInterests(); Iterator k = list.iterator();
			while (k.hasNext()) {
				out.print(k.next()+"\n");}%></textarea>
		</td>
	</tr>
	<tr>
		<td>
			Browse History : 
		</td>
		<td>
			<textarea name="histarea" rows="5" cols="30"><% jade.util.leap.List list2 = user.getBrowseHistory(); Iterator m = list2.iterator();
			while (m.hasNext()) {
				out.print(m.next()+"\n");}%></textarea>
		</td>
	</tr>
</table>
<br><br><br>
<input type="submit" name="todo" value="Back to Search">   <button name="todo" value="modifyProfile" type="submit">Modify</button>
</form>