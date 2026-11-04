import {
    getAttendance,
    getMemberProfile,
    getWeightEntries,
    getWorkouts,
    saveWeightEntry
} from "./api.js";

const memberId = localStorage.getItem("memberId");
if (!memberId) location.replace("../login.html");

const dateToday = () => {
    const date = new Date();
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
};
const safe = value => String(value).replace(/[&<>"']/g, character => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
})[character]);
const dateInput = document.getElementById("weightDate");
dateInput.value = dateToday();

function render(weights, workouts, attendance, profile) {
    const sortedWeights = [...weights].sort((a, b) => a.date.localeCompare(b.date));
    document.getElementById("progressWorkouts").textContent = workouts.length;
    document.getElementById("progressAttendance").textContent = attendance.length;

    const height = Number(profile.height);
    const currentWeight = Number(profile.weight);
    document.getElementById("profileHeight").textContent =
        height > 0 ? `${height} cm` : "—";
    document.getElementById("profileWeight").textContent =
        currentWeight > 0 ? `${currentWeight} kg` : "—";
    if (height > 0 && currentWeight > 0) {
        document.getElementById("bmiValue").textContent =
            (currentWeight / ((height / 100) ** 2)).toFixed(1);
        document.getElementById("bmiNote").textContent =
            "BMI is a screening measure, not a diagnosis.";
    } else {
        document.getElementById("bmiNote").textContent =
            "Add height and weight to your member profile to calculate BMI.";
    }

    if (sortedWeights.length >= 2) {
        const change = sortedWeights.at(-1).weight - sortedWeights[0].weight;
        document.getElementById("weightChange").textContent =
            `${change > 0 ? "+" : ""}${change.toFixed(1)} kg`;
    } else {
        document.getElementById("weightChange").textContent = "Need 2 logs";
    }
    document.getElementById("weightHistory").innerHTML = sortedWeights.length
        ? [...sortedWeights].reverse().map(entry => `
            <div class="fit-history-row">
              <span><strong>${safe(entry.date)}</strong><small>Weight measurement</small></span>
              <span class="fit-history-value">${Number(entry.weight).toFixed(1)} kg</span>
            </div>`).join("")
        : '<p class="fit-empty">No measurements yet. Save your first weight entry above.</p>';
}

async function loadPage() {
    try {
        const [weights, workouts, attendance, profile] = await Promise.all([
            getWeightEntries(memberId),
            getWorkouts(memberId),
            getAttendance(memberId),
            getMemberProfile(memberId)
        ]);
        document.getElementById("userName").textContent = profile.name || "Member";
        document.getElementById("userAvatar").textContent =
            (profile.name || "M").trim().charAt(0).toUpperCase();
        render(weights, workouts, attendance, profile);
    } catch (error) {
        document.getElementById("bmiNote").textContent =
            error.message || "Unable to load progress from the server.";
    }
}

document.getElementById("weightForm").addEventListener("submit", async event => {
    event.preventDefault();
    const weight = Number(document.getElementById("weightInput").value);
    const date = dateInput.value;
    const message = document.getElementById("progressMessage");
    if (!Number.isFinite(weight) || weight < 20 || weight > 350 || !date) {
        message.textContent = "Enter a valid weight and date.";
        return;
    }

    const button = event.currentTarget.querySelector("button[type='submit']");
    button.disabled = true;
    try {
        await saveWeightEntry(memberId, { date, weight });
        document.getElementById("weightInput").value = "";
        message.textContent = "Measurement saved to your account.";
        await loadPage();
    } catch (error) {
        message.textContent = error.message || "Unable to save measurement.";
    } finally {
        button.disabled = false;
    }
});

loadPage();
