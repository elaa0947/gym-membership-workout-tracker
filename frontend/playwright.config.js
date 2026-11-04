const { defineConfig } = require("@playwright/test");

module.exports = defineConfig({
    testDir: "./tests",
    use: {
        baseURL: "http://127.0.0.1:5500"
    },
    webServer: {
        command: "python3 -m http.server 5500 --directory ..",
        url: "http://127.0.0.1:5500/frontend/pages/login.html",
        reuseExistingServer: !process.env.CI,
        timeout: 30_000
    }
});
