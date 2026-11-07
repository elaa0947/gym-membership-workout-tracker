import { test, expect } from "@playwright/test";

test("member can login with valid credentials", async ({ page }, testInfo) => {

    await page.route("http://127.0.0.1:8080/api/**", async route => {
        const url = new URL(route.request().url());
        if (url.pathname === "/api/members/login") {
            await route.fulfill({
                status: 200,
                contentType: "application/json",
                body: JSON.stringify({ id: 7, name: "Test Member" })
            });
        } else if (url.pathname === "/api/members/profile/7") {
            await route.fulfill({
                status: 200,
                contentType: "application/json",
                body: JSON.stringify({ id: 7, name: "Test Member", onboardingCompleted: true })
            });
        } else {
            await route.fulfill({ status: 404, body: "Not found" });
        }
    });

    await page.goto(
        "http://127.0.0.1:5500/frontend/pages/login.html"
    );

    await expect(
        page.locator("#loginForm")
    ).toBeVisible();

    await page
        .locator("#email")
        .fill("test2@test.com");

    await page
        .locator("#password")
        .fill("password123");

    await page
        .locator("#loginForm button[type='submit']")
        .click();

    await expect(
        page.locator("#formMessage")
    ).toContainText("Welcome back");

    await expect(
        page.locator("#formMessage")
    ).toHaveClass(/success/);

    await expect(page).toHaveURL(/member\/dashboard\.html$/);

    await page.screenshot({
        path: testInfo.outputPath("login-test-result.png"),
        fullPage: true
    });
});
