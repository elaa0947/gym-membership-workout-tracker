const isDevHost = window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1";
const savedCustomBackend = localStorage.getItem("API_BACKEND_URL");
const API_BASE_URL = savedCustomBackend
    ? savedCustomBackend.replace(/\/+$/, "")
    : (isDevHost && window.location.port !== "8080" && window.location.port !== "80"
        ? `http://${window.location.hostname}:8080/api`
        : "/api");

async function request(path, options = {}) {
    let response;
    try {
        response = await fetch(`${API_BASE_URL}${path}`, {
            ...options,
            credentials: "include",
            headers: {
                "Bypass-Tunnel-Reminder": "true",
                ...(options.body ? { "Content-Type": "application/json" } : {}),
                ...options.headers
            }
        });
    } catch (networkError) {
        throw new Error("Unable to connect to backend service. Please check network or use http://localhost:5500.");
    }

    const body = await response.text();
    let data = null;
    if (body) {
        try {
            data = JSON.parse(body);
        } catch {
            if (body.includes("DNS_HOSTNAME_RESOLVED_PRIVATE") || body.includes("The page could not be found")) {
                data = {
                    message: "Backend unreachable from cloud preview: Vercel cannot reach local servers directly. Use http://localhost:5500 for local development or set a public backend URL."
                };
            } else {
                data = { message: body };
            }
        }
    }

    if (!response.ok) {
        throw new Error(data?.message || `Request failed (${response.status}).`);
    }
    return data;
}

document.getElementById("signOutLink")?.addEventListener("click", async event => {
    event.preventDefault();
    const link = event.currentTarget;
    try {
        await request("/members/logout", { method: "POST" });
    } catch (error) {
        console.error("Unable to invalidate the member session:", error);
    } finally {
        localStorage.removeItem("memberId");
        window.location.assign(link.href);
    }
});

export function registerMember(memberData) {
    return request("/members/register", {
        method: "POST",
        body: JSON.stringify(memberData)
    });
}

export function loginMember(loginData) {
    return request("/members/login", {
        method: "POST",
        body: JSON.stringify(loginData)
    });
}

export function getMemberProfile(memberId) {
    return request(`/members/profile/${encodeURIComponent(memberId)}`);
}

export function updateMemberProfile(memberId, profileData) {
    return request(`/members/profile/${encodeURIComponent(memberId)}`, {
        method: "PUT",
        body: JSON.stringify(profileData)
    });
}

export function completeOnboarding(memberId, onboardingData) {
    return request(`/members/onboarding/${encodeURIComponent(memberId)}`, {
        method: "PUT",
        body: JSON.stringify(onboardingData)
    });
}

export function getAttendance(memberId) {
    return request(`/members/${encodeURIComponent(memberId)}/attendance`);
}

export function checkIn(memberId, date) {
    return request(`/members/${encodeURIComponent(memberId)}/attendance`, {
        method: "POST",
        body: JSON.stringify({ date })
    });
}

export function getWorkouts(memberId) {
    return request(`/members/${encodeURIComponent(memberId)}/workouts`);
}

export function saveWorkout(memberId, workout) {
    return request(`/members/${encodeURIComponent(memberId)}/workouts`, {
        method: "POST",
        body: JSON.stringify(workout)
    });
}

export function getWeightEntries(memberId) {
    return request(`/members/${encodeURIComponent(memberId)}/weights`);
}

export function saveWeightEntry(memberId, entry) {
    return request(`/members/${encodeURIComponent(memberId)}/weights`, {
        method: "POST",
        body: JSON.stringify(entry)
    });
}

export function getMembership(memberId) {
    return request(`/members/${encodeURIComponent(memberId)}/membership`);
}

export function updateMembership(memberId, membershipData) {
    return request(`/members/${encodeURIComponent(memberId)}/membership`, {
        method: "POST",
        body: JSON.stringify(membershipData)
    });
}

export function getDashboardSummary(memberId) {
    return request(`/members/${encodeURIComponent(memberId)}/dashboard-summary`);
}

// ==========================================
// ADMIN API PERSPECTIVE
// ==========================================

export function loginAdmin(credentials) {
    return request("/admin/login", {
        method: "POST",
        body: JSON.stringify(credentials)
    });
}

export function logoutAdmin() {
    return request("/admin/logout", {
        method: "POST"
    });
}

export function getAdminOverview() {
    return request("/admin/overview");
}

export function getAdminMembers() {
    return request("/admin/members");
}

export function createAdminMember(memberData) {
    return request("/admin/members", {
        method: "POST",
        body: JSON.stringify(memberData)
    });
}

export function updateAdminMember(id, updateData) {
    return request(`/admin/members/${encodeURIComponent(id)}`, {
        method: "PUT",
        body: JSON.stringify(updateData)
    });
}

export function deleteAdminMember(id) {
    return request(`/admin/members/${encodeURIComponent(id)}`, {
        method: "DELETE"
    });
}

export function updateAdminMemberMembership(id, membershipData) {
    return request(`/admin/members/${encodeURIComponent(id)}/membership`, {
        method: "PUT",
        body: JSON.stringify(membershipData)
    });
}

export function getAdminAttendance() {
    return request("/admin/attendance");
}

export function markAdminAttendance(attendanceData) {
    return request("/admin/attendance", {
        method: "POST",
        body: JSON.stringify(attendanceData)
    });
}

export function deleteAdminAttendance(id) {
    return request(`/admin/attendance/${encodeURIComponent(id)}`, {
        method: "DELETE"
    });
}

export function getAdminWorkouts() {
    return request("/admin/workouts");
}
