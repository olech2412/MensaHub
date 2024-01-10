declare class ThemePreview {
    private _localClassNameMap;
    private ensureStylesheet;

    private _stylesheet?;

    get stylesheet(): CSSStyleSheet;

    add(css: string): void;

    clear(): void;

    previewLocalClassName(element?: HTMLElement, className?: string): void;
}

export declare const themePreview: ThemePreview;
export {};
