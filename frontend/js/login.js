import { loginMember } from "./api.js";


const form =
    document.getElementById("loginForm");

const emailInput =
    document.getElementById("email");

const passwordInput =
    document.getElementById("password");

const emailError =
    document.getElementById("emailError");

const passwordError =
    document.getElementById("passwordError");

const formMessage =
    document.getElementById("formMessage");


function setError(input, errorElement, message) {

    input.classList.add("input-error");

    errorElement.textContent = message;
}


function clearError(input, errorElement) {

    input.classList.remove("input-error");

    errorElement.textContent = "";
}


function clearErrors() {

    clearError(
        emailInput,
        emailError
    );

    clearError(
        passwordInput,
        passwordError
    );

    formMessage.className =
        "form-message";

    formMessage.textContent = "";
}


function validateForm() {

    let valid = true;

    const email =
        emailInput.value.trim();

    const password =
        passwordInput.value;


    const emailPattern =
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/;


    if (!email) {

        setError(
            emailInput,
            emailError,
            "Email is required."
        );

        valid = false;

    } else if (!emailPattern.test(email)) {

        setError(
            emailInput,
            emailError,
            "Enter a valid email address."
        );

        valid = false;

    }


    if (!password) {

        setError(
            passwordInput,
            passwordError,
            "Password is required."
        );

        valid = false;

    } else if (password.length < 8) {

        setError(
            passwordInput,
            passwordError,
            "Password must contain at least 8 characters."
        );

        valid = false;

    }


    return valid;
}


form.addEventListener(
    "submit",
    async (event) => {

        event.preventDefault();

        clearErrors();


        if (!validateForm()) {

            formMessage.className =
                "form-message error";

            formMessage.textContent =
                "Please correct the highlighted fields.";

            return;
        }


        const loginData = {

            email:
                emailInput.value.trim(),

            password:
                passwordInput.value

        };


        const submitButton =
            form.querySelector(
                "button[type='submit']"
            );


        submitButton.disabled = true;

        submitButton.innerHTML =
            "Signing in...";


        try {

            const member =
                await loginMember(loginData);


            // Store the logged-in member ID
            // so the profile page knows which
            // member's profile to load.
            localStorage.setItem(
                "memberId",
                member.id
            );


            formMessage.className =
                "form-message success";

            formMessage.textContent =
                `Welcome back, ${member.name}!`;


            form.reset();


        } catch (error) {

            formMessage.className =
                "form-message error";

            formMessage.textContent =
                error.message ||
                "Unable to connect to the server.";

        } finally {

            submitButton.disabled = false;

            submitButton.innerHTML =
                "Sign in <span>→</span>";

        }

    }
);