import { checkIn, getAttendance, getMemberProfile } from "./api.js";

const memberId = localStorage.getItem("memberId");
if (!memberId) location.replace("../login.html");

const dateKey = date => [
    date.getFullYear(),
    String(date.getMonth() + 1).padStart(2, "0"),
    String(date.getDate()).padStart(2, "0")
].join("-");
const todayKey = dateKey(new Date());
const pretty = value => new Date(`${value}T12:00:00`).toLocaleDateString(
    undefined, { year: "numeric", month: "short", day: "numeric" }
);
const safe = value => String(value).replace(/[&<>"']/g, character => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
})[character]);

function render(records) {
    records.sort((a, b) => b.date.localeCompare(a.date));
    const checkedIn = records.some(record => record.date === todayKey);
    document.getElementById("todayDate").textContent = pretty(todayKey);
    document.getElementById("attendanceStatus").textContent = checkedIn
        ? "You're checked in for today. Great work."
        : "You haven't checked in today.";
    const button = document.getElementById("checkInButton");
    button.disabled = checkedIn;
    button.textContent = checkedIn ? "✓ Checked in today" : "Mark attendance ↗";
    document.getElementById("attendanceTotal").textContent = records.length;
    document.getElementById("attendanceMonth").textContent = records.filter(
        record => record.date.startsWith(todayKey.slice(0, 7))
    ).length;
    const dates = new Set(records.map(record => record.date));
    let streak = 0;
    for (let day = new Date(); dates.has(dateKey(day)); day.setDate(day.getDate() - 1)) {
        streak += 1;
    }
    document.getElementById("attendanceStreak").textContent = streak;
    document.getElementById("attendanceHistory").innerHTML = records.length
        ? records.slice(0, 30).map(record => `
            <div class="fit-history-row">
              <span><strong>${safe(pretty(record.date))}</strong><small>Gym check-in recorded</small></span>
              <span class="fit-history-value">PRESENT</span>
            </div>`).join("")
        : '<p class="fit-empty">No attendance recorded yet. Check in when you visit the gym.</p>';
}

async function loadPage() {
    const message = document.getElementById("attendanceMessage");
    try {
        const [records, member] = await Promise.all([
            getAttendance(memberId),
            getMemberProfile(memberId)
        ]);
        document.getElementById("userName").textContent = member.name || "Member";
        document.getElementById("userAvatar").textContent =
            (member.name || "M").trim().charAt(0).toUpperCase();
        render(records);
    } catch (error) {
        message.textContent = error.message || "Unable to load attendance.";
    }
}

document.getElementById("checkInButton").addEventListener("click", async event => {
    const button = event.currentTarget;
    const message = document.getElementById("attendanceMessage");
    button.disabled = true;
    message.textContent = "";
    try {
        await checkIn(memberId, todayKey);
        render(await getAttendance(memberId));
        message.textContent = "Attendance saved to your account.";
    } catch (error) {
        button.disabled = false;
        message.textContent = error.message || "Unable to record attendance.";
    }
});

loadPage();
