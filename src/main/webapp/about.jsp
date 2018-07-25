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

<%
String user = (String) request.getSession().getAttribute("user");
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
      <a href="/logout?post_logout_redirect=/about.jsp">Logout</a>
    <% } %>
  </nav>

  <div id="container">
    <div
      style="width:75%; margin-left:auto; margin-right:auto; margin-top: 50px;">

      <h1>About Git Rekt's Chat App </h1>
      <p>
        This chat application is a project under development by Git Rekt (Team 1). Here
        is some background on the site and developers:
      </p>

      <h2>Features</h2>
      <ul>
        <li><strong>Profile Pages:</strong> You can see your own profile page by
            clicking the Hello &lt;name&gt;! in the navigation bar when you are
            logged in, and other users' profiles can be accessed by clicking their
            name in a Conversation! Profile pages have an "About Me" section which
            can be updated.</li>

        <li><strong>Styled Text:</strong> Almost all HTML tags can be
            used in messages, the exception being some potentially 'dangerous'
            tags like &lt;img&gt; and &lt;a&gt;!</li>

        <li><strong>Admin Page:</strong> Basic stats for the site can be found on
            the Admin Page, as well as a few administrative deletion buttons to
            delete all Users, Messages, or Conversations.</li>

        <li><strong>Direct Messages:</strong> A direct message conversation can be
            started with another user by going to their profile page and clicking the
            "Message" button! If a direct message conversation already exists between
            you two, the existing one will be opened. Only the two specific users are
            able to access it.</li>

        <li><strong>Group Conversations:</strong> A group conversation is made
            by ticking the "Group" checkbox before you create the conversation.
            The creator will be able to add other users to the group conversation
            through an "Add User" input box. Only the creator and users that are
            added to the conversation can access it.</li>

      </ul>
      <h2>Developers</h2>
      <ul>
        <li><strong>Vasuman Ravichandran:</strong> Git Rekt's CodeU project advisor.</li>
        <li><strong>Cynthia Serrano Najera:</strong> Is a student at Wellesley
            College and studies Computer Science and Latinx Studies. She has an
            interest in photography and comic books.</li>

        <li><strong>Justin Mah:</strong> Is a student at the University of
            Alberta and studies Computer Science. He also has an avid interest
            in anime and studying Japanese.</li>

        <li><strong>Sergio Castanon:</strong> Is a student at the Univeristy of
            Utah. He is interested in cars, especially Japanese cars.</li>
      </ul>
      </br>
    </div>
  </div>
</body>
</html>