<%@page import="ontologies.profile.*"%>
<%@page import="ontologies.search.*"%>  
<body bgcolor="#E6E6FA"> 
<h2>Result Contents</h2>
<%jade.util.leap.List contentList = (jade.util.leap.List)session.getAttribute("contentList"); 
  SearchBean searchBean = (SearchBean)session.getAttribute("bean");
  String term = searchBean.getWord();
  jade.util.leap.Iterator iter = contentList.iterator();
  %>
<form action="ControllerServlet" method="post">
<table border=1 frame=void rules=rows>
<% while(iter.hasNext()) { SearchObject myObj = (SearchObject)iter.next();
	jade.util.leap.List individuList = myObj.getContentList();
	jade.util.leap.Iterator iter2 = individuList.iterator();
	
	out.print("<tr>");
	out.print("<td>");
	out.print(myObj.getSubject());
	out.print("</td>");
	out.print("<td>");
	out.print("</td>");
	out.print("</tr>");
	
	while(iter2.hasNext()){
		PredObj predObj = (PredObj)iter2.next();		
		out.print("<tr>");
		out.print("<td>");
		out.print(predObj.getPredicate());
		out.print("</td>");
		out.print("<td>");
		out.print(predObj.getObj());
		out.print("</td>");
		out.print("</tr>");
		}

	out.print("<tr>");
	out.print("<td height=\"40\">");
	out.print("</td>");
	out.print("</tr>");
	} %>
</table>
<br><br><br>
<input type="submit" name="todo" value="Back to Search">
</form>