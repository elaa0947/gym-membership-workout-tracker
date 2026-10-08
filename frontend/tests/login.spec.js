import { test, expect } from "@playwright/test";

test("member can login with valid credentials", async ({ page }) => {

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
});