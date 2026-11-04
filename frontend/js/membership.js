import { getMemberProfile, getMembership, updateMembership } from "./api.js";

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

function formatDate(dateStr) {
    if (!dateStr) return "Not available";
    try {
        const [year, month, day] = dateStr.split("-");
        const date = new Date(year, Number(month) - 1, day);
        return date.toLocaleDateString(undefined, {
            year: "numeric",
            month: "short",
            day: "numeric"
        });
    } catch {
        return dateStr;
    }
}

async function loadMembership() {
    try {
        const [member, membership] = await Promise.all([
            getMemberProfile(memberId),
            getMembership(memberId)
        ]);

        const name = member.name || "Member";
        setText("userName", name);
        setText("userAvatar", name.charAt(0).toUpperCase());
        setText("planMemberName", name);
        setText("planMemberId", `#MEM-${String(member.id ?? memberId).padStart(4, "0")}`);
        setText("overviewName", name);
        setText("overviewEmail", member.email || "Not provided");
        setText("overviewPhone", member.phone || "Not provided");

        renderMembershipCard(membership);
    } catch (error) {
        console.error("Unable to load membership page:", error);
        setText("membershipMessage", "We couldn't load your member details. Please sign in again and retry.");
        setText("planMemberName", "Unavailable");
        setText("overviewName", "Unavailable");
    }
}

function renderMembershipCard(membership) {
    const statusBadge = document.getElementById("membershipStatus");
    const planNameEl = document.getElementById("planName");
    const planDescEl = document.getElementById("planDescription");
    const messageEl = document.getElementById("membershipMessage");

    const isActive = membership.status === "ACTIVE";
    const isExpired = membership.status === "EXPIRED";

    if (isActive) {
        statusBadge.textContent = "ACTIVE";
        statusBadge.className = "plan-status active";
        planNameEl.textContent = membership.planName || "Active Plan";
        planDescEl.textContent = `You are on the ${membership.planName || "Corely Plan"} with full access to gym features.`;
        setText("planStartDate", formatDate(membership.startDate));
        setText("planExpiryDate", formatDate(membership.endDate));
        setText("planDaysRemaining", `${membership.daysRemaining} days remaining`);

        messageEl.textContent = `✓ Your membership is active and verified in the database. Renews at ₹${membership.price}.`;
        messageEl.style.color = "var(--accent, #c4ff65)";
    } else if (isExpired) {
        statusBadge.textContent = "EXPIRED";
        statusBadge.className = "plan-status inactive";
        planNameEl.textContent = `${membership.planName || "Previous Plan"} (Expired)`;
        planDescEl.textContent = "Your membership period has ended. Select a tier below to renew access.";
        setText("planStartDate", formatDate(membership.startDate));
        setText("planExpiryDate", formatDate(membership.endDate));
        setText("planDaysRemaining", "Expired");

        messageEl.textContent = "Your membership has expired. Choose a plan below to renew.";
        messageEl.style.color = "var(--danger, #FF8296)";
    } else {
        statusBadge.textContent = "NO ACTIVE PLAN";
        statusBadge.className = "plan-status inactive";
        planNameEl.textContent = "No Plan Assigned";
        planDescEl.textContent = "Choose one of the training tiers below to activate your gym access.";
        setText("planStartDate", "None");
        setText("planExpiryDate", "Not assigned");
        setText("planDaysRemaining", "0 days");

        messageEl.textContent = "Select a plan below to activate your membership and unlock all gym facilities.";
        messageEl.style.color = "var(--muted, #a0aaa9)";
    }

    // Update buttons in plan catalog
    document.querySelectorAll(".tier-btn").forEach(button => {
        const planCode = button.dataset.plan;
        if (isActive && membership.plan === planCode) {
            button.textContent = "Current Active Plan ✓";
            button.classList.add("current");
            button.disabled = true;
        } else {
            button.textContent = `Choose ${planCode.charAt(0) + planCode.slice(1).toLowerCase()} Plan ↗`;
            button.classList.remove("current");
            button.disabled = false;
        }
    });
}

// Plan Selection / Activation Listener
document.querySelectorAll(".tier-btn").forEach(button => {
    button.addEventListener("click", async event => {
        const btn = event.currentTarget;
        const plan = btn.dataset.plan;
        const durationMonths = Number(btn.dataset.months);
        const price = Number(btn.dataset.price);

        const originalText = btn.textContent;
        btn.disabled = true;
        btn.textContent = "Activating...";

        const messageEl = document.getElementById("membershipMessage");
        messageEl.textContent = "Connecting to database and activating plan...";

        try {
            const updated = await updateMembership(memberId, {
                plan,
                durationMonths,
                price
            });

            renderMembershipCard(updated);
            messageEl.textContent = `🎉 Successfully upgraded to ${updated.planName}! Valid until ${formatDate(updated.endDate)}.`;
            messageEl.style.color = "var(--accent, #c4ff65)";

            // Smooth scroll to top membership card
            document.querySelector(".membership-plan-card")?.scrollIntoView({ behavior: "smooth" });
        } catch (error) {
            console.error("Failed to activate plan:", error);
            messageEl.textContent = error.message || "Failed to update membership plan. Please try again.";
            messageEl.style.color = "var(--danger, #FF8296)";
            btn.disabled = false;
            btn.textContent = originalText;
        }
    });
});
