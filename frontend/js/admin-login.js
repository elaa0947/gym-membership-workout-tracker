import { loginAdmin } from "./api.js";

const form = document.getElementById("adminLoginForm");
const formMessage = document.getElementById("formMessage");
const submitBtn = document.getElementById("submitBtn");

function showMessage(text, type) {
    formMessage.textContent = text;
    formMessage.className = `form-message ${type}`;
}

form.addEventListener("submit", async event => {
    event.preventDefault();

    const email = document.getElementById("adminEmail").value.trim();
    const password = document.getElementById("adminPassword").value;

    if (!email || !password) {
        showMessage("Please provide both email and password.", "error");
        return;
    }

    submitBtn.disabled = true;
    submitBtn.textContent = "Verifying Credentials...";

    try {
        const admin = await loginAdmin({ email, password });

        localStorage.setItem("adminId", admin.id);
        localStorage.setItem("adminRole", "ADMIN");
        localStorage.setItem("adminName", admin.name || "Administrator");
        localStorage.setItem("adminEmail", admin.email);

        showMessage("Access granted! Redirecting to Management Console...", "success");

        setTimeout(() => {
            window.location.href = "./dashboard.html";
        }, 500);
    } catch (error) {
        showMessage(error.message || "Failed to authenticate administrator.", "error");
        submitBtn.disabled = false;
        submitBtn.innerHTML = `Authenticate Console <span>→</span>`;
    }
});
