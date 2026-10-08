const API_BASE_URL = "http://localhost:8080/api";


export async function registerMember(memberData) {

    const response = await fetch(
        `${API_BASE_URL}/members/register`,
        {
            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(memberData)
        }
    );

    const data = await response.json();

    if (!response.ok) {

        throw new Error(
            data.message ||
            "Registration failed."
        );
    }

    return data;
}


export async function loginMember(loginData) {

    const response = await fetch(
        `${API_BASE_URL}/members/login`,
        {
            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(loginData)
        }
    );

    const data = await response.json();

    if (!response.ok) {

        throw new Error(
            data.message ||
            "Invalid email or password."
        );
    }

    return data;
}


export async function getMemberProfile(memberId) {

    const response = await fetch(
        `${API_BASE_URL}/members/profile/${memberId}`,
        {
            method: "GET"
        }
    );

    const data = await response.json();

    if (!response.ok) {

        throw new Error(
            data.message ||
            "Unable to load member profile."
        );
    }

    return data;
}


export async function updateMemberProfile(
    memberId,
    profileData
) {

    const response = await fetch(
        `${API_BASE_URL}/members/profile/${memberId}`,
        {
            method: "PUT",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(profileData)
        }
    );

    const data = await response.json();

    if (!response.ok) {

        throw new Error(
            data.message ||
            "Unable to update member profile."
        );
    }

    return data;
}export async function completeOnboarding(
    memberId,
    onboardingData
) {
    const response = await fetch(
        `${API_BASE_URL}/members/onboarding/${memberId}`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(onboardingData)
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw new Error(
            data.message ||
            "Unable to save onboarding information."
        );
    }

    return data;
}