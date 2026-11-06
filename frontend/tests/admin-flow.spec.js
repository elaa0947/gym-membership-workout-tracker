import { test, expect } from "@playwright/test";

test.describe("Admin Portal & Management Flow", () => {

    test("admin login page renders with dedicated management console styling", async ({ page }) => {
        await page.goto("http://127.0.0.1:5500/frontend/pages/admin/login.html");
        await expect(page.locator(".admin-badge-pill")).toBeVisible();
        await expect(page.locator("h1")).toContainText("Administrator");

        const submitBtn = page.locator("#submitBtn");
        await expect(submitBtn).toBeVisible();
        await expect(submitBtn).toHaveCSS("background-color", "rgb(196, 255, 101)");
    });

    test("administrator can login, inspect live metrics, and switch between operations tabs", async ({ page }) => {
        await page.goto("http://127.0.0.1:5500/frontend/pages/admin/login.html");
        await page.locator("#adminEmail").fill("admin@corelygym.com");
        await page.locator("#adminPassword").fill("AdminPass123!");
        await page.locator("#submitBtn").click();

        await expect(page.locator("#formMessage")).toContainText("Access granted");
        await page.waitForURL(/.*admin\/dashboard\.html/, { timeout: 6000 });

        // Verify operations header and live indicator
        await expect(page.locator(".admin-pill")).toBeVisible();
        await expect(page.locator("#statTotalMembers")).toBeVisible();

        // Verify tabs work
        const membersTab = page.locator(".admin-tab[data-tab='tab-members']");
        await membersTab.click();
        await expect(page.locator("#tab-members")).toBeVisible();
        await expect(page.locator("#membersTableBody")).toBeVisible();

        const attendanceTab = page.locator(".admin-tab[data-tab='tab-attendance']");
        await attendanceTab.click();
        await expect(page.locator("#tab-attendance")).toBeVisible();

        const workoutsTab = page.locator(".admin-tab[data-tab='tab-workouts']");
        await workoutsTab.click();
        await expect(page.locator("#tab-workouts")).toBeVisible();
    });

    test("regular members cannot log into admin console", async ({ page }) => {
        await page.goto("http://127.0.0.1:5500/frontend/pages/admin/login.html");
        await page.locator("#adminEmail").fill("greentest@example.com");
        await page.locator("#adminPassword").fill("Password123!");
        await page.locator("#submitBtn").click();

        await expect(page.locator("#formMessage")).toContainText("restricted to authorized gym administrators");
    });
});
