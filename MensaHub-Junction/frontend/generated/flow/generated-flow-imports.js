import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/login/theme/lumo/vaadin-login-form.js';
import '@vaadin/vertical-layout/theme/lumo/vaadin-vertical-layout.js';
import '@vaadin/horizontal-layout/theme/lumo/vaadin-horizontal-layout.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
    const pending = [];
    if (key === 'c18980db3e07b2f1c555f7d350c021b3959de851da80a2437b575bfac36ae993') {
        pending.push(import('./chunks/chunk-6b7f6c0e1d15a3c8c4eff714105758f1cfb322dc1121ceed09174b91e6491906.js'));
    }
    if (key === '83cd629d35e2293f6d8a4e00edfe6e78eb20d9fca23997ffe7f047dd78127b0d') {
        pending.push(import('./chunks/chunk-91d0dd5d31b55b092298f6eb5e2221faa3f6fc403d2c9d6ba375f03d5fb8ff36.js'));
    }
    if (key === '38fb35f4bd5bfc2b56632dfc15876efe5b99d893d1b288ae0dafa17fb2b658cf') {
        pending.push(import('./chunks/chunk-f803df643c07aa37eee57a440b68a91abb3cd3408a72bcc573024c04162131e4.js'));
    }
    if (key === '8908721ea975f12ec526046bab7e204e21a7975ad0e278fc977b8ebeb698369b') {
        pending.push(import('./chunks/chunk-fbac6833a25c00e5b4a187e170e878d64829a09c06bb603f374b091ec5391988.js'));
    }
    if (key === '9bf9f217fe731bdbd62e1250e12e76e49b645599b2afba0137f9081546c75360') {
        pending.push(import('./chunks/chunk-f9d7777a5eac2af16232b2217c96f127fffc5bd4c542735413a4c6b0b5265488.js'));
    }
    if (key === '6497bbac10772a5ed307d348e0a2c4dd3af4ad77a5fa76814d79c81f8fafc288') {
        pending.push(import('./chunks/chunk-a5ba281ebd4d7e41d8ed11737d7b25f37e5b0aa61575341f820674407b8ae326.js'));
    }
    return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;