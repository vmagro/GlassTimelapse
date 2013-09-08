<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ page import="com.socaldevs.glasstimelapse.web.Utils" %>

<% 
	if (Utils.isUserLoggedIn(session) && request.getServletPath().equals("/index.jsp")) {
		// redirect to home if logged in and visiting index page
		response.sendRedirect(request.getContextPath() + "/home.jsp");
	} else if (!Utils.isUserLoggedIn(session) && !request.getServletPath().equals("/index.jsp")) {
		// redirect to index if not logged in and not viisting index page
		response.sendRedirect(request.getContextPath() + "/index.jsp");
	}

%>

<!DOCTYPE>
<html>
<head>
  <link href='http://fonts.googleapis.com/css?family=Roboto:400,100,300,500,100italic,300italic,400italic,500italic,700,700italic,900italic,900' rel='stylesheet' type='text/css'>
  <link href="assets/style.css" type="text/css" rel="stylesheet">
  <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
  <script type="text/javascript">
    (function () {
      var po = document.createElement('script');
      po.type = 'text/javascript';
      po.async = true;
      po.src = 'https://plus.google.com/js/client:plusone.js?onload=start';
      var s = document.getElementsByTagName('script')[0];
      s.parentNode.insertBefore(po, s);
    })();
    
    function signInCallback(authResult) {
      if (authResult['code']) {
   	    $.post("/connect", JSON.stringify(authResult), function(profile) {
   	    	<% if (request.getServletPath().equals("/index.jsp")) { %>
   	    		// redirect to home
   		  		window.location = '/home.jsp';
   	    	<% } %>
   		});
      } else if (authResult['error']) {
    	  console.log('There was an error: ' + authResult['error']);
    	  
    	  <% if (!request.getServletPath().equals("/index.jsp")) { %>
    		// logs out, redirect to index page
			$.post('/logout', function() {
				window.location = '/index.jsp';
			});
    	  <% } %>
      }
    }
  </script>
</head>
<body>
	<header>
		<h1>Some App Name</h1>
		<% if(Utils.isUserLoggedIn(session)) { %>
			<div id="currentUser">
				<img src="<%=Utils.getCurrentUser(session).googlePublicProfilePhotoUrl %>" />
				<%= Utils.getCurrentUser(session).googleDisplayName %>
				<div class="clear"></div>
			</div>
		<% } %>
		<div id="signinButton"<%=Utils.isUserLoggedIn(session) ? " class=\"hidden\"" : "" %>>
		  <span class="g-signin"
		    data-scope="https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/userinfo.email"
		    data-clientid="597615227690-pfgba7ficse1kf1su0qkgjllktcb7psf.apps.googleusercontent.com"
		    data-redirecturi="postmessage"
		    data-accesstype="offline"
		    data-cookiepolicy="single_host_origin"
		    data-callback="signInCallback">
		  </span>
		</div>
		<div class="clear"></div>
	</header>