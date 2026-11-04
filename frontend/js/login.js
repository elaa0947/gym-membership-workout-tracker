import { loginMember, getMemberProfile } from "./api.js";

const loginForm = document.getElementById("loginForm");
const formMessage = document.getElementById("formMessage");

function showMessage(message, type) {
    formMessage.textContent = message;
    formMessage.className = `form-message ${type}`;
}

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const email =
        document.getElementById("email").value.trim();

    const password =
        document.getElementById("password").value;

    try {
        const member = await loginMember({
            email,
            password
        });

        // Save logged-in member ID
        localStorage.setItem(
            "memberId",
            member.id
        );

        showMessage(
            "Welcome back!",
            "success"
        );

        // Get latest member profile
        const profile =
            await getMemberProfile(member.id);

        // Redirect based on onboarding status
        setTimeout(() => {

            if (profile.onboardingCompleted === true) {

                window.location.href =
                    "./member/dashboard.html";

            } else {

                window.location.href =
                    "./member/onboarding.html";
            }

        }, 500);

    } catch (error) {

        showMessage(
            error.message ||
            "Invalid email or password.",
            "error"
        );
    }
});