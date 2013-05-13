<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>

	<head>
	
		<title>Computers database</title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" media="screen" href="css/main.css">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		
	</head>
	
	<body>
	
		<header class="topbar">
			<h1 class="fill">
				<a href="computers">Play 2.0 sample application &mdash; Computer database</a>
			</h1>
		</header>

		<section id="main">
		
			<h1>${count > 0 ? count : 'No' } computer${count > 1 ? 's' : '' } found</h1>
			
			<c:if test="${not empty info }">
		        <div class="alert-message warning">
		            <strong>Done!</strong> ${info }
		        </div>
			</c:if>
			
			<div id="actions">
			
				<form action="" method="GET">
					<input type="search" id="searchbox" name="search" value="${page.search }" placeholder="Filter by computer name...">
	            	<input type="submit" id="searchsubmit" value="Filter by name" class="btn primary">
	            	
	            	<c:if test="${not empty param.s }">
	            		<input type="hidden" id="s" name="s" value="${param.s}" />
	            	</c:if>
				</form>
				
				<a class="btn success" id="add" href="new">Add a new computer</a>
				
			</div>
			
			<c:choose>
				<c:when test="${count eq 0 }">
				
			        <div class="well">
			            <em>Nothing to display</em>
			        </div>
				
				</c:when>
				<c:otherwise>
			
					<table class="computers zebra-striped">
					
						<thead>
							<tr>
								<th class="col2 header ${s eq '1' ? 'headerSortUp' : s eq '-1' ? 'headerSortDown' : empty s ? 'headerSortUp' : s < -4 ? 'headerSortUp' : s > 4 ? 'headerSortUp' : ''}">
							        <a href="?search=${page.search}&s=${s eq '1' ? '-1' : '1' }">Computer name</a>
							    </th>
							    <th class="col3 header ${s eq '2' ? 'headerSortUp' : s eq '-2' ? 'headerSortDown' : '' }">
							        <a href="?search=${page.search}&s=${s eq '2' ? '-2' : '2' }">Introduced</a>
							    </th>
							    <th class="col4 header ${s eq '3' ? 'headerSortUp' : s eq '-3' ? 'headerSortDown' : '' }">
							        <a href="?search=${page.search}&s=${s eq '3' ? '-3' : '3' }">Discontinued</a>
							    </th>
							    <th class="col5 header ${s eq '4' ? 'headerSortUp' : s eq '-4' ? 'headerSortDown' : '' }">
							        <a href="?search=${page.search}&s=${s eq '4' ? '-4' : '4' }">Company</a>
							    </th>
							</tr>
						</thead>
						
						<tbody>
							<c:forEach var="cpu" items="${page.cpus}">
								<tr>
									<td><a href="update?id=${cpu.id }"><c:out value="${cpu.name}" /></a></td>
									<td><c:out value="${cpu.introducedToString}" /></td>
									<td><c:out value="${cpu.discontinuedToString}" /></td>
									<td><c:out value="${cpu.company.name}" /></td>
								</tr>
							</c:forEach>
						</tbody>
						
					</table>
					
					<div id="pagination" class="pagination">
			            <ul>
							<li class="prev ${page.previous.number eq page.number ? 'disabled' : '' }">
							    <a href="?search=${param.search}&page=${page.previous.number}&s=${s }">&larr; Previous</a>
							</li>
			                <li class="current">
			                    <a>Displaying <c:out value="${page.start}" /> to <c:out value="${page.end}"/> of <c:out value="${count}" /></a>
			                </li>
							<li class="next ${page.next.number eq page.number ? 'disabled' : page.size > count ? 'disabled' : '' }">
							    <a href="?search=${param.search}&page=${page.next.number}&s=${s }">Next &rarr;</a>
							</li>
			            </ul>
		        </div>
        
	        	</c:otherwise>
			</c:choose>
        
        </section>
        
    </body>
    
</html>
