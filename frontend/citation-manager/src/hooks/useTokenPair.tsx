import { useState, useCallback, useEffect } from "react";

const ACCESS_KEY = "accessToken";
const REFRESH_KEY = "refreshToken";

/**
 * Defines hook for managin access and refresh tokens.
 */
export default function useTokenPair() {
    const [tokenPair, setTokenPairState] = useState({
        accessToken: localStorage.getItem(ACCESS_KEY),
        refreshToken: localStorage.getItem(REFRESH_KEY)
    })
    const [loading, setLoading] = useState(false);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    // Store the token pair in local storage and state.
    const persist = useCallback((pair: { accessToken: string; refreshToken: string; }) => {
        if(pair?.accessToken !== "") localStorage.setItem(ACCESS_KEY, pair.accessToken);
        else localStorage.removeItem(ACCESS_KEY);
        if(pair?.refreshToken !== "") localStorage.setItem(REFRESH_KEY, pair.refreshToken);
        else localStorage.removeItem(REFRESH_KEY);
        setTokenPairState({
            accessToken: pair?.accessToken,
            refreshToken: pair?.refreshToken
        });
    }, [setTokenPairState])

    // Clears tokens from local storage.
    const clear = useCallback(() => {
        persist({ accessToken: "", refreshToken: "" });
    }, [persist]);

    // Try rotating the refresh token.
    const tryRefreshToken = useCallback(async (refreshToken: string) => {
        try {
            const res = await fetch("http://localhost:8090/api/auth/refresh", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ refreshToken }),
            });
            if (!res.ok) throw new Error("refresh failed");
            const data = await res.json();
            persist(data);
            setIsAuthenticated(true);
            return data;
        } catch (error) {
            clear();
            return null;
        }
    }, [persist, clear]);

    
    useEffect(() => {
        (async () => {
            const accessToken = localStorage.getItem(ACCESS_KEY);
            const refreshToken = localStorage.getItem(REFRESH_KEY);

            if (accessToken) {
                setTokenPairState({ accessToken, refreshToken });
                setIsAuthenticated(true);
                setLoading(false);
                return;
            }

            if (refreshToken) {
                await tryRefreshToken(refreshToken);
                setLoading(false);
                return;
            }

            clear();
            setLoading(false);
        })();
    }, [tryRefreshToken, clear]);

    const setTokenPair = useCallback((pair: { accessToken: string; refreshToken: string }) => {
        persist(pair);
        setIsAuthenticated(Boolean(pair?.accessToken));
    }, [persist]);

  return { tokenPair, setTokenPair, isAuthenticated, loading, clear };
}