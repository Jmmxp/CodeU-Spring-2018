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
<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Conversation" %>
<%@ page import="codeu.helper.AdminHelper"%>

<%
String user = (String) request.getSession().getAttribute("user");
%>

<!DOCTYPE html>
<html>
<head>
  <title>Conversations</title>
  <link rel="stylesheet" href="/css/main.css">
</head>
<body>

  <nav>
    <a id="navTitle" href="/">Git Rekt's Chat App</a>
    <a href="/conversations">Conversations</a>

    <% if (user != null) { %>
      <a href="/profile/<%= user %>">Hello <%= user %>!</a>
    <% } else{ %>
      <a href="/login">Login</a>
    <% } %>

    <a href="/about.jsp">About</a>

    <% if (AdminHelper.isAdmin(user)) { %>
      <a href="/admin">Admin</a>
    <% } %>

    <% if (user != null) { %>
      <a href="/logout?post_logout_redirect=/conversations">Logout</a>
    <% } %>

  </nav>

  <div id="container">

    <% if (request.getAttribute("error") != null){ %>
        <h2 style="color:red"><%= request.getAttribute("error") %></h2>
    <% } %>

    <% if (user != null){ %>
      <h1>New Conversation</h1>
      <form action="/conversations" method="POST">
          <div class="form-group">
            <label class="form-control-label">Title:</label>
          <input type="text" name="conversationTitle">
          <input type="checkbox" id="groupCheckbox" name="newGroupConversation">
          <label for="groupCheckbox">Group</label>
        </div>
        <button type="submit" name="newConversation">Create</button>
      </form>
      <hr/>
    <% } %>

    <h1>Conversations</h1>

    <%
    List<Conversation> conversations =
      (List<Conversation>) request.getAttribute("conversations");
    if(conversations == null || conversations.isEmpty()){
    %>
      <p>Create a conversation to get started.</p>
    <%
    }
    else{
    %>
      <ul class="mdl-list">
    <%
      for(Conversation conversation : conversations){
    %>
      <% if (conversation.isGroupConversation()) { %>
        <li><a href="/chat/<%= conversation.getTitle() %>">
          <%= conversation.getTitle() %></a> <i>Group</i></li>
      <% } else if (conversation.isNormalConversation()) { %>
        <li><a href="/chat/<%= conversation.getTitle() %>">
          <%= conversation.getTitle() %></a></li>
      <% } %>
    <%
      }
    %>
      </ul>
    <%
    }
    %>
    <hr/>

    <%
    if (user != null) {
    %>
      <h1>Direct Messages</h1>
      <ul class="mdl-list">
    <%
      for(Conversation conversation : conversations){
    %>
      <% if (conversation.isDirectConversation()) { %>
        <li><a href="/chat/<%= conversation.getTitle() %>">
          <%= conversation.getDirectConversationTitle(user) %></a></li>
      <% } %>
    <%
      }
    %>
      </ul>
      <hr/>
    <%
    }
    %>



  </div>
</body>
</html>
