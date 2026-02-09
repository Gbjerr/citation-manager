import type { TokenPair, LoginProps } from '../types/types.ts';

let refreshPromise: Promise<TokenPair | null> | null = null;

const refreshTokenPair = async (tokenPair: TokenPair, { setTokenPair, clear }: LoginProps) => {
    if (refreshPromise) return refreshPromise;
    if (!tokenPair?.refreshToken) {
        clear();
        return null;
    }

    refreshPromise = (async () => {
        try {
            const refreshRes = await fetch('http://localhost:8090/api/auth/refresh', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${tokenPair.refreshToken}`
                }
            });

            if (!refreshRes.ok) {
                console.error(refreshRes);
                clear();
                return null;
            }

            const updatedTokenPair: TokenPair = await refreshRes.json();
            setTokenPair(updatedTokenPair);
            return updatedTokenPair;
        } catch (error) {
            console.error(error);
            clear();
            return null;
        } finally {
            refreshPromise = null;
        }
    })();

    return refreshPromise;
};

export default async function authorizedFetch(tokenPair: TokenPair, url: string, method: string, {setTokenPair, clear}: LoginProps, body?: Record<string, string>) {

    const doFetch = async (tokenPair: TokenPair) => {
        const headers: Record<string, string> = { 'Content-Type': 'application/json' };
        if (tokenPair?.accessToken) {
            headers['Authorization'] = `Bearer ${tokenPair.accessToken}`;
        }

        return fetch(url, {
            method: method,
            headers: headers,
            body: body ? JSON.stringify(body) : undefined
        })
    };

    let response = await doFetch(tokenPair);

    // Use refresh token and re-do the request when access token has expired. 
    if(response.status == 401) {
        const updatedTokenPair = await refreshTokenPair(tokenPair, { setTokenPair, clear });
        if (!updatedTokenPair) return;
        response = await doFetch(updatedTokenPair);
    }

    return response;
}