<!-- ✅ Sidebar -->
<div class="sidebar">
  <div class="sidebar-header">
    ERPStyle
  </div>
  <ul class="sidebar-menu">

    <li><a href="<%= request.getContextPath() %>/getFiltreEmploye">liste des employes</a></li>
    <li><a href="<%= request.getContextPath() %>/paies-all">fiches de paies </a></li>
    <li><a href="<%= request.getContextPath() %>/salaire-details-filtre">salaire details </a></li>

    <li><a href="/logout"> Déconnexion</a></li>
  </ul>
</div>

<style>
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    width: 220px;
    height: 100vh;
    background-color: #f0f2f5;
    border-right: 1px solid #dce1e7;
    box-shadow: 2px 0 5px rgba(0,0,0,0.03);
    font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
  }

  .sidebar-header {
    background-color: #4b6cb7;
    color: white;
    padding: 20px;
    font-size: 20px;
    font-weight: bold;
    text-align: center;
  }

  .sidebar-menu {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .sidebar-menu li {
    border-bottom: 1px solid #e5e9f0;
  }

  .sidebar-menu a {
    display: block;
    padding: 15px 20px;
    color: #333;
    text-decoration: none;
    transition: background-color 0.2s ease;
  }

  .sidebar-menu a:hover {
    background-color: #dbe3f3;
    color: #2b4f88;
  }
</style>
