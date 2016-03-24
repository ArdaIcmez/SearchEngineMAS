<%@page import="ontologies.SearchBean"%>  
<%@ include file="index.jsp" %>  
<%@page import= "java.util.*" %>
<p>Results containing the term <i><% out.println("\""+session.getAttribute("word")+"\"");%></i> : </p>  
<%  
SearchBean bean=(SearchBean)session.getAttribute("bean");
int no =1;
for(Iterator<String> i = bean.getList().iterator(); i.hasNext(); ) {
    String item = i.next();
	out.println(no+"- " + "<a href=\"ControllerServlet?todo=clk&page=" +
    item+"\" target=\"_blank\"><u>"+item+"</u></a>"+"<br>");
	no++;
}
if (no == 1){
	out.println("<br><i> No results found. </i>");
}
%>  