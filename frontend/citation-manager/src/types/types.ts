export interface TokenPair {
    accessToken: string,
    refreshToken: string
}

export interface LoginProps {
    setTokenPair: (pair: TokenPair) => void;
    clear: () => void;
}

export interface CitationList {
    id: number,
    title: string
}

export interface Citation {
    title: string,
    authors: string,
    publisher: string,
    date: string,
    doi: string,
    url: string,
    isbn: string
}

export interface CitationListSelectionProps {
    selectedCitationList: CitationList;
    setSelectedCitationList: (list: CitationList) => void;
}