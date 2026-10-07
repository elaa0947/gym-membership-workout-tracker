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