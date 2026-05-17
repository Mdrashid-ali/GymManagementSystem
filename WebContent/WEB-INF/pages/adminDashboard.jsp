<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.member"%>
<%@ page import="com.fitTrackPro.model.trainer"%>
<%@ page import="java.util.List"%>
<%
List<member> allMembers = (List<member>) request.getAttribute("allMembers");
List<trainer> allTrainers = (List<trainer>) request.getAttribute("allTrainers");
String view = (String) request.getAttribute("view");
boolean dashboardView = view == null || "dashboard".equals(view);
boolean memberView = "members".equals(view);
boolean trainerView = "trainers".equals(view);
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Admin Dashboard - FitTrack Pro</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css?v=dashboard-sort-search-19">
</head>
<body>
	<div class="navbar">
		<div class="d-flex justify-content-between align-items-center"
			style="width: 100%; display: flex; align-items: center; justify-content: space-between;">
			<a
				href="${pageContext.request.contextPath}<%= ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).isAdmin() ? "/adminDashboard" : ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).isTrainer() ? "/trainerDashboard" : "/memberDashboard" %>"
				class="navbar-brand">FitTrack Pro</a>
			<div class="navbar-actions"
				style="margin-left: auto; display: inline-flex; align-items: center; gap: 12px;">
				<div class="navbar-user-card">
					<span class="navbar-user-name"><%=((com.fitTrackPro.model.user) session.getAttribute("currentUser")).getDisplayName()%></span><span
						class="navbar-user-role"><%=((com.fitTrackPro.model.user) session.getAttribute("currentUser")).isAdmin()
		? "Admin"
		: ((com.fitTrackPro.model.user) session.getAttribute("currentUser")).isTrainer() ? "Trainer" : "Member"%></span>
				</div>
				<a href="${pageContext.request.contextPath}/logout"
					class="btn btn-outline btn-sm navbar-logout" title="Logout"> <svg
						class="logout-icon" viewBox="0 0 24 24" aria-hidden="true">
						<path d="M4 4.5 11 2v20l-7-2.5v-15Z"></path>
						<path d="M12.5 5H18v4h-2V7h-3.5V5Zm0 12H16v-2h2v4h-5.5v-2Z"></path>
						<path d="M15 8.5 21 12l-6 3.5V13h-5v-2h5V8.5Z"></path>
						<circle cx="8" cy="12" r="0.8" fill="#ffffff"></circle></svg> <span>Logout</span>
				</a>
			</div>
		</div>
	</div>

	<div class="sidebar">
		<ul class="sidebar-menu">
			<li class="sidebar-item"><a
				href="${pageContext.request.contextPath}/adminDashboard"
				class="sidebar-link <%= dashboardView ? "active" : "" %>">Dashboard</a>
			</li>
			<li class="sidebar-item"><a
				href="${pageContext.request.contextPath}/admin/members"
				class="sidebar-link <%= memberView ? "active" : "" %>">Manage
					Members</a></li>
			<li class="sidebar-item"><a
				href="${pageContext.request.contextPath}/admin/trainers"
				class="sidebar-link <%= trainerView ? "active" : "" %>">Manage
					Trainers</a></li>
			<li class="sidebar-item"><a
				href="${pageContext.request.contextPath}/admin/add-member"
				class="sidebar-link">Add Member</a></li>
			<li class="sidebar-item"><a
				href="${pageContext.request.contextPath}/admin/add-trainer"
				class="sidebar-link">Add Trainer</a></li>
			<li class="sidebar-item"><a
				href="${pageContext.request.contextPath}/about" class="sidebar-link">About</a></li>
			<li class="sidebar-item"><a
				href="${pageContext.request.contextPath}/services"
				class="sidebar-link">Services</a></li>
			<li class="sidebar-item"><a
				href="${pageContext.request.contextPath}/contact"
				class="sidebar-link">Contact</a></li>
		</ul>
	</div>

	<div class="main-content">
		<h1><%=trainerView ? "Manage Trainers" : memberView ? "Manage Members" : "Admin Dashboard"%></h1>
		<%
		if (request.getAttribute("error") != null) {
		%>
		<div class="alert alert-danger"><%=request.getAttribute("error")%></div>
		<%
		}
		%>
		<%
		if (request.getParameter("success") != null) {
		%>
		<div class="alert alert-success"><%=request.getParameter("success")%></div>
		<%
		}
		%>

		<div class="row">
			<div class="col-3">
				<div class="stat-card">
					<div class="stat-value"><%=request.getAttribute("totalMembers") != null ? request.getAttribute("totalMembers") : 0%></div>
					<div class="stat-label">Total Members</div>
				</div>
			</div>
			<div class="col-3">
				<div class="stat-card">
					<div class="stat-value" style="color: #1d4ed8;"><%=request.getAttribute("activeMembers") != null ? request.getAttribute("activeMembers") : 0%></div>
					<div class="stat-label">Active Members</div>
				</div>
			</div>
			<div class="col-3">
				<div class="stat-card">
					<div class="stat-value" style="color: #e74c3c;"><%=request.getAttribute("expiredMembers") != null ? request.getAttribute("expiredMembers") : 0%></div>
					<div class="stat-label">Expired Memberships</div>
				</div>
			</div>
			<div class="col-3">
				<div class="stat-card">
					<div class="stat-value" style="color: #3498db;"><%=request.getAttribute("totalTrainers") != null ? request.getAttribute("totalTrainers") : 0%></div>
					<div class="stat-label">Total Trainers</div>
				</div>
			</div>
		</div>

		<%
		if (dashboardView || memberView) {
		%>
		<div class="card" style="margin-top: 20px;">
			<div
				class="card-header d-flex justify-content-between align-items-center">
				<h3>Members</h3>
				<a href="${pageContext.request.contextPath}/admin/add-member"
					class="btn btn-primary btn-sm">Add Member</a>
			</div>
			<div class="card-body">
				<div class="table-container">
					<table class="table">
						<thead>
							<tr>
								<th>ID</th>
								<th>Name</th>
								<th>Email</th>
								<th>Membership</th>
								<th>Expiry Date</th>
								<th>Status</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
							<%
							if (allMembers != null && !allMembers.isEmpty()) {
							%>
							<%
							for (member member : allMembers) {
							%>
							<tr>
								<td><%=member.getMemberId()%></td>
								<td><%=member.getFullName()%></td>
								<td><%=member.getEmail()%></td>
								<td><%=member.getMembershipType()%></td>
								<td><%=member.getMembershipExpiryDate()%></td>
								<td><span
									class="badge <%=member.isMembershipActive() ? "badge-success" : "badge-warning"%>">
										<%=member.isMembershipActive() ? "Active" : "Expired"%>
								</span></td>
								<td><a
									href="${pageContext.request.contextPath}/admin/update-member?memberId=<%= member.getMemberId() %>"
									class="btn btn-outline btn-sm">Edit</a>
									<form
										action="${pageContext.request.contextPath}/admin/delete-member"
										method="post" style="display: inline;"
										onsubmit="return confirm('Delete this member account permanently?');">
										<input type="hidden" name="memberId"
											value="<%=member.getMemberId()%>">
										<button type="submit" class="btn btn-danger btn-sm"
											style="margin-left: 6px;">Delete</button>
									</form></td>
							</tr>
							<%
							}
							%>
							<%
							} else {
							%>
							<tr>
								<td colspan="7" class="text-center">No members found.</td>
							</tr>
							<%
							}
							%>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<%
		}
		%>

		<%
		if (dashboardView || trainerView) {
		%>
		<div class="card" style="margin-top: 20px;">
			<div
				class="card-header d-flex justify-content-between align-items-center">
				<h3>Trainers</h3>
				<a href="${pageContext.request.contextPath}/admin/add-trainer"
					class="btn btn-primary btn-sm">Add Trainer</a>
			</div>
			<div class="card-body">
				<div class="table-container">
					<table class="table">
						<thead>
							<tr>
								<th>ID</th>
								<th>Name</th>
								<th>Email</th>
								<th>Phone</th>
								<th>Specialization</th>
								<th>Experience</th>
								<th>Status</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
							<%
							if (allTrainers != null && !allTrainers.isEmpty()) {
							%>
							<%
							for (trainer trainer : allTrainers) {
							%>
							<tr>
								<td><%=trainer.getTrainerId()%></td>
								<td><%=trainer.getFullName()%></td>
								<td><%=trainer.getEmail()%></td>
								<td><%=trainer.getPhone() == null ? "" : trainer.getPhone()%></td>
								<td><%=trainer.getSpecialization() == null ? "" : trainer.getSpecialization()%></td>
								<td><%=trainer.getExperienceYears()%> years</td>
								<td><span class="badge badge-success"><%=trainer.getStatus() == null ? "ACTIVE" : trainer.getStatus()%></span></td>
								<td><a
									href="${pageContext.request.contextPath}/admin/update-trainer?trainerId=<%= trainer.getTrainerId() %>"
									class="btn btn-outline btn-sm">Edit</a>
									<form
										action="${pageContext.request.contextPath}/admin/delete-trainer"
										method="post" style="display: inline;"
										onsubmit="return confirm('Delete this trainer account permanently?');">
										<input type="hidden" name="trainerId"
											value="<%=trainer.getTrainerId()%>">
										<button type="submit" class="btn btn-danger btn-sm"
											style="margin-left: 6px;">Delete</button>
									</form></td>
							</tr>
							<%
							}
							%>
							<%
							} else {
							%>
							<tr>
								<td colspan="8" class="text-center">No trainers found.</td>
							</tr>
							<%
							}
							%>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<%
		}
		%>
	</div>
	<script>
		(function() {
			function normalize(value) {
				return (value || '').toString().trim().toLowerCase();
			}

			function comparable(value) {
				var clean = normalize(value).replace(/,/g, '');
				var date = Date.parse(clean);
				if (!Number.isNaN(date)
						&& /\d{4}-\d{2}-\d{2}|\w+ \d{1,2}, \d{4}/.test(value))
					return date;
				var number = parseFloat(clean.replace(/[^0-9.-]/g, ''));
				if (!Number.isNaN(number) && /[0-9]/.test(clean))
					return number;
				return clean;
			}

			function cellText(row, index) {
				var cell = row.cells[index];
				return cell ? cell.textContent.trim() : '';
			}

			function sortRows(table, columnIndex, direction) {
				var tbody = table.tBodies[0];
				if (!tbody)
					return [];
				var rows = Array.from(tbody.rows).filter(function(row) {
					return row.cells.length > 1;
				});
				rows.sort(function(a, b) {
					var av = comparable(cellText(a, columnIndex));
					var bv = comparable(cellText(b, columnIndex));
					if (av < bv)
						return direction === 'asc' ? -1 : 1;
					if (av > bv)
						return direction === 'asc' ? 1 : -1;
					return 0;
				});
				rows.forEach(function(row) {
					tbody.appendChild(row);
				});
				table.querySelectorAll('th').forEach(function(th) {
					th.classList.remove('sorted-asc', 'sorted-desc');
				});
				var header = table.querySelectorAll('th')[columnIndex];
				if (header)
					header.classList.add(direction === 'asc' ? 'sorted-asc'
							: 'sorted-desc');
				return rows;
			}

			function lowerBound(rows, columnIndex, query) {
				var low = 0;
				var high = rows.length;
				while (low < high) {
					var mid = Math.floor((low + high) / 2);
					if (normalize(cellText(rows[mid], columnIndex)) < query)
						low = mid + 1;
					else
						high = mid;
				}
				return low;
			}

			function enhanceTable(table, tableNumber) {
				if (table.dataset.toolsReady === 'true' || !table.tHead
						|| !table.tBodies.length)
					return;
				var headers = Array.from(table.tHead.rows[0].cells);
				var rows = Array.from(table.tBodies[0].rows).filter(
						function(row) {
							return row.cells.length > 1;
						});
				if (!headers.length || !rows.length)
					return;
				table.dataset.toolsReady = 'true';

				headers.forEach(function(th, index) {
					if (normalize(th.textContent) === 'actions')
						return;
					th.classList.add('sortable');
					th.dataset.direction = 'asc';
					th.addEventListener('click', function() {
						var direction = th.dataset.direction === 'asc' ? 'desc'
								: 'asc';
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
				headers.forEach(function(th, index) {
					if (normalize(th.textContent) === 'actions')
						return;
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

				tools
						.querySelector('.table-search-btn')
						.addEventListener(
								'click',
								function() {
									var query = normalize(input.value);
									var columnIndex = parseInt(select.value, 10);
									rows = sortRows(table, columnIndex, 'asc');
									rows.forEach(function(row) {
										row.style.display = '';
										row.classList.remove('binary-match');
									});
									if (!query) {
										status.textContent = 'Enter a value to binary search.';
										return;
									}
									var start = lowerBound(rows, columnIndex,
											query);
									var matches = [];
									for (var i = start; i < rows.length; i++) {
										var text = normalize(cellText(rows[i],
												columnIndex));
										if (!text.startsWith(query))
											break;
										matches.push(rows[i]);
									}
									rows.forEach(function(row) {
										row.style.display = 'none';
									});
									matches.forEach(function(row) {
										row.style.display = '';
										row.classList.add('binary-match');
									});
									status.textContent = matches.length ? matches.length
											+ ' matching row(s) found.'
											: 'No matching row found.';
								});

				input.addEventListener('keydown', function(event) {
					if (event.key === 'Enter')
						tools.querySelector('.table-search-btn').click();
				});

				tools
						.querySelector('.table-reset-btn')
						.addEventListener(
								'click',
								function() {
									var tbody = table.tBodies[0];
									originalRows.forEach(function(row) {
										row.style.display = '';
										row.classList.remove('binary-match');
										tbody.appendChild(row);
									});
									table.querySelectorAll('th').forEach(
											function(th) {
												th.classList.remove(
														'sorted-asc',
														'sorted-desc');
											});
									input.value = '';
									status.textContent = 'Click a column heading to sort.';
								});
			}

			document.querySelectorAll('.main-content table.table').forEach(
					function(table, index) {
						enhanceTable(table, index + 1);
					});
		})();
	</script>
</body>
</html>