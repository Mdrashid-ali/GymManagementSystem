<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="com.fitTrackPro.model.trainer" %>
<%@ page import="com.fitTrackPro.model.workoutPlan" %>
<%@ page import="com.fitTrackPro.model.member" %>
<%@ page import="java.util.List" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null || !user.isTrainer()) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    trainer trainer = (trainer) request.getAttribute("trainer");
    List<workoutPlan> workoutPlans = (List<workoutPlan>) request.getAttribute("workoutPlans");
    List<member> members = (List<member>) request.getAttribute("members");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trainer Dashboard - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=dashboard-sort-search-19">
</head>
<body>
        <div class="navbar">
        <div class="d-flex justify-content-between align-items-center" style="width:100%;display:flex;align-items:center;justify-content:space-between;">
            <a href="${pageContext.request.contextPath}<%= ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).isAdmin() ? "/adminDashboard" : ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).isTrainer() ? "/trainerDashboard" : "/memberDashboard" %>" class="navbar-brand">FitTrack Pro</a>
            <div class="navbar-actions" style="margin-left:auto;display:inline-flex;align-items:center;gap:12px;"><div class="navbar-user-card"><span class="navbar-user-name"><%= ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).getDisplayName() %></span><span class="navbar-user-role"><%= ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).isAdmin() ? "Admin" : ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).isTrainer() ? "Trainer" : "Member" %></span></div><a href="${pageContext.request.contextPath}/logout" class="btn btn-outline btn-sm navbar-logout" title="Logout">
                <svg class="logout-icon" viewBox="0 0 24 24" aria-hidden="true"><path d="M4 4.5 11 2v20l-7-2.5v-15Z"></path><path d="M12.5 5H18v4h-2V7h-3.5V5Zm0 12H16v-2h2v4h-5.5v-2Z"></path><path d="M15 8.5 21 12l-6 3.5V13h-5v-2h5V8.5Z"></path><circle cx="8" cy="12" r="0.8" fill="#ffffff"></circle></svg>
                <span>Logout</span>
            </a></div>
        </div>
    </div>
    
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainerDashboard" class="sidebar-link active">Dashboard</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/members" class="sidebar-link">My Members</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/workouts" class="sidebar-link">Workout Plans</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/create-workout" class="sidebar-link">Create Workout Plan</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/attendance" class="sidebar-link">Attendance</a>
            </li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/about" class="sidebar-link">About</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/services" class="sidebar-link">Services</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/contact" class="sidebar-link">Contact</a></li>
        </ul>
    </div>
    
    <div class="main-content">
        <h1>Trainer Dashboard</h1>
        
        <% if (request.getParameter("success") != null) { %>
            <div class="alert alert-success">
                <%= request.getParameter("success") %>
            </div>
        <% } %>
        
        <div class="row">
            <div class="col-6">
                <div class="card">
                    <div class="card-header">
                        <h3>Trainer Information</h3>
                    </div>
                    <div class="card-body">
                        <% if (trainer != null) { %>
                            <p><strong>Name:</strong> <%= trainer.getFullName() %></p>
                            <p><strong>Specialization:</strong> <%= trainer.getSpecialization() %></p>
                            <p><strong>Experience:</strong> <%= trainer.getExperienceYears() %> years</p>
                            <p><strong>Certification:</strong> <%= trainer.getCertification() %></p>
                        <% } %>
                    </div>
                </div>
            </div>
            
            <div class="col-6">
                <div class="card">
                    <div class="card-header">
                        <h3>Quick Actions</h3>
                    </div>
                    <div class="card-body">
                        <a href="${pageContext.request.contextPath}/trainer/create-workout" class="btn btn-primary btn-block">Create New Workout Plan</a>
                        <a href="${pageContext.request.contextPath}/trainer/members" class="btn btn-outline btn-block" style="margin-top: 10px;">View All Members</a>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="card" style="margin-top: 20px;">
            <div class="card-header">
                <h3>Recent Workout Plans</h3>
            </div>
            <div class="card-body">
                <% if (workoutPlans != null && !workoutPlans.isEmpty()) { %>
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Plan Name</th>
                                    <th>Member</th>
                                    <th>Start Date</th>
                                    <th>End Date</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% 
                                int count = 0;
                                for (workoutPlan plan : workoutPlans) {
                                    if (count++ >= 5) break;
                                %>
                                <tr>
                                    <td><%= plan.getPlanName() %></td>
                                    <td><%= plan.getMemberName() %></td>
                                    <td><%= plan.getStartDate() %></td>
                                    <td><%= plan.getEndDate() %></td>
                                    <td>
                                        <span class="badge <%= plan.isActive() ? "badge-success" : "badge-secondary" %>">
                                            <%= plan.getStatus() %>
                                        </span>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } else { %>
                    <p>No workout plans created yet.</p>
                <% } %>
            </div>
        </div>
    </div>
<script>
(function () {
    function normalize(value) {
        return (value || '').toString().trim().toLowerCase();
    }

    function comparable(value) {
        var clean = normalize(value).replace(/,/g, '');
        var date = Date.parse(clean);
        if (!Number.isNaN(date) && /\d{4}-\d{2}-\d{2}|\w+ \d{1,2}, \d{4}/.test(value)) return date;
        var number = parseFloat(clean.replace(/[^0-9.-]/g, ''));
        if (!Number.isNaN(number) && /[0-9]/.test(clean)) return number;
        return clean;
    }

    function cellText(row, index) {
        var cell = row.cells[index];
        return cell ? cell.textContent.trim() : '';
    }

    function sortRows(table, columnIndex, direction) {
        var tbody = table.tBodies[0];
        if (!tbody) return [];
        var rows = Array.from(tbody.rows).filter(function (row) { return row.cells.length > 1; });
        rows.sort(function (a, b) {
            var av = comparable(cellText(a, columnIndex));
            var bv = comparable(cellText(b, columnIndex));
            if (av < bv) return direction === 'asc' ? -1 : 1;
            if (av > bv) return direction === 'asc' ? 1 : -1;
            return 0;
        });
        rows.forEach(function (row) { tbody.appendChild(row); });
        table.querySelectorAll('th').forEach(function (th) { th.classList.remove('sorted-asc', 'sorted-desc'); });
        var header = table.querySelectorAll('th')[columnIndex];
        if (header) header.classList.add(direction === 'asc' ? 'sorted-asc' : 'sorted-desc');
        return rows;
    }

    function lowerBound(rows, columnIndex, query) {
        var low = 0;
        var high = rows.length;
        while (low < high) {
            var mid = Math.floor((low + high) / 2);
            if (normalize(cellText(rows[mid], columnIndex)) < query) low = mid + 1;
            else high = mid;
        }
        return low;
    }

    function enhanceTable(table, tableNumber) {
        if (table.dataset.toolsReady === 'true' || !table.tHead || !table.tBodies.length) return;
        var headers = Array.from(table.tHead.rows[0].cells);
        var rows = Array.from(table.tBodies[0].rows).filter(function (row) { return row.cells.length > 1; });
        if (!headers.length || !rows.length) return;
        table.dataset.toolsReady = 'true';

        headers.forEach(function (th, index) {
            if (normalize(th.textContent) === 'actions') return;
            th.classList.add('sortable');
            th.dataset.direction = 'asc';
            th.addEventListener('click', function () {
                var direction = th.dataset.direction === 'asc' ? 'desc' : 'asc';
                th.dataset.direction = direction;
                sortRows(table, index, direction);
            });
        });

        var tools = document.createElement('div');
        tools.className = 'table-tools';
        tools.innerHTML = '<label for="tableSearch' + tableNumber + '">Binary Search</label>'
            + '<select id="tableColumn' + tableNumber + '"></select>'
            + '<input id="tableSearch' + tableNumber + '" type="search" placeholder="Enter exact starting text">'
            + '<button type="button" class="btn btn-primary btn-sm table-search-btn">Search</button>'
            + '<button type="button" class="btn btn-outline btn-sm table-reset-btn">Reset</button>'
            + '<span class="search-status">Click a column heading to sort.</span>';

        var select = tools.querySelector('select');
        headers.forEach(function (th, index) {
            if (normalize(th.textContent) === 'actions') return;
            var option = document.createElement('option');
            option.value = index;
            option.textContent = th.textContent.trim();
            select.appendChild(option);
        });

        var container = table.closest('.table-container') || table;
        container.parentNode.insertBefore(tools, container);

        var input = tools.querySelector('input');
        var status = tools.querySelector('.search-status');
        var originalRows = rows.slice();

        tools.querySelector('.table-search-btn').addEventListener('click', function () {
            var query = normalize(input.value);
            var columnIndex = parseInt(select.value, 10);
            rows = sortRows(table, columnIndex, 'asc');
            rows.forEach(function (row) {
                row.style.display = '';
                row.classList.remove('binary-match');
            });
            if (!query) {
                status.textContent = 'Enter a value to binary search.';
                return;
            }
            var start = lowerBound(rows, columnIndex, query);
            var matches = [];
            for (var i = start; i < rows.length; i++) {
                var text = normalize(cellText(rows[i], columnIndex));
                if (!text.startsWith(query)) break;
                matches.push(rows[i]);
            }
            rows.forEach(function (row) { row.style.display = 'none'; });
            matches.forEach(function (row) {
                row.style.display = '';
                row.classList.add('binary-match');
            });
            status.textContent = matches.length ? matches.length + ' matching row(s) found.' : 'No matching row found.';
        });

        input.addEventListener('keydown', function (event) {
            if (event.key === 'Enter') tools.querySelector('.table-search-btn').click();
        });

        tools.querySelector('.table-reset-btn').addEventListener('click', function () {
            var tbody = table.tBodies[0];
            originalRows.forEach(function (row) {
                row.style.display = '';
                row.classList.remove('binary-match');
                tbody.appendChild(row);
            });
            table.querySelectorAll('th').forEach(function (th) { th.classList.remove('sorted-asc', 'sorted-desc'); });
            input.value = '';
            status.textContent = 'Click a column heading to sort.';
        });
    }

    document.querySelectorAll('.main-content table.table').forEach(function (table, index) {
        enhanceTable(table, index + 1);
    });
})();
</script>
</body>
</html>
