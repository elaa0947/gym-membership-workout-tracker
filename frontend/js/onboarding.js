import { completeOnboarding } from "./api.js";

const memberId =
    localStorage.getItem("memberId");

const onboardingForm =
    document.getElementById("onboardingForm");

const heightInput =
    document.getElementById("height");

const weightInput =
    document.getElementById("weight");

const fitnessGoalInput =
    document.getElementById("fitnessGoal");

const formMessage =
    document.getElementById("formMessage");


function showMessage(message, type) {

    formMessage.textContent = message;

    formMessage.className =
        `form-message ${type}`;
}


onboardingForm.addEventListener(
    "submit",
    async (event) => {

        event.preventDefault();

        if (!memberId) {

            showMessage(
                "Session not found. Please login again.",
                "error"
            );

            return;
        }

        const height =
            Number(heightInput.value);

        const weight =
            Number(weightInput.value);

        const fitnessGoal =
            fitnessGoalInput.value;

        if (height <= 0) {

            showMessage(
                "Enter a valid height.",
                "error"
            );

            return;
        }

        if (weight <= 0) {

            showMessage(
                "Enter a valid weight.",
                "error"
            );

            return;
        }

        if (!fitnessGoal) {

            showMessage(
                "Please select your fitness goal.",
                "error"
            );

            return;
        }

        try {

            await completeOnboarding(
                memberId,
                {
                    height,
                    weight,
                    fitnessGoal
                }
            );

            showMessage(
                "Profile setup completed!",
                "success"
            );

            setTimeout(() => {

                window.location.href =
                    "./dashboard.html";

            }, 700);

        } catch (error) {

            showMessage(
                error.message ||
                "Unable to save your information.",
                "error"
            );
        }
    }
);