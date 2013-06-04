<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html>

	<head>
		
		<title>Computers database</title>
		
        <link rel="stylesheet" type="text/css" media="screen" href="/computer-database/css/bootstrap.min.css"> 
        <link rel="stylesheet" type="text/css" media="screen" href="/computer-database/css/main.css"> 
    
    </head>
    
    <body>
    
    	<header class="topbar">
            <h1 class="fill">
                <a href="/computer-database/">Play 2.0 sample application &mdash; Computer database</a>
            </h1>
        </header>
        
        <section id="main">
        
	        <h1>Edit companies</h1>
	
			<c:if test="${not empty info }">
		        <div class="alert-message warning">
		            ${info }
		        </div>
			</c:if>
			
			<form:form action="company" commandName="company" method="POST">
			
				<fieldset>
				
					<div class="clearfix ">

						<label for="id">Actual name</label>
						
						<div class="input">
												
							<form:select id="id" path="id">
													
								<form:option class="blank" value="-1">++ Add a new company</form:option>
								
								<form:options items="${companies}" itemValue="id" itemLabel="name" />
								
							</form:select>
						
						</div>
						
					</div>
					
					<div class="clearfix <c:if test="${!empty result.getFieldError('name')}"> error</c:if>">

						<label for="name">New name</label>
						
						<div class="input">
						
							<form:input type="text" id="name" path="name"/>
							
							<span class="help-inline"></span> 
						
						</div>
					
					</div>
				
				
				</fieldset>
			
				<div class="actions">
		        
		            <input type="submit" value="Save this company" class="btn primary"/>
		            <input type="submit" value="Delete" class="btn danger" formaction="company/delete"/> or 
		            <a href="computer/new" class="btn">Cancel</a>
		            
		        </div>
		        
			</form:form>	

        </section>
        
    </body>
    
</html>