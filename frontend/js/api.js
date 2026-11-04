const isDevHost = window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1";
const API_BASE_URL = isDevHost && window.location.port !== "8080" && window.location.port !== "80"
    ? `http://${window.location.hostname}:8080/api`
    : "/api";

async function request(path, options = {}) {
    const response = await fetch(`${API_BASE_URL}${path}`, {
        ...options,
        credentials: "include",
        headers: {
            ...(options.body ? { "Content-Type": "application/json" } : {}),
            ...options.headers
        }
    });

    const body = await response.text();
    let data = null;
    if (body) {
        try {
            data = JSON.parse(body);
        } catch {
            data = { message: body };
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
