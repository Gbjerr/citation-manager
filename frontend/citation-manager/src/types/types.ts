export interface TokenPair {
    accessToken: string;
    refreshToken: string;
}

export interface LoginProps {
    setTokenPair: (pair: TokenPair) => void;
    clear: () => void;
}

export interface CitationList {
    id: number;
    title: string;
}

interface Citation {
    id: number;
    title: string;
    authors: string;
    publisher: string;
    date: Date;
    doi: string;
    url: string;
}

export interface UserCitation extends Citation {
    isbn: string;
    position: number;
    citationListId: number;
}

export interface SemanticCitation extends Citation {
    abstractText: string;
}

export interface CitationListSelectionProps {
    selectedCitationList: CitationList;
    setSelectedCitationList: (list: CitationList) => void;
}


/**
 * The available reference style names that should be a 1 to 1 match 
 * to the named .csl file found in /public/styles/ e.g. 'ieee' -> /public/styles/ieee.csl
 */
export const REFERENCE_STYLE_VALUES = [
    'ieee',
    'apa',
    'vancouver',
    'modern-language-association',
    'chicago-author-date-17th-edition',
    'american-chemical-society',
    'apa-6th-edition'
] as const;

export type ReferenceStyleType = (typeof REFERENCE_STYLE_VALUES)[number];

// Separator for citation entity ID and formatted text in bibliographic entries.
export const SEP: string = '\x1F'

export const EMPTY_CITATION: UserCitation = {
    id: -1,
    title: '',
    authors: '',
    publisher: '',
    date: new Date(), // "YYYY-MM-DD"
    doi: '',
    url: '',
    isbn: '',
    position: -1,
    citationListId: -1
};