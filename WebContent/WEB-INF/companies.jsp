<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>

	<head>
		
		<title>Computers database</title>
		
        <link rel="stylesheet" type="text/css" media="screen" href="css/bootstrap.min.css"> 
        <link rel="stylesheet" type="text/css" media="screen" href="css/main.css"> 
    
    </head>
    
    <body>
    
    	<header class="topbar">
            <h1 class="fill">
                <a href="computers">2.0 sample application &mdash; Computer database</a>
            </h1>
        </header>
        
        <section id="main">
        
	        <h1>Edit companies</h1>
	
			<c:if test="${not empty info }">
		        <div class="alert-message warning">
		            <strong>Done!</strong> ${info }
		        </div>
			</c:if>
	
			<form action="companies" method="POST" >
	        
		        <fieldset>

					<div class="clearfix ">
					
					    <label for="id">Actual name</label>
					    
					    <div class="input">
					        
						    <select id="id" name="id">
						        
						        <option class="blank" value="-1">++ Add a new company</option>
					        
						        <c:forEach var="cie" items="${cies}">
						        
						        	<option value="${cie.id }" ${not empty param.id ? param.id eq cie.id ? 'selected="selected"' : '' : '' }>${cie.name }</option>
						        
						        </c:forEach>
						        
					    	</select>
					         
					    </div>
					    
					</div>
					
					<div class="clearfix ${ err >= 10 ? 'error' : ''}">
					
					    <label for="name">New name</label>
					    
					    <div class="input">
					        
						    <input type="text" id="name" name="name" value="${param.name }">
						
						    <span class="help-inline"></span> 
					        
					    </div>
					    
					</div>

		        </fieldset>
		        
		        <div class="actions">
		        
		            <input type="submit" value="Save this company" class="btn primary"/>
		            <input type="submit" value="Delete" class="btn danger" formaction="deletecie"/> or 
		            <a href="new" class="btn">Cancel</a>
		            
		        </div>
		
			</form>

        </section>
        
    </body>
    
</html>