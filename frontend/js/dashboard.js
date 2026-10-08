import { getMemberProfile } from "./api.js";

const memberId = localStorage.getItem("memberId");

if (!memberId) {
    window.location.href = "../login.html";
}

async function loadDashboard() {
    try {
        const member = await getMemberProfile(memberId);

        // ==============================
        // MEMBER NAME
        // ==============================

        const userName =
            document.getElementById("userName");

        if (userName) {
            userName.textContent =
                member.name || "Member";
        }

        // ==============================
        // MEMBER AVATAR
        // ==============================

        const userAvatar =
            document.getElementById("userAvatar");

        if (userAvatar && member.name) {
            userAvatar.textContent =
                member.name.charAt(0).toUpperCase();
        }

        // ==============================
        // HEIGHT
        // ==============================

        const memberHeight =
            document.getElementById("memberHeight");

        if (memberHeight) {
            memberHeight.textContent =
                member.height
                    ? `Height: ${member.height} cm`
                    : "Height: --";
        }

        // ==============================
        // WEIGHT
        // ==============================

        const memberWeight =
            document.getElementById("memberWeight");

        if (memberWeight) {
            memberWeight.textContent =
                member.weight
                    ? `${member.weight} kg`
                    : "--";
        }

        // ==============================
        // FITNESS GOAL
        // ==============================

        const fitnessGoal =
            document.getElementById("fitnessGoal");

        if (fitnessGoal) {
            fitnessGoal.textContent =
                member.fitnessGoal
                    ? `Goal: ${member.fitnessGoal}`
                    : "Goal: --";
        }

        // ==============================
        // BMI
        // ==============================

        const memberBmi =
            document.getElementById("memberBmi");

        if (
            memberBmi &&
            member.height > 0 &&
            member.weight > 0
        ) {
            const heightInMeters =
                member.height / 100;

            const bmi =
                member.weight /
                (heightInMeters * heightInMeters);

            memberBmi.textContent =
                bmi.toFixed(1);
        }

    } catch (error) {

        console.error(
            "Unable to load dashboard:",
            error
        );
    }
}

loadDashboard();