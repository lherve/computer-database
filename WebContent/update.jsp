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
        
	        <h1>${cpu.id < 0 ? 'Add a' : 'Edit' } computer</h1>
	
			<form action="update" method="POST" >
	        
		        <fieldset>
		
					<div class="clearfix ${err mod 10 >= 1 ? 'error' : ''}">
					
					    <label for="name">Computer name</label>
					    
					    <div class="input">
					        
					   		<input type="text" id="name" name="name" value="${cpu.name }" >
					
					        <span class="help-inline">Required</span>
					        
					    </div>
					    
					</div>


					<div class="clearfix ${err mod 100 >= 10 ? 'error' : ''}">
					
					    <label for="introduced">Introduced date</label>
					    
					    <div class="input">
					        
					    	<input type="text" id="introduced" name="introduced" value="${introduced }" >
					
					        <span class="help-inline">Date (&#x27;yyyy-MM-dd&#x27;)</span> 
					        
					    </div>
					    
					</div>


					<div class="clearfix ${ err mod 1000 >= 100 ? 'error' : ''}">
					
					    <label for="discontinued">Discontinued date</label>
					    
					    <div class="input">
					        
						    <input type="text" id="discontinued" name="discontinued" value="${discontinued }" >
						
						    <span class="help-inline">Date (&#x27;yyyy-MM-dd&#x27;)</span> 
					        
					    </div>
					    
					</div>


					<div class="clearfix ">
					
					    <label for="company">Company</label>
					    
					    <div class="input">
					        
						    <select id="company" name="company">
						        
						        <option class="blank" value="">-- Choose a company --</option>
					        
						        <c:forEach var="cie" items="${cies}">
						        
						        	<option value="${cie.id }" ${empty cpu.company ? '' : cie.id eq cpu.company.id ? 'selected="selected"' : ''}>${cie.name }</option>
						        
						        </c:forEach>
						        
					    	</select>
					
					        <span class="help-inline"></span> 
					        
					    </div>
					    
					</div>

		        </fieldset>
		        
				<input type="hidden" value="${cpu.id}" name="id" />	        
        
		        <div class="actions">
		        
		            <input type="submit" value="Save this computer" class="btn primary"> or 
		            <a href="computers" class="btn">Cancel</a> 
		            
		        </div>
		
			</form>

			<c:if test="${cpu.id > 0 }">
	
				<form action="delete" method="POST" class="topRight">
				    
				    <input type="hidden" value="${cpu.id }" name="id"/>
				    
			        <input type="submit" value="Delete this computer" class="btn danger">
				    
				</form>
	
			</c:if>

        </section>
        
    </body>
    
</html>