import { useEffect, useState } from 'react';
import useTokenPair from './hooks/useTokenPair'
import Login from './components/Login/Login';
import CitationLists from './components/CitationLists/CitationLists';
import type { CitationList, TokenPair } from './types/types.ts'
import './App.css'
import { CitationsEditor } from './components/CitationsEditor/CitationsEditor.tsx';


const App = () => {
    const { tokenPair, setTokenPair, isAuthenticated, clear } = useTokenPair();
    const [selectedCitationList, setSelectedCitationList] = useState<CitationList | null>(null);

    // De-select the citation list if not authenticated.
    useEffect(() => {
        setSelectedCitationList(null);
    }, [isAuthenticated]);

    const handleLogout = async (clear: () => void, tokenPair: TokenPair) => {
        try {
            const headers: Record<string, string> = {"Content-Type": "application/json"};
            if(tokenPair?.accessToken) {
                headers["Authorization"] = `Bearer ${tokenPair.accessToken}`;
            }

            const res = await fetch('http://localhost:8090/api/auth/logout', {
                method: "POST",
                headers: headers,
                body: JSON.stringify({
                    refreshToken: tokenPair.refreshToken
                })
            });
            if(!res.ok) console.log(res.status);
        
        } catch (e) {
            console.error(e);
        } finally {
            clear();
        }
    }

    if(!isAuthenticated) {
        return <Login setTokenPair={setTokenPair} clear={clear} />
    }

    return (
        <div className="app-wrapper">
            <button className="logoutBtn" onClick={() => handleLogout(clear, tokenPair)}>Logout</button>
            <aside className="citation-lists-wrapper">
                <CitationLists tokenPair={tokenPair} setTokenPair={setTokenPair} clear={clear} setSelectedCitationList={setSelectedCitationList} />
            </aside>
            <main className='main-content'>
                {
                    selectedCitationList == null
                      ? <h2>No citations.</h2>
                      : <CitationsEditor 
                          tokenPair={tokenPair} 
                          setTokenPair={setTokenPair} 
                          clear={clear} 
                          selectedCitationList={selectedCitationList} 
                        />
                }
            </main>
        </div>
    );
};

export default App
