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

export interface Citation {
    id: number;
    title: string;
    authors: string;
    publisher: string;
    date: Date;
    doi: string;
    url: string;
    isbn: string;
}

export interface CitationListSelectionProps {
    selectedCitationList: CitationList;
    setSelectedCitationList: (list: CitationList) => void;
}

// TODO: Supports only three styles, extend number of styles. 
export const REFERENCE_STYLE_VALUES = [
    'ieee',
    'apa',
    'vancouver',
    // 'mla',
    // 'chicago',
    // 'ama',
    // 'acs',
    // 'cse',
    // 'harvard',
    // 'bluebook',
] as const;

export type ReferenceStyleType = (typeof REFERENCE_STYLE_VALUES)[number];