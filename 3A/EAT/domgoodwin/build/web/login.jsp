<%-- 
    Document   : index
    Created on : 15-Mar-2010, 14:47:22
    Author     : bastinl
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
       
        <title>Login / signup page</title>
    </head>
    <body>
        <h2>Please log in!</h2>
              <form method="POST" action="/coursework/do/login">
                Username:<input type="text" name="username" value="" />----
                Password:<input type="password" name="password" value="" />        
        <input type="submit" value="Click to log in" />
        </form>
        
        <form method="POST" action="/coursework/do/addUser">
            <h2> Don't yet have an account? </h2>
            Username:<input type="text" name="newUsername" value="" />----
                Password:<input type="password" name="newPassword" value="" />      
            <input type="submit" value="Sign up as a new user"/>
        </form>
        
    </body>
</html>
