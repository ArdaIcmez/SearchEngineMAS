<%@page import="ontologies.search.*"%>
<%@ include file="index.jsp" %>  
<%SearchBean bean=(SearchBean)session.getAttribute("bean"); %>
<p>Results containing the term <i><% out.println("\""+bean.getWord()+"\"");%></i> : </p>  
<%
jade.util.leap.List myList = bean.getResultList();
jade.util.leap.Iterator iter = myList.iterator();
int no =1;
while(iter.hasNext()) {
    String item = iter.next().toString();
	out.println(no+"- " + "<a href=\"ControllerServlet?todo=clk&page=" +
    item+"\" target=\"_blank\"><u>"+item+"</u></a>"+"<br>");
	no++;
}
if (no == 1){
	out.println("<br><i> No results found. </i>");
}
%>  