import { getMemberProfile, getWorkouts, saveWorkout } from "./api.js";

const memberId = localStorage.getItem("memberId");
if (!memberId) location.replace("../login.html");

const exercises = [
    { name: "Bodyweight squats", detail: "3 sets · 12 reps", tag: "LEGS" },
    { name: "Push-ups", detail: "3 sets · 10 reps", tag: "CHEST" },
    { name: "Dumbbell rows", detail: "3 sets · 10 reps each side", tag: "BACK" },
    { name: "Plank", detail: "3 sets · 30 seconds", tag: "CORE" },
    { name: "Brisk treadmill walk", detail: "10 minutes", tag: "CARDIO" }
];
const escapeHTML = value => String(value).replace(/[&<>"']/g, character => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
})[character]);
const localDate = () => {
    const date = new Date();
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
};
const list = document.getElementById("exerciseList");
let sessions = [];
exercises.forEach((exercise, index) => {
    const row = document.createElement("label");
    row.className = "fit-exercise";
    row.innerHTML = `<input type="checkbox" data-exercise="${index}"><span><strong>${escapeHTML(exercise.name)}</strong><small>${escapeHTML(exercise.detail)}</small></span><span class="fit-tag">${exercise.tag}</span>`;
    list.appendChild(row);
});

function render(sessions) {
    const done = [...list.querySelectorAll("input:checked")].length;
    document.getElementById("workoutCount").textContent =
        `${done} of ${exercises.length} exercises completed`;
    document.getElementById("exerciseDone").textContent = done;
    document.getElementById("sessionTotal").textContent = sessions.length;
    document.getElementById("exerciseBar").style.width = `${done / exercises.length * 100}%`;
    document.getElementById("workoutHistory").innerHTML = sessions.length
        ? sessions.slice(0, 8).map(session => `
            <div class="fit-history-row">
              <span><strong>${escapeHTML(session.date)}</strong><small>${session.exercises.length} exercises completed</small></span>
              <span class="fit-history-value">${session.exercises.length}/${exercises.length}</span>
            </div>`).join("")
        : '<p class="fit-empty">No workouts saved yet. Complete a session to start your history.</p>';
}

async function loadPage() {
    try {
        const [loadedSessions, member] = await Promise.all([
            getWorkouts(memberId),
            getMemberProfile(memberId)
        ]);
        sessions = loadedSessions;
        document.getElementById("userName").textContent = member.name || "Member";
        document.getElementById("userAvatar").textContent =
            (member.name || "M").trim().charAt(0).toUpperCase();
        render(sessions);
    } catch (error) {
        document.getElementById("workoutMessage").textContent =
            error.message || "Unable to load workouts.";
    }
}

list.addEventListener("change", () => {
    render(sessions);
});

document.getElementById("saveWorkout").addEventListener("click", async event => {
    const selected = [...list.querySelectorAll("input:checked")]
        .map(input => exercises[Number(input.dataset.exercise)].name);
    const message = document.getElementById("workoutMessage");
    if (!selected.length) {
        message.textContent = "Complete at least one exercise before saving.";
        return;
    }

    const button = event.currentTarget;
    button.disabled = true;
    message.textContent = "";
    try {
        await saveWorkout(memberId, { date: localDate(), exercises: selected });
        list.querySelectorAll("input").forEach(input => { input.checked = false; });
        sessions = await getWorkouts(memberId);
        render(sessions);
        message.textContent = "Workout saved to your account.";
    } catch (error) {
        message.textContent = error.message || "Unable to save this workout.";
    } finally {
        button.disabled = false;
    }
});

loadPage();
