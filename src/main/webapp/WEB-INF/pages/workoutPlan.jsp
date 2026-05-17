<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fitTrackPro.model.user" %>
<%@ page import="com.fitTrackPro.model.workoutPlan" %>
<%@ page import="java.util.List" %>
<%
    user user = (user) session.getAttribute("currentUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    List<workoutPlan> workoutPlans = (List<workoutPlan>) request.getAttribute("workoutPlans");
    boolean isMember = user.isMember();
    boolean isTrainer = user.isTrainer();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Workout Plans - FitTrack Pro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=exercise-video-fixed-18">
    <style>
        .workout-card {
            background: var(--white);
            border-radius: var(--border-radius);
            box-shadow: var(--box-shadow);
            margin-bottom: 20px;
            overflow: hidden;
        }
        
        .workout-header {
            background: var(--primary-color);
            color: white;
            padding: 15px 20px;
        }
        
        .workout-body {
            padding: 20px;
        }
        
        .exercise-list {
            display: grid;
            gap: 10px;
            line-height: 1.5;
        }

        .exercise-row {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 12px;
            padding: 10px 12px;
            border: 1px solid #dde5df;
            border-radius: 8px;
            background: #ffffff;
        }

        .exercise-text {
            color: var(--dark-color);
        }

        .watch-exercise {
            display: inline-flex;
            align-items: center;
            gap: 7px;
            padding: 7px 12px;
            border: 1px solid #cfd8d3;
            border-radius: 8px;
            background: #f8faf7;
            color: #0f766e;
            font-weight: 600;
            cursor: pointer;
            transition: var(--transition-fast);
            white-space: nowrap;
        }

        .watch-exercise:hover {
            background: #0f766e;
            border-color: #0f766e;
            color: #ffffff;
        }

        .watch-exercise svg {
            width: 17px;
            height: 17px;
            fill: currentColor;
        }

        .video-panel {
            display: none;
            position: fixed;
            inset: 0;
            z-index: 600;
            padding: 40px 24px;
            background: rgba(15, 23, 42, 0.72);
            align-items: center;
            justify-content: center;
        }

        .video-panel.active {
            display: flex;
        }

        .video-dialog {
            width: min(920px, 96vw);
            background: #ffffff;
            border-radius: 10px;
            box-shadow: 0 24px 70px rgba(0, 0, 0, 0.32);
            overflow: hidden;
        }

        .video-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 12px;
            padding: 14px 18px;
            border-bottom: 1px solid #dde5df;
        }

        .video-header h3 {
            margin: 0;
            font-size: 18px;
        }

        .video-close {
            border: 0;
            background: #fee2e2;
            color: #991b1b;
            border-radius: 7px;
            padding: 7px 10px;
            cursor: pointer;
            font-weight: 700;
        }

        .video-frame-wrap {
            aspect-ratio: 16 / 9;
            background: #0f172a;
        }

        .video-frame-wrap iframe {
            width: 100%;
            height: 100%;
            border: 0;
            display: block;
        }

        .video-message {
            display: none;
            padding: 34px 22px;
            color: #ffffff;
            text-align: center;
            background: #0f172a;
        }

        .video-message.active {
            display: block;
        }
        
        .workout-footer {
            background: var(--light-color);
            padding: 15px 20px;
            border-top: 1px solid var(--gray-light);
        }
    </style>
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
    
    <% if (isMember) { %>
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/memberDashboard" class="sidebar-link">Dashboard</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/member/profile" class="sidebar-link">My Profile</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/member/attendance" class="sidebar-link">Attendance History</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/member/workout" class="sidebar-link active">Workout Plans</a>
            </li>
        </ul>
    </div>
    <% } else if (isTrainer) { %>
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainerDashboard" class="sidebar-link">Dashboard</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/members" class="sidebar-link">My Members</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/workouts" class="sidebar-link active">Workout Plans</a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/trainer/create-workout" class="sidebar-link">Create Workout Plan</a>
            </li>
        </ul>
    </div>
    <% } %>
    
    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center" style="width:100%;display:flex;align-items:center;justify-content:space-between;">
            <h1>Workout Plans</h1>
            <% if (isTrainer) { %>
            <a href="${pageContext.request.contextPath}/trainer/create-workout" class="btn btn-primary">Create New Plan</a>
            <% } %>
        </div>
        
        <% if (request.getParameter("success") != null) { %>
            <div class="alert alert-success">
                <%= request.getParameter("success") %>
            </div>
        <% } %>
        
        <% if (workoutPlans != null && !workoutPlans.isEmpty()) { %>
            <% for (workoutPlan plan : workoutPlans) { %>
            <div class="workout-card">
                <div class="workout-header">
                    <div class="d-flex justify-content-between align-items-center" style="width:100%;display:flex;align-items:center;justify-content:space-between;">
                        <h3 style="margin: 0; color: white;"><%= plan.getPlanName() %></h3>
                        <span class="badge <%= plan.isActive() ? "badge-success" : "badge-secondary" %>" 
                              style="background: white; color: <%= plan.isActive() ? "#1d4ed8" : "#9aa4ad" %>;">
                            <%= plan.getStatus() %>
                        </span>
                    </div>
                </div>
                <div class="workout-body">
                    <div class="row">
                        <div class="col-6">
                            <p><strong>Trainer:</strong> <%= plan.getTrainerName() %></p>
                            <% if (isTrainer) { %>
                            <p><strong>Member:</strong> <%= plan.getMemberName() %></p>
                            <% } %>
                            <p><strong>Period:</strong> <%= plan.getStartDate() %> to <%= plan.getEndDate() %></p>
                        </div>
                        <div class="col-6">
                            <p><strong>Description:</strong></p>
                            <p><%= plan.getDescription() != null ? plan.getDescription() : "No description provided." %></p>
                        </div>
                    </div>
                    
                    <hr style="margin: 20px 0; border-color: #eee;">
                    
                    <h4>Exercises</h4>
                    <div class="exercise-list" data-video-exercises="true">
                        <%= plan.getExercises() != null ? plan.getExercises().replace("\n", "<br>") : "No exercises listed." %>
                    </div>
                    
                    <% if (plan.getNotes() != null && !plan.getNotes().isEmpty()) { %>
                    <hr style="margin: 20px 0; border-color: #eee;">
                    <h4>Notes</h4>
                    <p><%= plan.getNotes() %></p>
                    <% } %>
                </div>
                <div class="workout-footer">
                    <div class="d-flex justify-content-between">
                        <span class="text-muted">Created: <%= plan.getCreatedAt() %></span>
                        <% if (plan.isActive()) { %>
                        <span class="badge badge-success">Active Plan</span>
                        <% } else if (plan.isCompleted()) { %>
                        <span class="badge badge-secondary">Completed</span>
                        <% } %>
                    </div>
                </div>
            </div>
            <% } %>
        <% } else { %>
            <div class="card">
                <div class="card-body text-center" style="padding: 60px;">
                    <p class="text-muted">No workout plans found.</p>
                    <% if (isTrainer) { %>
                    <a href="${pageContext.request.contextPath}/trainer/create-workout" class="btn btn-primary" style="margin-top: 20px;">Create First Workout Plan</a>
                    <% } else { %>
                    <p>Your trainer will assign workout plans soon.</p>
                    <% } %>
                </div>
            </div>
        <% } %>
    </div>
    <div class="video-panel" id="exerciseVideoPanel" aria-hidden="true">
        <div class="video-dialog">
            <div class="video-header">
                <h3 id="exerciseVideoTitle">Exercise Video</h3>
                <button type="button" class="video-close" id="exerciseVideoClose">Close</button>
            </div>
            <div class="video-frame-wrap">
                <iframe id="exerciseVideoFrame" title="Exercise video" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>
                <div class="video-message" id="exerciseVideoMessage">No embedded video is available for this exercise yet.</div>
            </div>
        </div>
    </div>
    <script>
        (function () {
            var panel = document.getElementById('exerciseVideoPanel');
            var frame = document.getElementById('exerciseVideoFrame');
            var title = document.getElementById('exerciseVideoTitle');
            var close = document.getElementById('exerciseVideoClose');
            var message = document.getElementById('exerciseVideoMessage');
            var videoMap = [
                { keys: ['squat', 'squats'], id: 'aclHkVaku9U' },
                { keys: ['push-up', 'push up', 'pushups', 'push-ups'], id: 'IODxDxX7oi4' },
                { keys: ['dumbbell row', 'dumbbell rows', 'db row', 'one arm row'], id: 'roCP6wCXPqo' },
                { keys: ['plank', 'planks'], id: 'pSHjTRCQxIw' },
                { keys: ['bench press'], id: 'rT7DgCr-3pg' },
                { keys: ['deadlift', 'deadlifts'], id: 'ytGaGIn3SjE' },
                { keys: ['lunges', 'lunge'], id: 'QOVaHwm-Q6U' },
                { keys: ['burpee', 'burpees'], id: 'TU8QYVW0gDU' },
                { keys: ['mountain climber', 'mountain climbers'], id: 'nmwgirgXLYM' },
                { keys: ['jumping jack', 'jumping jacks'], id: 'c4DAnQ6DtF8' },
                { keys: ['bicep curl', 'biceps curl', 'curl'], id: 'ykJmrZ5v0Oo' },
                { keys: ['shoulder press', 'overhead press'], id: 'qEwKCR5JCog' }
            ];

            function cleanExerciseName(text) {
                return text.replace(/:\s*.*/, '').replace(/\s+/g, ' ').trim();
            }

            function findVideoId(exercise) {
                var name = cleanExerciseName(exercise).toLowerCase();
                for (var i = 0; i < videoMap.length; i++) {
                    for (var j = 0; j < videoMap[i].keys.length; j++) {
                        if (name.indexOf(videoMap[i].keys[j]) !== -1) {
                            return videoMap[i].id;
                        }
                    }
                }
                return null;
            }

            function openVideo(exercise) {
                title.textContent = cleanExerciseName(exercise);
                var videoId = findVideoId(exercise);
                if (videoId) {
                    message.classList.remove('active');
                    frame.style.display = 'block';
                    frame.src = 'https://www.youtube-nocookie.com/embed/' + videoId + '?autoplay=1&rel=0';
                } else {
                    frame.src = '';
                    frame.style.display = 'none';
                    message.classList.add('active');
                    message.textContent = 'No embedded video is available for ' + cleanExerciseName(exercise) + ' yet.';
                }
                panel.classList.add('active');
                panel.setAttribute('aria-hidden', 'false');
            }

            function closeVideo() {
                frame.src = '';
                frame.style.display = 'block';
                message.classList.remove('active');
                panel.classList.remove('active');
                panel.setAttribute('aria-hidden', 'true');
            }

            document.querySelectorAll('[data-video-exercises="true"]').forEach(function (list) {
                var lines = list.innerHTML.split(/<br\s*\/?>/i).map(function (line) {
                    var temp = document.createElement('div');
                    temp.innerHTML = line;
                    return temp.textContent.trim();
                }).filter(Boolean);

                if (!lines.length || lines[0] === 'No exercises listed.') {
                    return;
                }

                list.innerHTML = '';
                lines.forEach(function (line) {
                    var row = document.createElement('div');
                    row.className = 'exercise-row';

                    var text = document.createElement('span');
                    text.className = 'exercise-text';
                    text.textContent = line;

                    var button = document.createElement('button');
                    button.type = 'button';
                    button.className = 'watch-exercise';
                    button.innerHTML = '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M8 5v14l11-7-11-7Z"></path></svg><span>Watch</span>';
                    button.addEventListener('click', function () {
                        openVideo(line);
                    });

                    row.appendChild(text);
                    row.appendChild(button);
                    list.appendChild(row);
                });
            });

            close.addEventListener('click', closeVideo);
            panel.addEventListener('click', function (event) {
                if (event.target === panel) {
                    closeVideo();
                }
            });
            document.addEventListener('keydown', function (event) {
                if (event.key === 'Escape') {
                    closeVideo();
                }
            });
        })();
    </script>
</body>
</html>
