import { getDashboardSummary, getMemberProfile } from "./api.js";

const memberId = localStorage.getItem("memberId");

if (!memberId) {
    window.location.href = "../login.html";
}

const setText = (id, value) => {
    const el = document.getElementById(id);
    if (el) el.textContent = value;
};

function formatDate(dateStr) {
    if (!dateStr) return "Not available";
    try {
        const [year, month, day] = dateStr.split("-");
        const date = new Date(year, Number(month) - 1, day);
        return date.toLocaleDateString(undefined, {
            year: "numeric",
            month: "short",
            day: "numeric"
        });
    } catch {
        return dateStr;
    }
}

function getBmiCategory(bmi) {
    if (!bmi) return "Based on height & weight";
    if (bmi < 18.5) return "Underweight range";
    if (bmi < 24.9) return "Normal / Healthy range";
    if (bmi < 29.9) return "Overweight range";
    return "Obese range";
}

async function loadDashboard() {
    try {
        let summary = null;
        try {
            summary = await getDashboardSummary(memberId);
        } catch (apiErr) {
            console.warn("Could not load summary endpoint, falling back to profile:", apiErr);
            const profile = await getMemberProfile(memberId);
            summary = {
                member: profile,
                bmi: profile.height && profile.weight ? Number((profile.weight / ((profile.height / 100) ** 2)).toFixed(1)) : null,
                membership: { status: "INACTIVE", planName: "No Active Plan", price: 0 },
                attendance: { totalVisits: 0, visitsThisMonth: 0, currentStreak: 0, checkedInToday: false },
                workouts: { totalSessions: 0, latestSession: null },
                progress: { totalLogs: 0, weightChange: null, recentWeights: [] }
            };
        }

        const member = summary.member || {};
        const membership = summary.membership || {};
        const attendance = summary.attendance || {};
        const workouts = summary.workouts || {};
        const progress = summary.progress || {};

        // 1. Header & Greeting
        const name = member.name || "Member";
        setText("userName", name);
        setText("userAvatar", name.charAt(0).toUpperCase());
        setText("welcomeGreeting", `Welcome back,\n${name}.`);

        // Hero metrics
        setText("memberHeight", member.height ? `Height: ${member.height} cm` : "Height: --");
        setText("memberWeightHero", member.weight ? `Weight: ${member.weight} kg` : "Weight: --");
        setText("fitnessGoal", member.fitnessGoal ? `Goal: ${member.fitnessGoal}` : "Goal: General Fitness");

        // 2. Stat 1: MEMBERSHIP
        const isMemberActive = membership.status === "ACTIVE";
        const isMemberExpired = membership.status === "EXPIRED";
        const dashMembershipPlan = document.getElementById("dashMembershipPlan");
        if (dashMembershipPlan) {
            if (isMemberActive) {
                dashMembershipPlan.textContent = (membership.plan || "ACTIVE").toUpperCase();
                dashMembershipPlan.style.color = "var(--accent, #c4ff65)";
            } else if (isMemberExpired) {
                dashMembershipPlan.textContent = "EXPIRED";
                dashMembershipPlan.style.color = "var(--danger, #FF8296)";
            } else {
                dashMembershipPlan.textContent = "NO PLAN";
                dashMembershipPlan.style.color = "var(--muted, #a0aaa9)";
            }
        }
        setText("dashMembershipMeta", isMemberActive
            ? `${membership.daysRemaining} days remaining`
            : (isMemberExpired ? "Plan expired" : "Tap to select a plan"));

        // 3. Stat 2: ATTENDANCE
        setText("dashAttendanceVisits", attendance.visitsThisMonth ?? 0);
        setText("dashAttendanceMeta", `${attendance.currentStreak ?? 0}-day streak · ${attendance.totalVisits ?? 0} total`);

        // 4. Stat 3: CURRENT WEIGHT
        const currentWeight = member.weight ?? (progress.recentWeights?.[0]?.weight);
        setText("memberWeight", currentWeight ? `${Number(currentWeight).toFixed(1)} kg` : "--");
        const change = progress.weightChange;
        if (change != null) {
            setText("dashWeightMeta", `${change > 0 ? "+" : ""}${change.toFixed(1)} kg overall`);
        } else {
            setText("dashWeightMeta", "Logged in database");
        }

        // 5. Stat 4: BMI
        const bmi = summary.bmi;
        setText("memberBmi", bmi ? bmi.toFixed(1) : "--");
        setText("dashBmiCategory", getBmiCategory(bmi));

        // 6. Content Grid: WORKOUT CARD
        const latestWorkout = workouts.latestSession;
        const dashWorkoutTitle = document.getElementById("dashWorkoutTitle");
        const dashWorkoutBadge = document.getElementById("dashWorkoutBadge");
        const dashExerciseList = document.getElementById("dashExerciseList");
        const dashWorkoutAction = document.getElementById("dashWorkoutAction");

        if (latestWorkout && latestWorkout.exercises?.length) {
            if (dashWorkoutTitle) dashWorkoutTitle.textContent = `Completed ${formatDate(latestWorkout.date)}`;
            if (dashWorkoutBadge) {
                dashWorkoutBadge.textContent = `${latestWorkout.exercises.length} EXERCISES`;
                dashWorkoutBadge.style.background = "rgba(196, 255, 101, 0.15)";
                dashWorkoutBadge.style.color = "var(--accent, #c4ff65)";
            }
            if (dashExerciseList) {
                dashExerciseList.innerHTML = latestWorkout.exercises.slice(0, 4).map((ex, idx) => `
                    <div class="exercise-item">
                        <span>0${idx + 1}</span>
                        <strong>${ex}</strong>
                        <small>Completed session</small>
                    </div>
                `).join("");
            }
            if (dashWorkoutAction) dashWorkoutAction.innerHTML = `Start new session <span>→</span>`;
        } else {
            if (dashWorkoutTitle) dashWorkoutTitle.textContent = "Today's Routine";
            if (dashWorkoutBadge) {
                dashWorkoutBadge.textContent = "READY";
            }
            if (dashWorkoutAction) dashWorkoutAction.innerHTML = `Log your first workout <span>→</span>`;
        }

        // 7. Content Grid: MEMBERSHIP CARD
        setText("dashPlanName", membership.planName || "No Plan Selected");
        const dashPlanBadge = document.getElementById("dashPlanBadge");
        if (dashPlanBadge) {
            dashPlanBadge.textContent = membership.status || "INACTIVE";
            dashPlanBadge.className = isMemberActive ? "status-badge" : "status-badge";
            dashPlanBadge.style.color = isMemberActive ? "var(--accent, #c4ff65)" : "var(--muted, #a0aaa9)";
        }
        setText("dashValidUntil", isMemberActive ? formatDate(membership.endDate) : "Inactive");
        setText("dashRenewalPrice", membership.price ? `₹${membership.price}` : "Choose plan");

        // 8. Content Grid: PROGRESS CARD
        const dashWeightTrend = document.getElementById("dashWeightTrend");
        if (dashWeightTrend) {
            if (change != null) {
                dashWeightTrend.textContent = `${change <= 0 ? "↓" : "↑"} ${Math.abs(change).toFixed(1)} kg`;
                dashWeightTrend.style.color = change <= 0 ? "var(--accent, #c4ff65)" : "var(--accent-dark, #a8f042)";
            } else {
                dashWeightTrend.textContent = `${progress.totalLogs ?? 0} measurements`;
            }
        }
        const dashWeightList = document.getElementById("dashWeightList");
        if (dashWeightList && progress.recentWeights?.length) {
            dashWeightList.innerHTML = progress.recentWeights.slice(0, 3).map(w => `
                <div class="exercise-item">
                    <span>•</span>
                    <strong>${formatDate(w.date)}</strong>
                    <small style="color: var(--accent, #c4ff65); font-weight: 700;">${Number(w.weight).toFixed(1)} kg</small>
                </div>
            `).join("");
        } else if (dashWeightList) {
            dashWeightList.innerHTML = `<p class="fit-empty" style="text-align: left; padding: 10px 0;">No weight logs yet. Log your weight in the progress section.</p>`;
        }

        // 9. Content Grid: ATTENDANCE CARD
        const now = new Date();
        const currentMonthName = now.toLocaleString("default", { month: "long" });
        setText("dashAttendanceMonth", `${currentMonthName} Visits`);
        const daysInMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0).getDate();
        const daysPassed = now.getDate();
        const visits = attendance.visitsThisMonth || 0;
        const rate = Math.round((visits / Math.max(daysPassed, 1)) * 100);
        setText("dashAttendanceRate", `${rate}%`);

        const barSpan = document.getElementById("dashAttendanceBarSpan");
        if (barSpan) barSpan.style.width = `${Math.min(rate, 100)}%`;

        const desc = document.getElementById("dashAttendanceDescription");
        if (desc) {
            if (attendance.checkedInToday) {
                desc.textContent = `✓ You've checked in today! ${visits} sessions attended in ${currentMonthName}.`;
                desc.style.color = "var(--accent, #c4ff65)";
            } else {
                desc.textContent = `You've attended ${visits} of ${daysPassed} days so far this month.`;
                desc.style.color = "var(--muted, #a0aaa9)";
            }
        }

        // 10. Content Grid: ACTIVITY FEED
        const activityList = document.getElementById("dashActivityList");
        if (activityList) {
            const feedItems = [];

            if (attendance.checkedInToday) {
                feedItems.push({
                    title: "Check-in recorded",
                    desc: "You marked attendance for today.",
                    color: "var(--accent, #c4ff65)"
                });
            }

            if (latestWorkout) {
                feedItems.push({
                    title: `Workout saved (${formatDate(latestWorkout.date)})`,
                    desc: `${latestWorkout.exercises?.length || 0} exercises completed.`,
                    color: "var(--accent, #c4ff65)"
                });
            }

            if (isMemberActive) {
                feedItems.push({
                    title: `${membership.planName} active`,
                    desc: `Valid through ${formatDate(membership.endDate)}.`,
                    color: "var(--accent, #c4ff65)"
                });
            } else {
                feedItems.push({
                    title: "Membership pending",
                    desc: "Activate a tier in the Membership tab.",
                    color: "var(--danger, #FF8296)"
                });
            }

            activityList.innerHTML = feedItems.map(item => `
                <div class="notification-item">
                    <span class="notification-dot" style="background: ${item.color}; box-shadow: 0 0 10px ${item.color};"></span>
                    <div>
                        <strong style="color: var(--text, #f2f5f4);">${item.title}</strong>
                        <p style="color: var(--muted, #a0aaa9);">${item.desc}</p>
                    </div>
                </div>
            `).join("");
        }

    } catch (error) {
        console.error("Unable to load dashboard:", error);
    }
}

loadDashboard();
