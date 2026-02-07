export interface TokenPair {
    accessToken: string,
    refreshToken: string
}

export interface LoginProps {
    setTokenPair: (pair: TokenPair) => void;
    clear: () => void;
}