import {LitElement} from 'lit';

export declare class InfoTab extends LitElement {
    private _devTools;
    private serverInfo;

    render(): import("lit").TemplateResult<1>;

    handleMessage(message: any): boolean;

    copyInfoToClipboard(): void;

    protected createRenderRoot(): Element | ShadowRoot;
}
