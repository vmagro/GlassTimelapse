<%@page import="com.socaldevs.glasstimelapse.web.User"%>
<%@page import="com.google.gdata.client.http.AuthSubUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="header.jsp" %>
<%
	String youtubeToken = request.getParameter("token");
	if (youtubeToken != null) {
		User user = Utils.getCurrentUser(session);
		String onetimeUseToken = AuthSubUtil.getTokenFromReply(request.getQueryString());
		String sessionToken = AuthSubUtil.exchangeForSessionToken(onetimeUseToken, null);
		user.setAndUpdateYoutubeToken(sessionToken);		
	}
%>
	<div class="center">
		<ul id="menu">
			<li>New Stuff</li>
			<li>My Timelapses</li>
		</ul>
		<div id="content">
			<% if(Utils.getCurrentUser(session).glassId != null) { %>
				<div class="box glass">
					<h4>Glass paired!</h4>
					<p>Your account has been paired with the glass <%= Utils.getCurrentUser(session).glassId %></p>
				</div>
			<% } else { %>
				<div class="box glass">
					<h4>Waiting to pair with your Glass...</h4>
					<p>Download our app and pair your Glass with your account.</p>
				</div>
			<% } %>
			<% if(Utils.getCurrentUser(session).youtubeSessionToken == null) { %>
				<a href="https://www.google.com/accounts/AuthSubRequest?next=http%3A%2F%2Fglass.ptzlabs.com%2Fhome.jsp&scope=http%3A%2F%2Fgdata.youtube.com&session=1&secure=0">
				<div class="box youtube">
					<h4>YouTube permission needed.</h4>
					<p>With YouTube access, we will automatically upload <i>unlisted</i> videos as soon as we finish processing your pictures.</p>
				</div>
				</a>
			<% } %>
			<div id="home-welcome">
				<h2>Home</h2>
				<p class="desc">It doesn't seem like you have paired a Glass yet. </p>
			</div>
		</div>
		<div class="clear"></div>
	</div>
</body>
</html>