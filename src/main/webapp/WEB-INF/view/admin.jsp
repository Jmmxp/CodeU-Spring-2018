<%--
  Copyright 2017 Google Inc.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>

<%@ page import="codeu.helper.AdminHelper"%>
<%@ page import="codeu.model.store.basic.UserStore"%>
<%@ page import="codeu.model.store.basic.MessageStore"%>
<%@ page import="codeu.model.store.basic.ConversationStore"%>

<%
String user = (String) request.getSession().getAttribute("user");
String message = (String) request.getSession().getAttribute("adminMessage");
int numUsers = UserStore.getInstance().getNumUsers();
int numMessages = MessageStore.getInstance().getNumMessages();
int numConversations = ConversationStore.getInstance().getNumConversations();
%>
<!DOCTYPE html>
<html>
<head>
  <title>Git Rekt's Chat App</title>
  <link rel="stylesheet" href="/css/main.css">
</head>
<body>

  <nav>
    <a id="navTitle" href="/">Git Rekt's Chat App</a>
    <a href="/conversations">Conversations</a>

    <% if(user != null){ %>
      <a href="/profile/<%= user %>">Hello <%= user %>!</a>
    <% } else{ %>
      <a href="/login">Login</a>
    <% } %>

    <a href="/about.jsp">About</a>

    <% if (AdminHelper.isAdmin(user)) { %>
      <a href="/admin">Admin</a>
    <% } %>

    <% if (user != null) { %>
      <a href="/logout">Logout</a>
    <% } %>

  </nav>

  <div id="container">
    <div
      style="width:75%; margin-left:auto; margin-right:auto; margin-top: 50px;">

      <% if (AdminHelper.isAdmin(user)) { %>

          <h1>Administration</h1>
          <hr />
          <h2>Site Statistics</h2>

          <p>Hello <%= user %>! Welcome to the admin page!</p>
          <p>Here are some site stats:</p>

          <ul>
            <li><b>Users: </b><%= numUsers %></li>
            <li><b>Messages: </b><%= numMessages%></li>
            <li><b>Conversations: </b><%= numConversations%></li>
          </ul>
          <p>

		  <form method="post" action="${pageContext.request.contextPath}/admin">
			  <input type="submit" name="deleteUsersButton" value="Delete All Users" disabled/>
			  <input type="submit" name="deleteMessagesButton" value="Delete All Messages" disabled/>
			  <input type="submit" name="deleteConversationsButton" value="Delete All Conversations" disabled/>
		  </form>
          <p>Sorry, these buttons have been disabled for the Showcase! :)</p>

      <% } %>

    </div>
  </div>
</body>
</html>