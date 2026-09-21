const {defineConfig} = require("cypress");

module.exports = defineConfig({
    video: true,
    videoCompression: true,
    screenshotOnRunFailure: true,
    reporter: "cypress-mochawesome-reporter",

    reporterOptions: {
        charts: true,
        reportPageTitle: "Supplier Delivery E2E Report",
        embeddedScreenshots: true,
        inlineAssets: true,
        videoOnFailOnly: false,
        saveAllAttempts: false,
    },
    e2e: {
        baseUrl: "http://localhost:8080",
        /*setupNodeEvents(on, config) {
            require("cypress-mochawesome-reporter/plugin")(on);
        },*/
        setupNodeEvents(on, config) {
            if (config.env.reportFormat !== "junit") {
                require("cypress-mochawesome-reporter/plugin")(on);
            }

            return config;
        },
    },
});
