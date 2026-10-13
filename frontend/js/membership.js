import { getMemberProfile } from "./api.js";

const memberId = localStorage.getItem("memberId");

const setText = (id, value) => {
    const element = document.getElementById(id);
    if (element) element.textContent = value;
};

if (!memberId) {
    window.location.href = "../login.html";
} else {
    loadMembership();
}

async function loadMembership() {
    try {
        const member = await getMemberProfile(memberId);
        const name = member.name || "Member";

        setText("userName", name);
        setText("userAvatar", name.charAt(0).toUpperCase());
        setText("planMemberName", name);
        setText("planMemberId", member.id ?? memberId);
        setText("overviewName", name);
        setText("overviewEmail", member.email || "Not provided");
        setText("overviewPhone", member.phone || "Not provided");
    } catch (error) {
        console.error("Unable to load membership page:", error);
        setText("membershipMessage", "We couldn't load your member details. Please sign in again and retry.");
        setText("planMemberName", "Unavailable");
        setText("overviewName", "Unavailable");
    }
}

document.getElementById("signOutLink")?.addEventListener("click", () => {
    localStorage.removeItem("memberId");
});
