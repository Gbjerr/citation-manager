import { Cite, plugins } from '@citation-js/core';
import '@citation-js/plugin-csl';
import '@citation-js/plugin-bibtex';
import { SEP, type Citation, type ReferenceStyleType } from '../types/types';

type CSLDate = { 'date-parts': number[][] };

type CSLName = { given?: string; family?: string };

function parseAuthors(authorsRaw: string | undefined): CSLName[] {
    if (!authorsRaw) return [];

    // Handles multiple authors by using separator ";".
    const authorStrings = authorsRaw
        .split(/\s*[;]\s*/)
        .map(s => s.trim())
        .filter(Boolean);

    return authorStrings.map(full => {
        const parts = full.split(/\s+/).filter(Boolean);
        if (parts.length === 1) return { family: parts[0] };
        const family = parts.pop();
        const given = parts.join(' ');
        return { given, family };
    });
}

function parseIssuedDate(
    dateRaw: Date | string | undefined,
): CSLDate | undefined {
    if (!dateRaw) return undefined;

    const d = typeof dateRaw === 'string' ? new Date(dateRaw) : dateRaw;
    if (Number.isNaN(d.getTime?.() ?? NaN)) return undefined;

    // CSL expects one array per date: [[YYYY, M, D]]
    return { 'date-parts': [[d.getFullYear(), d.getMonth() + 1, d.getDate()]] };
}

/**
 * Utility to retrieve the formatted reference list given the reference style and citations.
 */
export async function formatCitations(
    citationList: Citation[],
    referenceStyle: ReferenceStyleType
): Promise<string> {
    const defaultCitationJsRefs = ['harvard', 'apa', 'vancouver'];

    // Add requested reference template if not included.
    if (!defaultCitationJsRefs.includes(referenceStyle)) {
        const ieeeXml = await fetch(`/styles/${referenceStyle}.csl`).then(r => {
            if (!r.ok)
                throw new Error(
                    `Failed to load ${referenceStyle} CSL: ${r.status} ${r.statusText}`,
                );
            return r.text();
        });

        plugins.config.get('@csl').templates.add(referenceStyle, ieeeXml);
    }

    const items = citationList.map((citation) => ({
        id: String(citation.id),
        type: 'article-journal',
        title: citation.title || '',
        author: parseAuthors(citation.authors),
        issued: parseIssuedDate(citation.date),
        publisher: citation.publisher || undefined,
        DOI: citation.doi || undefined,
        URL: citation.url || undefined,
        ISBN: citation.isbn || undefined,
    }));

    const citations = new Cite(items);

    return citations
        .format('bibliography', {
            format: 'text',
            template: referenceStyle,
            lang: 'en-US',
            prepend(entry: { id?: string }) {
                return `${entry.id}${SEP} `;
            },
        })
        .trim();
}
