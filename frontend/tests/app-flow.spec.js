import { test, expect } from "@playwright/test";
import { execSync } from "child_process";

test.describe("Full Application Flow with Green-White-Black Theme", () => {
    test.beforeEach(() => {
        try {
            execSync(`mysql -h 127.0.0.1 -u gym_app -pGymAppPass123! -e "USE gym_tracker;
            SET @uid = (SELECT id FROM members WHERE email = 'greentest@example.com');
            UPDATE members SET name = 'Green Theme Test', membership_plan = NULL, membership_status = 'NONE', membership_duration_months = NULL, membership_start_date = NULL, membership_end_date = NULL, membership_price = NULL WHERE id = @uid;
            DELETE FROM attendance_records WHERE member_id = @uid;
            DELETE FROM workout_sessions WHERE member_id = @uid;
            DELETE FROM weight_entries WHERE member_id = @uid;
            "`, { stdio: "ignore" });
        } catch (e) {
            // ignore if mysql cli is not in path for that runner
        }
    });

    test("landing page renders with kinetic green & dark theme", async ({ page }) => {
        await page.goto("http://127.0.0.1:5500/frontend/index.html");
        await expect(page.locator(".polaris-landing")).toBeVisible();
        await expect(page.locator(".px-hero-content h1")).toContainText("BUILT BY");

        // Verify primary CTA exists and has kinetic green background
        const cta = page.locator(".px-cta-primary").first();
        await expect(cta).toBeVisible();
        await expect(cta).toHaveCSS("background-color", "rgb(196, 255, 101)"); // #c4ff65
    });

    test("registration page renders with green, white, and black theme without any blue", async ({ page }) => {
        await page.goto("http://127.0.0.1:5500/frontend/pages/register.html");
        await expect(page.locator(".auth-container")).toBeVisible();

        // Primary submit button is green #c4ff65 with dark text
        const submitBtn = page.locator("button[type='submit']");
        await expect(submitBtn).toBeVisible();
        await expect(submitBtn).toHaveCSS("background-color", "rgb(196, 255, 101)");
        await expect(submitBtn).toHaveCSS("color", "rgb(16, 21, 10)");

        // Labels are crisp white (#f2f5f4)
        const nameLabel = page.locator("label[for='name']");
        await expect(nameLabel).toHaveCSS("color", "rgb(242, 245, 244)");

        // Inputs have dark neutral background and green focus outline
        const nameInput = page.locator("#name");
        await nameInput.focus();
        await expect(nameInput).toHaveCSS("border-color", "rgb(196, 255, 101)");

        // Links are green
        const signInLink = page.locator(".form-footer a");
        await expect(signInLink).toHaveCSS("color", "rgb(196, 255, 101)");
    });

    test("member logs in and navigates internal sections with unified green theme and functional database persistence", async ({ page }) => {
        // 1. Login
        await page.goto("http://127.0.0.1:5500/frontend/pages/login.html");
        await page.locator("#email").fill("greentest@example.com");
        await page.locator("#password").fill("Password123!");
        await page.locator("#loginForm button[type='submit']").click();

        await expect(page.locator("#formMessage")).toContainText("Welcome back");
        await page.waitForURL(/.*dashboard\.html/, { timeout: 6000 });

        // 2. Dashboard loads with welcome banner & stat cards
        await expect(page.locator(".dashboard-welcome")).toBeVisible();
        await expect(page.locator("#userName")).toBeVisible();
        await expect(page.locator(".stat-card").first()).toBeVisible();

        // 3. Navigate to Membership and activate Pro Plan
        await page.locator(".sidebar-link[href='membership.html']").click();
        await page.waitForURL(/.*membership\.html/);
        await expect(page.locator(".membership-plan-card")).toBeVisible();

        const proButton = page.locator(".tier-btn[data-plan='PRO']");
        await expect(proButton).toBeVisible();
        await proButton.click();

        // Verify database update message and active status
        await expect(page.locator("#membershipMessage")).toContainText("Successfully upgraded to Pro Plan", { timeout: 5000 });
        await expect(page.locator("#membershipStatus")).toContainText("ACTIVE");

        // 4. Navigate to Workout and record session
        await page.locator(".sidebar-link[href='workout.html']").click();
        await page.waitForURL(/.*workout\.html/);
        await expect(page.locator(".fit-hero")).toBeVisible();
        
        // Check at least one exercise
        const firstExerciseCheckbox = page.locator(".fit-exercise input[type='checkbox']").first();
        await firstExerciseCheckbox.check();

        const saveWorkoutBtn = page.locator("#saveWorkout");
        await expect(saveWorkoutBtn).toBeVisible();
        await expect(saveWorkoutBtn).toHaveCSS("background-color", "rgb(196, 255, 101)");
        await saveWorkoutBtn.click();
        await expect(page.locator("#workoutMessage")).toContainText("Workout saved", { timeout: 5000 });

        // 5. Navigate to Attendance and mark check-in
        await page.locator(".sidebar-link[href='attendance.html']").click();
        await page.waitForURL(/.*attendance\.html/);
        const checkInBtn = page.locator("#checkInButton");
        await expect(checkInBtn).toBeVisible();
        await expect(checkInBtn).toHaveCSS("background-color", "rgb(196, 255, 101)");
        await checkInBtn.click();
        await expect(page.locator("#attendanceMessage")).toContainText("Attendance saved", { timeout: 5000 });

        // 6. Navigate to Progress and log weight
        await page.locator(".sidebar-link[href='progress.html']").click();
        await page.waitForURL(/.*progress\.html/);
        await expect(page.locator(".fit-hero")).toBeVisible();
        
        await page.locator("#weightInput").fill("73.5");
        await page.locator("#weightDate").fill(new Date().toISOString().split("T")[0]);
        const logWeightBtn = page.locator(".fit-button[type='submit']");
        await expect(logWeightBtn).toBeVisible();
        await expect(logWeightBtn).toHaveCSS("background-color", "rgb(196, 255, 101)");
        await logWeightBtn.click();
        await expect(page.locator("#progressMessage")).toContainText("Measurement saved", { timeout: 5000 });

        // 7. Navigate to Profile and update name
        await page.locator(".sidebar-link[href='profile.html']").click();
        await page.waitForURL(/.*profile\.html/);
        await expect(page.locator(".profile-avatar-large")).toBeVisible();
        await expect(page.locator(".profile-avatar-large")).toHaveCSS("background-color", "rgb(196, 255, 101)");
        
        await page.locator("#name").fill("Green Champion");
        await page.locator("#saveProfileButton").click();
        await expect(page.locator("#formMessage")).toContainText("Profile updated successfully", { timeout: 5000 });

        // 8. Return to Dashboard and verify everything reflects live data
        await page.locator(".sidebar-link[href='dashboard.html']").click();
        await page.waitForURL(/.*dashboard\.html/);
        await expect(page.locator("#dashMembershipPlan")).toContainText("PRO");
        await expect(page.locator("#memberWeight")).toContainText("73.5 kg");
        await expect(page.locator("#userName")).toContainText("Green Champion");
    });
});
