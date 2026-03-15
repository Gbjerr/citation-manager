declare module '@citation-js/core' {
    export class Cite {
        constructor(input?: unknown, opts?: unknown);
        format(type: string, opts?: unknown): string;
        get(options?: unknown): unknown;
        add(input: unknown, opts?: unknown): void;
        static plugins: any;
        static util: any;
    }

    export class plugins {
        static config: any;
    }
}
