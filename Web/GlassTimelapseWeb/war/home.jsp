<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="header.jsp" %>
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
			<div id="home-welcome">
				<h2>Home</h2>
				<p class="desc">It doesn't seem like you have paired a Glass yet. </p>
			</div>
		</div>
		<div class="clear"></div>
	</div>
</body>
</html>