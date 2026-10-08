import {
    getMemberProfile,
    updateMemberProfile
} from "./api.js";


// =========================================================
// SESSION
// =========================================================

const memberId = localStorage.getItem("memberId");

console.log("PROFILE.JS LOADED");
console.log("MEMBER ID:", memberId);


// =========================================================
// DOM ELEMENTS
// =========================================================

const profileForm =
    document.getElementById("profileForm");

const formMessage =
    document.getElementById("formMessage");

const nameInput =
    document.getElementById("name");

const emailInput =
    document.getElementById("email");

const phoneInput =
    document.getElementById("phone");

const memberIdInput =
    document.getElementById("memberId");

const createdAtInput =
    document.getElementById("createdAt");

const profileAvatar =
    document.getElementById("profileAvatar");

const resetButton =
    document.getElementById("resetButton");


// =========================================================
// PROFILE STATE
// =========================================================

let originalProfile = null;


// =========================================================
// MESSAGE
// =========================================================

function showMessage(message, type) {

    formMessage.textContent = message;

    formMessage.className =
        `form-message ${type}`;
}


// =========================================================
// AVATAR
// =========================================================

function updateAvatar(name) {

    const initial =
        name?.trim().charAt(0).toUpperCase() || "M";

    profileAvatar.textContent = initial;
}


// =========================================================
// DATE FORMAT
// =========================================================

function formatDate(dateValue) {

    if (!dateValue) {
        return "";
    }

    return new Date(dateValue).toLocaleDateString(
        "en-IN",
        {
            day: "2-digit",
            month: "short",
            year: "numeric"
        }
    );
}


// =========================================================
// LOAD PROFILE
// =========================================================

async function loadProfile() {

    console.log("LOAD PROFILE STARTED");

    if (!memberId) {

        console.error(
            "No member ID found in localStorage."
        );

        showMessage(
            "No member session found. Please login again.",
            "error"
        );

        profileForm
            .querySelectorAll("input, button")
            .forEach(element => {
                element.disabled = true;
            });

        return;
    }


    try {

        console.log(
            "Requesting profile for member:",
            memberId
        );


        const member =
            await getMemberProfile(memberId);


        console.log(
            "PROFILE DATA RECEIVED:",
            member
        );


        // Store original data
        originalProfile = {
            name: member.name,
            email: member.email,
            phone: member.phone,
            id: member.id,
            createdAt: member.createdAt
        };


        // =================================================
        // DISPLAY DATABASE DATA
        // =================================================

        nameInput.value =
            member.name || "";

        emailInput.value =
            member.email || "";

        phoneInput.value =
            member.phone || "";

        memberIdInput.value =
            member.id || "";

        createdAtInput.value =
            formatDate(member.createdAt);


        // Update avatar
        updateAvatar(member.name);


        console.log(
            "PROFILE UI UPDATED"
        );

    } catch (error) {

        console.error(
            "PROFILE LOAD ERROR:",
            error
        );

        showMessage(
            error.message ||
            "Unable to load your profile.",
            "error"
        );
    }
}


// =========================================================
// UPDATE PROFILE
// =========================================================

profileForm.addEventListener(
    "submit",
    async (event) => {

        event.preventDefault();


        if (!profileForm.reportValidity()) {
            return;
        }


        const name =
            nameInput.value.trim();

        const phone =
            phoneInput.value.trim();


        try {

            console.log(
                "Updating member:",
                memberId
            );


            const updatedMember =
                await updateMemberProfile(
                    memberId,
                    {
                        name,
                        phone
                    }
                );


            console.log(
                "UPDATED PROFILE:",
                updatedMember
            );


            // Update stored original values
            originalProfile = {
                ...originalProfile,

                name: updatedMember.name,

                phone: updatedMember.phone
            };


            // Update UI
            nameInput.value =
                updatedMember.name;

            phoneInput.value =
                updatedMember.phone;


            // Update avatar
            updateAvatar(
                updatedMember.name
            );


            showMessage(
                "Profile updated successfully.",
                "success"
            );


        } catch (error) {

            console.error(
                "PROFILE UPDATE ERROR:",
                error
            );

            showMessage(
                error.message ||
                "Unable to update your profile.",
                "error"
            );
        }
    }
);


// =========================================================
// RESET CHANGES
// =========================================================

resetButton.addEventListener(
    "click",
    () => {

        if (!originalProfile) {
            return;
        }


        nameInput.value =
            originalProfile.name || "";

        phoneInput.value =
            originalProfile.phone || "";


        updateAvatar(
            originalProfile.name
        );


        showMessage(
            "Changes have been reset.",
            "info"
        );
    }
);


// =========================================================
// INITIALIZE
// =========================================================

loadProfile();