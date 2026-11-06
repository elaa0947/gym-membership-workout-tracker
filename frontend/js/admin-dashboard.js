import {
    getAdminOverview,
    getAdminMembers,
    createAdminMember,
    updateAdminMember,
    deleteAdminMember,
    updateAdminMemberMembership,
    getAdminAttendance,
    markAdminAttendance,
    deleteAdminAttendance,
    getAdminWorkouts,
    logoutAdmin
} from "./api.js";

// Session check
const adminRole = localStorage.getItem("adminRole");
if (adminRole !== "ADMIN") {
    window.location.replace("./login.html");
}

document.getElementById("adminNameDisplay").textContent =
    localStorage.getItem("adminName") || "Administrator";

// State
let allMembers = [];
let allAttendance = [];
let allWorkouts = [];

// Helper functions
const escapeHTML = value => String(value ?? "").replace(/[&<>"']/g, c => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
})[c]);

const formatDate = dateStr => {
    if (!dateStr) return "—";
    try {
        const [y, m, d] = dateStr.split("-");
        return new Date(y, Number(m) - 1, d).toLocaleDateString(undefined, {
            year: "numeric", month: "short", day: "numeric"
        });
    } catch {
        return dateStr;
    }
};

function notify(text, isError = false) {
    const el = document.getElementById("adminNotification");
    el.style.display = "block";
    el.textContent = text;
    el.style.background = isError ? "rgba(255, 130, 150, 0.15)" : "rgba(196, 255, 101, 0.15)";
    el.style.color = isError ? "#FF8296" : "#C4FF65";
    el.style.border = isError ? "1px solid rgba(255, 130, 150, 0.3)" : "1px solid rgba(196, 255, 101, 0.3)";
    setTimeout(() => {
        el.style.display = "none";
    }, 4000);
}

// Global modal helpers
window.closeModal = id => {
    document.getElementById(id).classList.remove("open");
};
window.openModal = id => {
    document.getElementById(id).classList.add("open");
};

// Sign out
document.getElementById("adminSignOutBtn").addEventListener("click", async () => {
    try {
        await logoutAdmin();
    } catch (e) {
        console.warn("Logout error:", e);
    } finally {
        localStorage.removeItem("adminId");
        localStorage.removeItem("adminRole");
        localStorage.removeItem("adminName");
        localStorage.removeItem("adminEmail");
        window.location.assign("./login.html");
    }
});

// Tab Navigation
document.querySelectorAll(".admin-tab").forEach(tab => {
    tab.addEventListener("click", () => {
        document.querySelectorAll(".admin-tab").forEach(t => t.classList.remove("active"));
        document.querySelectorAll(".tab-pane").forEach(p => p.classList.remove("active"));
        tab.classList.add("active");
        const target = tab.dataset.tab;
        document.getElementById(target).classList.add("active");
    });
});

// 1. Overview Loader
async function refreshOverview() {
    try {
        const stats = await getAdminOverview();

        document.getElementById("statTotalMembers").textContent = stats.totalMembers ?? 0;
        document.getElementById("statActiveMembers").textContent = stats.activeMemberships ?? 0;
        document.getElementById("statRevenue").textContent = `₹${Number(stats.totalRevenue ?? 0).toLocaleString()}`;
        document.getElementById("statTodayAttendance").textContent = stats.todayAttendance ?? 0;
        document.getElementById("statTotalWorkouts").textContent = stats.totalWorkouts ?? 0;

        const total = stats.totalMembers || 1;
        const activePct = Math.round(((stats.activeMemberships || 0) / total) * 100);
        document.getElementById("statActiveRatio").textContent = `${activePct}% active member conversion`;
        document.getElementById("statExpiredMeta").textContent = `${stats.expiredMemberships ?? 0} expired memberships`;
        document.getElementById("statTotalVisits").textContent = `${stats.totalAttendance ?? 0} total check-in records`;

        // Feeds
        const membersFeed = document.getElementById("recentMembersFeed");
        if (stats.recentMembers?.length) {
            membersFeed.innerHTML = stats.recentMembers.map(m => `
                <div class="live-feed-item">
                    <div>
                        <strong style="color: #F2F5F4;">${escapeHTML(m.name)}</strong>
                        <div style="font-size: 0.78rem; color: var(--muted);">${escapeHTML(m.email)}</div>
                    </div>
                    <span class="badge ${m.membershipStatus === 'ACTIVE' ? 'badge-active' : 'badge-none'}">
                        ${m.membershipPlan || 'NO PLAN'}
                    </span>
                </div>
            `).join("");
        } else {
            membersFeed.innerHTML = `<p style="color: var(--muted); font-size: 0.85rem;">No members registered yet.</p>`;
        }

        const attendanceFeed = document.getElementById("recentAttendanceFeed");
        if (stats.recentAttendance?.length) {
            attendanceFeed.innerHTML = stats.recentAttendance.map(a => `
                <div class="live-feed-item">
                    <div>
                        <strong style="color: #F2F5F4;">${escapeHTML(a.memberName || 'Member #' + a.memberId)}</strong>
                        <div style="font-size: 0.78rem; color: var(--muted);">${escapeHTML(a.memberEmail || '')}</div>
                    </div>
                    <span style="font-size: 0.8rem; font-weight: 700; color: #C4FF65;">
                        ${formatDate(a.date)}
                    </span>
                </div>
            `).join("");
        } else {
            attendanceFeed.innerHTML = `<p style="color: var(--muted); font-size: 0.85rem;">No check-ins logged yet.</p>`;
        }
    } catch (err) {
        console.error("Failed to load overview:", err);
        if (err.message && err.message.includes("401")) {
            window.location.replace("./login.html");
        }
    }
}

// 2. Members Directory Loader
async function refreshMembers() {
    try {
        allMembers = await getAdminMembers();
        document.getElementById("tabMemberCount").textContent = allMembers.length;
        renderMembersTable();
        populateAttendanceMemberSelect();
    } catch (err) {
        console.error("Failed to load members:", err);
    }
}

function renderMembersTable() {
    const query = document.getElementById("memberSearchInput").value.trim().toLowerCase();
    const planFilter = document.getElementById("memberFilterPlan").value;

    const filtered = allMembers.filter(m => {
        const matchesQuery = !query ||
            (m.name && m.name.toLowerCase().includes(query)) ||
            (m.email && m.email.toLowerCase().includes(query)) ||
            (m.phone && m.phone.toLowerCase().includes(query));

        const matchesPlan = planFilter === "ALL" || (m.membershipStatus === planFilter);
        return matchesQuery && matchesPlan;
    });

    const tbody = document.getElementById("membersTableBody");
    if (!filtered.length) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: var(--muted); padding: 2rem;">No matching members found.</td></tr>`;
        return;
    }

    tbody.innerHTML = filtered.map(m => {
        const isActive = m.membershipStatus === "ACTIVE";
        const isExpired = m.membershipStatus === "EXPIRED";
        const badgeClass = isActive ? "badge-active" : (isExpired ? "badge-expired" : "badge-none");
        const statusLabel = m.membershipStatus || "NONE";

        return `
            <tr>
                <td>
                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                        <span style="width: 32px; height: 32px; border-radius: 50%; background: ${m.role === 'ADMIN' ? 'rgba(196,255,101,0.2)' : 'rgba(255,255,255,0.06)'}; color: ${m.role === 'ADMIN' ? '#c4ff65' : '#F2F5F4'}; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 0.85rem;">
                            ${escapeHTML((m.name || "M").charAt(0).toUpperCase())}
                        </span>
                        <div>
                            <strong>${escapeHTML(m.name)}</strong>
                            <div style="font-size: 0.75rem; color: var(--muted);">#MEM-${String(m.id).padStart(4, "0")} ${m.role === 'ADMIN' ? '<span style="color:#c4ff65;">[ADMIN]</span>' : ''}</div>
                        </div>
                    </div>
                </td>
                <td>
                    <div>${escapeHTML(m.email)}</div>
                    <div style="font-size: 0.78rem; color: var(--muted);">${escapeHTML(m.phone || "—")}</div>
                </td>
                <td>
                    <span style="font-size: 0.82rem; color: #F2F5F4;">${escapeHTML(m.fitnessGoal || "General Fitness")}</span>
                </td>
                <td>
                    <strong>${m.membershipPlan || "No Plan"}</strong>
                    <div style="font-size: 0.75rem; color: var(--muted);">${m.membershipEndDate ? 'Ends: ' + formatDate(m.membershipEndDate) : '—'}</div>
                </td>
                <td>
                    <span class="badge ${badgeClass}">${statusLabel}</span>
                </td>
                <td>
                    <div style="font-size: 0.78rem; color: var(--muted);">
                        Check-ins: <strong style="color: #F2F5F4;">${m.attendanceCount ?? 0}</strong><br>
                        Workouts: <strong style="color: #F2F5F4;">${m.workoutCount ?? 0}</strong>
                    </div>
                </td>
                <td style="text-align: right;">
                    <div style="display: inline-flex; gap: 0.4rem;">
                        <button class="admin-btn admin-btn-secondary" style="font-size: 0.75rem; padding: 0.35rem 0.65rem;" onclick="openEditMember(${m.id})">
                            Edit
                        </button>
                        <button class="admin-btn admin-btn-secondary" style="font-size: 0.75rem; padding: 0.35rem 0.65rem;" onclick="openManagePlan(${m.id})">
                            Plan ⚡
                        </button>
                        ${m.role !== 'ADMIN' ? `
                            <button class="admin-btn admin-btn-danger" style="font-size: 0.75rem; padding: 0.35rem 0.65rem;" onclick="confirmDeleteMember(${m.id}, '${escapeHTML(m.name)}')">
                                ×
                            </button>
                        ` : ''}
                    </div>
                </td>
            </tr>
        `;
    }).join("");
}

// 3. Attendance Records Loader
async function refreshAttendance() {
    try {
        allAttendance = await getAdminAttendance();
        document.getElementById("tabAttendanceCount").textContent = allAttendance.length;
        renderAttendanceTable();
    } catch (err) {
        console.error("Failed to load attendance:", err);
    }
}

function renderAttendanceTable() {
    const query = document.getElementById("attendanceSearchInput").value.trim().toLowerCase();

    const filtered = allAttendance.filter(a => {
        return !query ||
            (a.memberName && a.memberName.toLowerCase().includes(query)) ||
            (a.memberEmail && a.memberEmail.toLowerCase().includes(query)) ||
            (a.date && a.date.includes(query));
    });

    const tbody = document.getElementById("attendanceTableBody");
    if (!filtered.length) {
        tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--muted); padding: 2rem;">No attendance logs found.</td></tr>`;
        return;
    }

    tbody.innerHTML = filtered.map(a => `
        <tr>
            <td><code style="color: var(--muted);">#LOG-${String(a.id).padStart(4, "0")}</code></td>
            <td><strong>${formatDate(a.date)}</strong></td>
            <td>${escapeHTML(a.memberName || 'Member #' + a.memberId)}</td>
            <td><span style="color: var(--muted);">${escapeHTML(a.memberEmail || '—')}</span></td>
            <td><span class="badge badge-active">PRESENT</span></td>
            <td style="text-align: right;">
                <button class="admin-btn admin-btn-danger" style="font-size: 0.75rem; padding: 0.35rem 0.65rem;" onclick="removeAttendance(${a.id})">
                    Remove
                </button>
            </td>
        </tr>
    `).join("");
}

// 4. Workout Feed Loader
async function refreshWorkouts() {
    try {
        allWorkouts = await getAdminWorkouts();
        document.getElementById("tabWorkoutCount").textContent = allWorkouts.length;
        renderWorkoutsTable();
    } catch (err) {
        console.error("Failed to load workouts:", err);
    }
}

function renderWorkoutsTable() {
    const query = document.getElementById("workoutSearchInput").value.trim().toLowerCase();

    const filtered = allWorkouts.filter(w => {
        const matchesQuery = !query ||
            (w.memberName && w.memberName.toLowerCase().includes(query)) ||
            (w.memberEmail && w.memberEmail.toLowerCase().includes(query)) ||
            (w.exercises && w.exercises.some(e => e.toLowerCase().includes(query)));
        return matchesQuery;
    });

    const tbody = document.getElementById("workoutsTableBody");
    if (!filtered.length) {
        tbody.innerHTML = `<tr><td colspan="4" style="text-align: center; color: var(--muted); padding: 2rem;">No workout activity recorded yet.</td></tr>`;
        return;
    }

    tbody.innerHTML = filtered.map(w => `
        <tr>
            <td><strong>${formatDate(w.date)}</strong></td>
            <td>
                <div><strong>${escapeHTML(w.memberName || 'Member #' + w.memberId)}</strong></div>
                <div style="font-size: 0.75rem; color: var(--muted);">${escapeHTML(w.memberEmail || '')}</div>
            </td>
            <td>
                <div style="display: flex; flex-wrap: wrap; gap: 0.35rem;">
                    ${(w.exercises || []).map(ex => `
                        <span class="badge badge-plan">${escapeHTML(ex)}</span>
                    `).join("")}
                </div>
            </td>
            <td><span style="font-size: 0.78rem; color: var(--muted);">${w.savedAt ? String(w.savedAt).replace("T", " ").slice(0, 19) : "—"}</span></td>
        </tr>
    `).join("");
}

// Search listeners
document.getElementById("memberSearchInput").addEventListener("input", renderMembersTable);
document.getElementById("memberFilterPlan").addEventListener("change", renderMembersTable);
document.getElementById("attendanceSearchInput").addEventListener("input", renderAttendanceTable);
document.getElementById("workoutSearchInput").addEventListener("input", renderWorkoutsTable);

// Add Member Modal
document.getElementById("openAddMemberBtn").addEventListener("click", () => {
    document.getElementById("formAddMember").reset();
    window.openModal("modalAddMember");
});

document.getElementById("formAddMember").addEventListener("submit", async event => {
    event.preventDefault();
    const name = document.getElementById("newMemberName").value.trim();
    const email = document.getElementById("newMemberEmail").value.trim();
    const phone = document.getElementById("newMemberPhone").value.trim();
    const password = document.getElementById("newMemberPassword").value;
    const fitnessGoal = document.getElementById("newMemberGoal").value;

    try {
        await createAdminMember({
            name, email, phone, password, fitnessGoal, onboardingCompleted: true
        });
        window.closeModal("modalAddMember");
        notify(`✓ Successfully registered member: ${name}`);
        await Promise.all([refreshOverview(), refreshMembers()]);
    } catch (err) {
        alert(err.message || "Failed to create member.");
    }
});

// Edit Member Modal
window.openEditMember = id => {
    const member = allMembers.find(m => m.id === id);
    if (!member) return;
    document.getElementById("editMemberId").value = member.id;
    document.getElementById("editMemberName").value = member.name || "";
    document.getElementById("editMemberPhone").value = member.phone || "";
    document.getElementById("editMemberGoal").value = member.fitnessGoal || "";
    document.getElementById("editMemberRole").value = member.role || "MEMBER";
    window.openModal("modalEditMember");
};

document.getElementById("formEditMember").addEventListener("submit", async event => {
    event.preventDefault();
    const id = document.getElementById("editMemberId").value;
    const name = document.getElementById("editMemberName").value.trim();
    const phone = document.getElementById("editMemberPhone").value.trim();
    const fitnessGoal = document.getElementById("editMemberGoal").value.trim();
    const role = document.getElementById("editMemberRole").value;

    try {
        await updateAdminMember(id, { name, phone, fitnessGoal, role });
        window.closeModal("modalEditMember");
        notify(`✓ Updated member details.`);
        await Promise.all([refreshOverview(), refreshMembers()]);
    } catch (err) {
        alert(err.message || "Failed to update member.");
    }
});

// Manage Plan Modal
window.openManagePlan = id => {
    const member = allMembers.find(m => m.id === id);
    if (!member) return;
    document.getElementById("planMemberId").value = member.id;
    document.getElementById("planMemberNameBanner").textContent = `Managing plan for: ${member.name} (${member.email})`;
    if (member.membershipPlan) {
        document.getElementById("planTierSelect").value = member.membershipPlan;
    }
    document.getElementById("planStatusSelect").value = member.membershipStatus || "ACTIVE";
    window.openModal("modalManagePlan");
};

document.getElementById("formManagePlan").addEventListener("submit", async event => {
    event.preventDefault();
    const id = document.getElementById("planMemberId").value;
    const plan = document.getElementById("planTierSelect").value;
    const status = document.getElementById("planStatusSelect").value;

    const planConfig = {
        STARTER: { months: 3, price: 2400 },
        PRO: { months: 6, price: 4200 },
        ELITE: { months: 12, price: 7200 }
    }[plan] || { months: 1, price: 999 };

    try {
        await updateAdminMemberMembership(id, {
            plan,
            durationMonths: planConfig.months,
            price: planConfig.price,
            status
        });
        window.closeModal("modalManagePlan");
        notify(`✓ Membership plan updated for member.`);
        await Promise.all([refreshOverview(), refreshMembers()]);
    } catch (err) {
        alert(err.message || "Failed to update plan.");
    }
});

// Delete Member
window.confirmDeleteMember = async (id, name) => {
    if (!confirm(`Are you sure you want to permanently delete member "${name}"? This removes all associated attendance, workouts, and weight logs.`)) {
        return;
    }

    try {
        await deleteAdminMember(id);
        notify(`✓ Member removed.`);
        await Promise.all([refreshOverview(), refreshMembers(), refreshAttendance(), refreshWorkouts()]);
    } catch (err) {
        alert(err.message || "Failed to delete member.");
    }
};

// Mark Attendance Modal
function populateAttendanceMemberSelect() {
    const select = document.getElementById("attendanceMemberSelect");
    select.innerHTML = `<option value="">-- Choose Member --</option>` +
        allMembers.map(m => `<option value="${m.id}">${escapeHTML(m.name)} (${escapeHTML(m.email)})</option>`).join("");
}

document.getElementById("openMarkAttendanceBtn").addEventListener("click", () => {
    populateAttendanceMemberSelect();
    document.getElementById("attendanceDateInput").value = new Date().toISOString().split("T")[0];
    window.openModal("modalMarkAttendance");
});

document.getElementById("formMarkAttendance").addEventListener("submit", async event => {
    event.preventDefault();
    const memberId = document.getElementById("attendanceMemberSelect").value;
    const date = document.getElementById("attendanceDateInput").value;

    if (!memberId || !date) {
        alert("Please select both a member and date.");
        return;
    }

    try {
        await markAdminAttendance({ memberId: Number(memberId), date });
        window.closeModal("modalMarkAttendance");
        notify(`✓ Recorded attendance for member.`);
        await Promise.all([refreshOverview(), refreshAttendance(), refreshMembers()]);
    } catch (err) {
        alert(err.message || "Failed to mark attendance.");
    }
});

// Remove Attendance
window.removeAttendance = async id => {
    if (!confirm("Are you sure you want to delete this check-in entry?")) return;
    try {
        await deleteAdminAttendance(id);
        notify("✓ Attendance record removed.");
        await Promise.all([refreshOverview(), refreshAttendance(), refreshMembers()]);
    } catch (err) {
        alert(err.message || "Failed to remove attendance.");
    }
};

// Initial Boot
async function init() {
    await Promise.all([
        refreshOverview(),
        refreshMembers(),
        refreshAttendance(),
        refreshWorkouts()
    ]);
}

init();
