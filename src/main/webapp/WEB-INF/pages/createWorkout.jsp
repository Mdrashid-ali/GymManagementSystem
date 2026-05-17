<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="com.fitTrackPro.model.member" %>
<%@ page import="java.util.List" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null || !user.isTrainer()) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    List<member> members = (List<member>) request.getAttribute("members");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Workout Plan - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=brand-dashboard-16">
    <style>
        .workout-validation-error {
            display: none;
            margin-bottom: 18px;
        }
        .workout-validation-error.show {
            display: block;
        }
        .field-error {
            display: none;
            color: #dc2626;
            font-size: 0.86rem;
            margin-top: 6px;
        }
        .field-error.show {
            display: block;
        }
        .form-control.input-error {
            border-color: #dc2626;
            box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.12);
        }
    </style></head>
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
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainerDashboard" class="sidebar-link">Dashboard</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/members" class="sidebar-link">My Members</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/workouts" class="sidebar-link">Workout Plans</a></li>
            <li class="sidebar-item"><a href="${pageContext.request.contextPath}/trainer/create-workout" class="sidebar-link active">Create Workout Plan</a></li>
        </ul>
    </div>

    <div class="main-content">
        <h1>Create Workout Plan</h1>
        <% if (request.getParameter("error") != null) { %>
            <div class="alert alert-danger"><%= request.getParameter("error") %></div>
        <% } %>

        <div class="card">
            <div class="card-header"><h3>Plan Details</h3></div>
            <div class="card-body">
                <form id="createWorkoutForm" action="${pageContext.request.contextPath}/trainer/create-workout" method="post" novalidate>
                    <div id="workoutValidationMessage" class="alert alert-danger workout-validation-error" role="alert"></div>
                    <div class="form-group">
                        <label for="memberId">Member</label>
                        <select id="memberId" name="memberId" class="form-control" data-required-message="Please select a member.">
                            <option value="">Select member</option>
                            <% if (members != null) { %>
                                <% for (member member : members) { %>
                                    <option value="<%= member.getMemberId() %>"><%= member.getFullName() %> (<%= member.getEmail() %>)</option>
                                <% } %>
                            <% } %>
                        </select>
                        <div class="field-error" data-error-for="memberId"></div>
                    </div>

                    <div class="form-group">
                        <label for="planName">Plan Name</label>
                        <input type="text" id="planName" name="planName" class="form-control" data-required-message="Please enter the plan name.">
                        <div class="field-error" data-error-for="planName"></div>
                    </div>

                    <div class="row">
                        <div class="col-6">
                            <div class="form-group">
                                <label for="startDate">Start Date</label>
                                <input type="date" id="startDate" name="startDate" class="form-control" data-required-message="Please choose a start date.">
                                <div class="field-error" data-error-for="startDate"></div>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="form-group">
                                <label for="endDate">End Date</label>
                                <input type="date" id="endDate" name="endDate" class="form-control" data-required-message="Please choose an end date.">
                                <div class="field-error" data-error-for="endDate"></div>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="description">Description</label>
                        <textarea id="description" name="description" class="form-control" rows="3"></textarea>
                    </div>

                    <div class="form-group">
                        <label for="exercises">Exercises</label>
                        <textarea id="exercises" name="exercises" class="form-control" rows="8" data-required-message="Please enter the exercises for this plan."></textarea>
                        <div class="field-error" data-error-for="exercises"></div>
                    </div>

                    <div class="form-group">
                        <label for="notes">Notes</label>
                        <textarea id="notes" name="notes" class="form-control" rows="3"></textarea>
                    </div>

                    <div class="d-flex justify-content-between">
                        <a href="${pageContext.request.contextPath}/trainer/workouts" class="btn btn-outline">Cancel</a>
                        <button type="submit" class="btn btn-primary">Create Plan</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <script>
        var createWorkoutForm = document.getElementById('createWorkoutForm');
        var workoutMessage = document.getElementById('workoutValidationMessage');
        var requiredWorkoutFields = ['memberId', 'planName', 'startDate', 'endDate', 'exercises'];

        function showFieldError(input, message) {
            var error = document.querySelector('[data-error-for="' + input.id + '"]');
            input.classList.add('input-error');
            if (error) {
                error.textContent = message;
                error.classList.add('show');
            }
        }

        function clearFieldError(input) {
            var error = document.querySelector('[data-error-for="' + input.id + '"]');
            input.classList.remove('input-error');
            if (error) {
                error.textContent = '';
                error.classList.remove('show');
            }
        }

        createWorkoutForm.addEventListener('submit', function(event) {
            var messages = [];
            var firstInvalid = null;

            requiredWorkoutFields.forEach(function(fieldId) {
                var input = document.getElementById(fieldId);
                clearFieldError(input);
                if (input.value.trim() === '') {
                    var message = input.dataset.requiredMessage;
                    messages.push(message);
                    showFieldError(input, message);
                    if (!firstInvalid) {
                        firstInvalid = input;
                    }
                }
            });

            var startDate = document.getElementById('startDate');
            var endDate = document.getElementById('endDate');
            if (startDate.value && endDate.value && endDate.value < startDate.value) {
                var dateMessage = 'End date cannot be before start date.';
                messages.push(dateMessage);
                showFieldError(endDate, dateMessage);
                if (!firstInvalid) {
                    firstInvalid = endDate;
                }
            }

            if (messages.length > 0) {
                event.preventDefault();
                workoutMessage.textContent = 'Please complete the required fields before creating the plan.';
                workoutMessage.classList.add('show');
                firstInvalid.focus();
            }
        });

        requiredWorkoutFields.forEach(function(fieldId) {
            var input = document.getElementById(fieldId);
            input.addEventListener('input', function() {
                clearFieldError(input);
                workoutMessage.classList.remove('show');
            });
            input.addEventListener('change', function() {
                clearFieldError(input);
                workoutMessage.classList.remove('show');
            });
        });
    </script></body>
</html>
