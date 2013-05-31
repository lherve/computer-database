<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>

	<head>
		<title>Computers database - error</title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="/computer-database/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" media="screen" href="/computer-database/css/main.css">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		
	</head>
	
	<body>
		
		<header class="topbar">
			<h1 class="fill">
				<a href="/computer-database/">Play 2.0 sample application &mdash; Computer database</a>
			</h1>
		</header>
		
		<section id="main">

			<h1>Error Page</h1>
	
			<c:if test="${not empty exception }">
				 <div class="alert-message warning">
		            <strong>Error: </strong>${exception.message }
		        </div>
	        </c:if>

		</section>
		
	</body>
	
</html>