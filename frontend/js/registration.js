import { registerMember } from "./api.js";


const form =
    document.getElementById("registrationForm");

const nameInput =
    document.getElementById("name");

const emailInput =
    document.getElementById("email");

const phoneInput =
    document.getElementById("phone");

const passwordInput =
    document.getElementById("password");

const confirmPasswordInput =
    document.getElementById("confirmPassword");

const formMessage =
    document.getElementById("formMessage");


/* =========================================
   Helper functions
========================================= */

function setError(input, errorId, message) {

    input.classList.add("input-error");

    document.getElementById(errorId).textContent =
        message;
}


function clearError(input, errorId) {

    input.classList.remove("input-error");

    document.getElementById(errorId).textContent =
        "";
}


function clearAllErrors() {

    clearError(nameInput, "nameError");

    clearError(emailInput, "emailError");

    clearError(phoneInput, "phoneError");

    clearError(passwordInput, "passwordError");

    clearError(
        confirmPasswordInput,
        "confirmPasswordError"
    );

    formMessage.className = "form-message";

    formMessage.textContent = "";
}


/* =========================================
   Validation
========================================= */

function validateForm() {

    let valid = true;


    const name =
        nameInput.value.trim();

    const email =
        emailInput.value.trim();

    const phone =
        phoneInput.value.trim();

    const password =
        passwordInput.value;

    const confirmPassword =
        confirmPasswordInput.value;


    /* Name */

    if (name.length < 2) {

        setError(
            nameInput,
            "nameError",
            "Please enter your full name."
        );

        valid = false;

    } else {

        clearError(
            nameInput,
            "nameError"
        );
    }


    /* Email */

    const emailPattern =
        /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailPattern.test(email)) {

        setError(
            emailInput,
            "emailError",
            "Enter a valid email address."
        );

        valid = false;

    } else {

        clearError(
            emailInput,
            "emailError"
        );
    }


    /* Phone */

    const phonePattern =
        /^[6-9]\d{9}$/;

    if (!phonePattern.test(phone)) {

        setError(
            phoneInput,
            "phoneError",
            "Enter a valid 10-digit phone number."
        );

        valid = false;

    } else {

        clearError(
            phoneInput,
            "phoneError"
        );
    }


    /* Password */

    if (password.length < 8) {

        setError(
            passwordInput,
            "passwordError",
            "Password must contain at least 8 characters."
        );

        valid = false;

    } else {

        clearError(
            passwordInput,
            "passwordError"
        );
    }


    /* Confirm password */

    if (password !== confirmPassword) {

        setError(
            confirmPasswordInput,
            "confirmPasswordError",
            "Passwords do not match."
        );

        valid = false;

    } else {

        clearError(
            confirmPasswordInput,
            "confirmPasswordError"
        );
    }


    return valid;
}


/* =========================================
   Form submission
========================================= */

form.addEventListener(
    "submit",
    async (event) => {

        event.preventDefault();

        clearAllErrors();


        if (!validateForm()) {

            formMessage.className =
                "form-message error";

            formMessage.textContent =
                "Please correct the highlighted fields.";

            return;
        }


        const memberData = {

            name: nameInput.value.trim(),

            email: emailInput.value.trim(),

            phone: phoneInput.value.trim(),

            password: passwordInput.value
        };


        const submitButton =
            form.querySelector("button[type='submit']");


        submitButton.disabled = true;

        submitButton.innerHTML =
            "Creating account...";


        try {

            /*
             * This calls the Spring Boot backend.
             *
             * The endpoint will be implemented by
             * Abishai on his backend branch.
             */

            const result =
                await registerMember(memberData);


            formMessage.className =
                "form-message success";

            formMessage.textContent =
                result.message ||
                "Account created successfully.";


            form.reset();


        } catch (error) {

            formMessage.className =
                "form-message error";

            formMessage.textContent =
                error.message ||
                "Unable to create account.";

        } finally {

            submitButton.disabled = false;

            submitButton.innerHTML =
                "Create account <span>→</span>";
        }
    }
);