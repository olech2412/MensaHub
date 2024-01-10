import {LitElement} from 'lit';
import {VaadinDevTools} from './vaadin-dev-tools.js';

export declare class VaadinDevToolsLog extends LitElement {
    _devTools: VaadinDevTools;

    activate(): void;

    render(): import("lit").TemplateResult<1>;

    protected createRenderRoot(): Element | ShadowRoot;
}
